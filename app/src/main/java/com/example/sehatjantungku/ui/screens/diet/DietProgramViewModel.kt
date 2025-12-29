package com.example.sehatjantungku.ui.screens.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatjantungku.BuildConfig
import com.example.sehatjantungku.data.model.DietPlan
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// State Input Form
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

// Hasil Perhitungan SAW
data class CalculationResult(
    val bestDietId: String,
    val bestDietName: String,
    val scores: Map<String, Double>
)

// Model Data Progress User
data class UserDietProgress(
    val userId: String = "",
    val dietId: String = "",
    val dietName: String = "",
    val currentDay: Int = 1,
    val tasks: Map<String, Boolean> = mapOf(
        "sarapan" to false, "siang" to false, "malam" to false, "camilan" to false, "air" to false
    ),
    val lastUpdated: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)

// Model Badge
data class EarnedBadge(
    val id: String = "",
    val name: String = "",
    val dateEarned: Long = 0
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

    // ==========================================
    // 1. DATA DIET PLAN & PROGRESS
    // ==========================================
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

    fun loadUserDietProgress() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoadingProgress.value = true
            try {
                val doc = db.collection("users").document(userId)
                    .collection("diet_program").document("active_diet")
                    .get().await()

                _dietProgress.value = if (doc.exists()) doc.toObject(UserDietProgress::class.java) else null
            } catch (e: Exception) { e.printStackTrace() } finally { _isLoadingProgress.value = false }
        }
    }

    fun startNewDiet(dietId: String, dietName: String, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val newProgress = UserDietProgress(userId = userId, dietId = dietId, dietName = dietName, currentDay = 1)
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("diet_program").document("active_diet")
                    .set(newProgress).await()
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

    fun completeDay(maxDays: Int, onSuccess: () -> Unit, onFinished: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val current = _dietProgress.value ?: return
        val nextDay = current.currentDay + 1

        viewModelScope.launch {
            try {
                if (current.currentDay >= maxDays) {
                    // --- DIET SELESAI ---
                    db.collection("users").document(userId)
                        .collection("diet_program").document("active_diet")
                        .update("isCompleted", true).await()

                    // Simpan Badge
                    saveEarnedBadge(current.dietId, current.dietName)

                    // [PERBAIKAN PENTING] Update state lokal agar UI tahu diet sudah selesai
                    _dietProgress.value = current.copy(isCompleted = true)

                    onFinished()
                } else {
                    // Lanjut Hari Berikutnya
                    val emptyTasks = mapOf("sarapan" to false, "siang" to false, "malam" to false, "camilan" to false, "air" to false)
                    db.collection("users").document(userId)
                        .collection("diet_program").document("active_diet")
                        .update("currentDay", nextDay, "tasks", emptyTasks, "lastUpdated", System.currentTimeMillis()).await()
                    _dietProgress.value = current.copy(currentDay = nextDay, tasks = emptyTasks)
                    onSuccess()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun stopCurrentDiet(onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("diet_program").document("active_diet")
                    .delete().await()
                _dietProgress.value = null
                onSuccess()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // [PERBAIKAN] Fungsi Ulangi Diet (Reset)
    fun repeatCurrentDiet(onSuccess: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val current = _dietProgress.value ?: return

        viewModelScope.launch {
            try {
                val emptyTasks = mapOf(
                    "sarapan" to false, "siang" to false, "malam" to false,
                    "camilan" to false, "air" to false
                )

                // Update Firestore: Reset hari ke 1 dan hapus status completed
                db.collection("users").document(userId)
                    .collection("diet_program").document("active_diet")
                    .update(
                        "currentDay", 1,
                        "isCompleted", false, // PENTING: Set false
                        "tasks", emptyTasks,
                        "lastUpdated", System.currentTimeMillis()
                    )
                    .await()

                // Update local state agar UI langsung berubah
                _dietProgress.value = current.copy(
                    currentDay = 1,
                    isCompleted = false,
                    tasks = emptyTasks,
                    lastUpdated = System.currentTimeMillis()
                )

                onSuccess(current.dietId)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // ==========================================
    // 2. BADGE SYSTEM
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

    // ==========================================
    // 3. CVD & SAW
    // ==========================================
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

    // Update Functions (Shortened for brevity but logic is same)
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
            generativeModel.generateContent("Singkat: Mengapa $dietName cocok untuk data saya: ${state.value}").text ?: "Cocok."
        } catch (e: Exception) { "Rekomendasi terbaik." }
    }
}