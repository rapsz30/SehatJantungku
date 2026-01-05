package com.example.sehatjantungku.ui.screens.diet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatjantungku.BuildConfig
import com.example.sehatjantungku.data.model.DietPlan
import com.example.sehatjantungku.utils.DietNotificationHelper // Pastikan import ini ada
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// --- MODEL DATA (TIDAK BERUBAH) ---
data class UserDietProgress(
    val userId: String = "",
    val dietId: String = "",
    val dietName: String = "",
    val currentDay: Int = 1,
    val tasks: Map<String, Boolean> = mapOf(
        "sarapan" to false, "siang" to false, "malam" to false, "camilan" to false, "air" to false
    ),
    val lastUpdated: Long = System.currentTimeMillis(),
    val lastLogDate: String = "", // Format: yyyy-MM-dd
    @get:PropertyName("isCompleted")
    val isCompleted: Boolean = false,
    val currentStreak: Int = 0
)

data class DietProgramState(
    val bloodPressure: String = "",
    val cholesterol: String = "",
    val healthConditions: List<String> = emptyList(),
    val foodPreference: String = "",
    val activityLevel: String = "",
    val commitment: String = "",
    val useCvdScore: Boolean = false,
    val cvdScoreVal: Double = 0.0,
    val cvdRiskCategory: String = ""
)

data class CalculationResult(
    val bestDietId: String,
    val bestDietName: String,
    val scores: Map<String, Double>
)

data class EarnedBadge(
    val id: String = "",
    val name: String = "",
    val dateEarned: Long = 0
)

data class InAppNotificationData(
    val title: String,
    val message: String,
    val time: String,
    val type: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

class DietProgramViewModel : ViewModel() {

    // State Form & Logic
    private val _state = MutableStateFlow(DietProgramState())
    val state: StateFlow<DietProgramState> = _state.asStateFlow()

    private val _cvdDataAvailable = MutableStateFlow(false)
    val cvdDataAvailable: StateFlow<Boolean> = _cvdDataAvailable.asStateFlow()

    // State Progress User
    private val _dietProgress = MutableStateFlow<UserDietProgress?>(null)
    val dietProgress: StateFlow<UserDietProgress?> = _dietProgress.asStateFlow()

    private val _isLoadingProgress = MutableStateFlow(true)
    val isLoadingProgress: StateFlow<Boolean> = _isLoadingProgress.asStateFlow()

    // State Content Diet Plan
    private val _fetchedDietPlan = MutableStateFlow<DietPlan?>(null)
    val fetchedDietPlan: StateFlow<DietPlan?> = _fetchedDietPlan.asStateFlow()

    private val _isLoadingPlan = MutableStateFlow(false)
    val isLoadingPlan: StateFlow<Boolean> = _isLoadingPlan.asStateFlow()

    // State Badge Profile
    private val _userBadges = MutableStateFlow<List<EarnedBadge>>(emptyList())
    val userBadges: StateFlow<List<EarnedBadge>> = _userBadges.asStateFlow()

    // Firebase & Gemini
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    init {
        checkLatestCVDData()
    }

    // Helper Tanggal & Waktu
    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    // ==========================================
    // 1. DATA DIET PLAN & PROGRESS
    // ==========================================

    fun resetData() {
        _dietProgress.value = null
        _state.value = DietProgramState()
        _cvdDataAvailable.value = false
        _fetchedDietPlan.value = null
        _userBadges.value = emptyList()
        _isLoadingProgress.value = false
    }

    fun loadUserDietProgress() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            resetData()
            return
        }
        val userId = currentUser.uid

        viewModelScope.launch {
            _isLoadingProgress.value = true
            _dietProgress.value = null

            try {
                val doc = db.collection("users").document(userId)
                    .collection("diet_program").document("active_diet")
                    .get().await()

                if (doc.exists()) {
                    val progress = doc.toObject(UserDietProgress::class.java)
                    _dietProgress.value = progress
                } else {
                    _dietProgress.value = null
                }
                checkLatestCVDData()
            } catch (e: Exception) {
                e.printStackTrace()
                _dietProgress.value = null
            } finally {
                _isLoadingProgress.value = false
            }
        }
    }

    fun fetchDietPlanFromFirebase(dietId: String) {
        viewModelScope.launch {
            _isLoadingPlan.value = true
            _fetchedDietPlan.value = null
            try {
                val doc = db.collection("diet_plans").document(dietId).get().await()
                if (doc.exists()) {
                    _fetchedDietPlan.value = doc.toObject(DietPlan::class.java)
                }
            } catch (e: Exception) { e.printStackTrace() } finally { _isLoadingPlan.value = false }
        }
    }

    // --- UPDATED: MENERIMA CONTEXT UNTUK NOTIFIKASI ---
    fun startNewDiet(dietId: String, dietName: String, context: Context, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        val newProgress = UserDietProgress(
            userId = userId,
            dietId = dietId,
            dietName = dietName,
            currentDay = 1,
            isCompleted = false,
            currentStreak = 0,
            lastLogDate = ""
        )

        viewModelScope.launch {
            try {
                // 1. Simpan ke Firestore
                db.collection("users").document(userId)
                    .collection("diet_program").document("active_diet")
                    .set(newProgress).await()

                // 2. [BARU] Jadwalkan Notifikasi (Alarm)
                // Kita gunakan data dari _fetchedDietPlan yang (seharusnya) sudah dimuat di layar sebelumnya
                val currentPlan = _fetchedDietPlan.value
                if (currentPlan != null) {
                    val helper = DietNotificationHelper(context)
                    helper.scheduleDietPlan(currentPlan)
                }

                _dietProgress.value = newProgress
                onSuccess()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun saveChecklistOnly(updatedTasks: Map<String, Boolean>, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("diet_program").document("active_diet")
                    .update("tasks", updatedTasks, "lastUpdated", System.currentTimeMillis()).await()

                _dietProgress.value = _dietProgress.value?.copy(tasks = updatedTasks)
                onSuccess()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // --- COMPLETE DAY + STREAK (TIDAK BERUBAH) ---
    fun completeDay(maxDays: Int, onSuccess: () -> Unit, onFinished: () -> Unit, onError: (String) -> Unit = {}) {
        val userId = auth.currentUser?.uid ?: return
        val current = _dietProgress.value ?: return

        val todayDate = getTodayDate()

        if (current.lastLogDate == todayDate) {
            onError("Kamu sudah menyelesaikan target hari ini. Kembali lagi besok ya!")
            return
        }

        var newStreak = current.currentStreak
        val lastDateString = current.lastLogDate

        if (lastDateString.isNotEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val lastDate = sdf.parse(lastDateString)
                val today = sdf.parse(todayDate)

                if (lastDate != null && today != null) {
                    val diffInMillis = today.time - lastDate.time
                    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

                    if (diffInDays == 1L) {
                        newStreak += 1
                    } else if (diffInDays > 1L) {
                        newStreak = 1
                    }
                }
            } catch (e: Exception) {
                newStreak = 1
            }
        } else {
            newStreak = 1
        }

        val nextDay = current.currentDay + 1

        viewModelScope.launch {
            try {
                if (current.currentDay >= maxDays) {
                    // SELESAI
                    db.collection("users").document(userId)
                        .collection("diet_program").document("active_diet")
                        .update(
                            "isCompleted", true,
                            "lastLogDate", todayDate,
                            "lastUpdated", System.currentTimeMillis()
                        ).await()

                    saveEarnedBadge(current.dietId, current.dietName)

                    saveInAppNotification(
                        userId,
                        "Program Selesai! \uD83C\uDF89",
                        "Selamat! Anda menamatkan program ${current.dietName}.",
                        "ACHIEVEMENT"
                    )

                    _dietProgress.value = current.copy(isCompleted = true, lastLogDate = todayDate)
                    onFinished()

                } else {
                    // LANJUT NEXT DAY
                    val emptyTasks = mapOf("sarapan" to false, "siang" to false, "malam" to false, "camilan" to false, "air" to false)

                    db.collection("users").document(userId)
                        .collection("diet_program").document("active_diet")
                        .update(
                            "currentDay", nextDay,
                            "tasks", emptyTasks,
                            "lastUpdated", System.currentTimeMillis(),
                            "currentStreak", newStreak,
                            "lastLogDate", todayDate
                        ).await()

                    saveInAppNotification(
                        userId,
                        "Hari ke-${current.currentDay} Selesai \uD83D\uDD25",
                        "Hebat! Streak Anda kini $newStreak hari. Pertahankan!",
                        "ACHIEVEMENT"
                    )

                    _dietProgress.value = current.copy(
                        currentDay = nextDay,
                        tasks = emptyTasks,
                        currentStreak = newStreak,
                        lastLogDate = todayDate
                    )
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Gagal menyimpan progress. Cek koneksi internet.")
            }
        }
    }

    private suspend fun saveInAppNotification(userId: String, title: String, message: String, type: String) {
        val notif = InAppNotificationData(
            title = title,
            message = message,
            time = getCurrentTime(),
            type = type,
            isRead = false,
            timestamp = System.currentTimeMillis()
        )
        try {
            db.collection("users").document(userId)
                .collection("notifications")
                .add(notif)
                .await()
        } catch (e: Exception) { e.printStackTrace() }
    }

    // --- UPDATED: MENERIMA CONTEXT UNTUK STOP NOTIFIKASI ---
    fun stopCurrentDiet(context: Context, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                // 1. Hapus dari Firestore
                db.collection("users").document(userId)
                    .collection("diet_program").document("active_diet")
                    .delete().await()

                // 2. [BARU] Matikan semua alarm
                val helper = DietNotificationHelper(context)
                helper.cancelAllAlarms()

                _dietProgress.value = null
                onSuccess()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun repeatCurrentDiet(onSuccess: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val current = _dietProgress.value ?: return

        viewModelScope.launch {
            try {
                val emptyTasks = mapOf("sarapan" to false, "siang" to false, "malam" to false, "camilan" to false, "air" to false)

                db.collection("users").document(userId)
                    .collection("diet_program").document("active_diet")
                    .update(
                        "currentDay", 1,
                        "isCompleted", false,
                        "tasks", emptyTasks,
                        "lastUpdated", System.currentTimeMillis(),
                        "currentStreak", 0,
                        "lastLogDate", ""
                    ).await()

                _dietProgress.value = current.copy(
                    currentDay = 1,
                    isCompleted = false,
                    tasks = emptyTasks,
                    lastUpdated = System.currentTimeMillis(),
                    currentStreak = 0,
                    lastLogDate = ""
                )

                delay(500)
                onSuccess(current.dietId)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // ==========================================
    // 2. BADGE & CVD (TIDAK BERUBAH)
    // ==========================================
    private suspend fun saveEarnedBadge(dietId: String, dietName: String) {
        val userId = auth.currentUser?.uid ?: return
        val badge = EarnedBadge(id = dietId, name = dietName, dateEarned = System.currentTimeMillis())
        try {
            db.collection("users").document(userId)
                .collection("badges").document(dietId).set(badge).await()
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun fetchUserBadges() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").document(userId).collection("badges").get().await()
                _userBadges.value = snapshot.toObjects(EarnedBadge::class.java)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun checkLatestCVDData() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("cvd_history").whereEqualTo("userId", userId).get().await()
                if (!snapshot.isEmpty) {
                    val latestDoc = snapshot.documents.maxByOrNull { doc -> doc.getTimestamp("date")?.seconds ?: 0L }
                    if (latestDoc != null) {
                        _state.value = _state.value.copy(
                            cvdScoreVal = latestDoc.getDouble("userRiskScore") ?: 0.0,
                            cvdRiskCategory = latestDoc.getString("riskCategory") ?: "Tidak Diketahui"
                        )
                        _cvdDataAvailable.value = true
                    } else { _cvdDataAvailable.value = false }
                } else { _cvdDataAvailable.value = false }
            } catch (e: Exception) { _cvdDataAvailable.value = false }
        }
    }

    // Form Update Functions
    fun updateBloodPressure(v: String) { _state.value = _state.value.copy(bloodPressure = v) }
    fun updateCholesterol(v: String) { _state.value = _state.value.copy(cholesterol = v) }
    fun updateFoodPreference(v: String) { _state.value = _state.value.copy(foodPreference = v) }
    fun updateActivityLevel(v: String) { _state.value = _state.value.copy(activityLevel = v) }
    fun updateCommitment(v: String) { _state.value = _state.value.copy(commitment = v) }
    fun updateUseCVDData(v: Boolean) { if (v && !_cvdDataAvailable.value) return; _state.value = _state.value.copy(useCvdScore = v) }
    fun toggleHealthCondition(condition: String) {
        val current = _state.value.healthConditions.toMutableList()
        if (current.contains(condition)) current.remove(condition) else current.add(condition)
        _state.value = _state.value.copy(healthConditions = current)
    }

    fun calculateBestDiet(): CalculationResult {
        val s = _state.value
        val wBP = if (s.useCvdScore) 0.20 else 0.30
        val wChol = if (s.useCvdScore) 0.15 else 0.25
        val wCond = 0.20
        val wCVD = if (s.useCvdScore) 0.35 else 0.0
        val wPref = 0.10

        val c1_BP = when { s.bloodPressure.contains("Normal", true) -> 1.0; s.bloodPressure.contains("Kadang", true) -> 3.0; else -> 5.0 }
        val c2_Chol = when { s.cholesterol.contains("Normal", true) -> 1.0; s.cholesterol.contains("Agak", true) -> 3.0; else -> 5.0 }
        val c3_Cond = if (s.healthConditions.isEmpty() || s.healthConditions.contains("Tidak ada")) 1.0 else 1.0 + (s.healthConditions.size * 2.0).coerceAtMost(4.0)
        val c4_CVD = if (s.cvdScoreVal * 100 < 10) 1.0 else if (s.cvdScoreVal * 100 < 20) 3.0 else 5.0

        val alternatives = mapOf("1" to listOf(5.0, 3.0, 4.0, 5.0), "2" to listOf(3.0, 4.0, 5.0, 4.0), "3" to listOf(2.0, 5.0, 3.0, 3.0), "4" to listOf(4.0, 4.0, 4.0, 4.0))
        val finalScores = mutableMapOf<String, Double>()

        alternatives.forEach { (id, attr) ->
            var prefBonus = 0.0
            if (s.foodPreference.contains("Nabati", true) && (id == "1" || id == "2")) prefBonus = 1.0
            if (s.foodPreference.contains("Hewani", true) && (id == "4")) prefBonus = 1.0
            val score = (wBP * attr[0]/5.0 * c1_BP) + (wChol * attr[1]/5.0 * c2_Chol) + (wCond * attr[2]/5.0 * c3_Cond) + (wCVD * attr[3]/5.0 * c4_CVD) + (wPref * prefBonus)
            finalScores[id] = score
        }
        val best = finalScores.maxByOrNull { it.value } ?: finalScores.entries.first()
        val names = mapOf("1" to "Diet DASH", "2" to "Diet Mediterania", "3" to "Diet Rendah Lemak", "4" to "Diet Jantung Sehat")
        return CalculationResult(best.key, names[best.key] ?: "Diet Sehat", finalScores)
    }

    suspend fun generateGeminiAnalysis(dietName: String): String {
        return try {
            generativeModel.generateContent("Jelaskan secara singkat dalam 1-2 paragraf Mengapa $dietName cocok untuk data saya: ${state.value}").text ?: "Cocok."
        } catch (e: Exception) { "Rekomendasi terbaik." }
    }
}