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
    val bestDietId: String, // Tetap String untuk navigasi
    val bestDietName: String,
    val scores: Map<String, Double>
)

// Model Data Progress User (Disimpan di Firebase)
data class UserDietProgress(
    val userId: String = "",
    val dietId: String = "", // Disimpan sebagai String agar fleksibel
    val dietName: String = "",
    val currentDay: Int = 1,
    val tasks: Map<String, Boolean> = mapOf(
        "sarapan" to false,
        "siang" to false,
        "malam" to false,
        "camilan" to false,
        "air" to false
    ),
    val lastUpdated: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
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

    // State Content Diet Plan (Dari Firebase)
    private val _fetchedDietPlan = MutableStateFlow<DietPlan?>(null)
    val fetchedDietPlan: StateFlow<DietPlan?> = _fetchedDietPlan.asStateFlow()

    private val _isLoadingPlan = MutableStateFlow(false)
    val isLoadingPlan: StateFlow<Boolean> = _isLoadingPlan.asStateFlow()

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
    // 1. DATA DIET PLAN (Firestore)
    // ==========================================
    fun fetchDietPlanFromFirebase(dietId: String) {
        viewModelScope.launch {
            _isLoadingPlan.value = true
            _fetchedDietPlan.value = null
            try {
                // Mengambil dokumen berdasarkan ID string ("1", "2", dst)
                val doc = db.collection("diet_plans").document(dietId).get().await()
                if (doc.exists()) {
                    val plan = doc.toObject(DietPlan::class.java)
                    _fetchedDietPlan.value = plan
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingPlan.value = false
            }
        }
    }

    // ==========================================
    // 2. PROGRESS USER (Firestore)
    // ==========================================

    fun loadUserDietProgress() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoadingProgress.value = true
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
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingProgress.value = false
            }
        }
    }

    fun startNewDiet(dietId: String, dietName: String, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val newProgress = UserDietProgress(
            userId = userId,
            dietId = dietId,
            dietName = dietName,
            currentDay = 1
        )

        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("diet_program").document("active_diet")
                    .set(newProgress)
                    .await()

                _dietProgress.value = newProgress
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveChecklistOnly(updatedTasks: Map<String, Boolean>, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .collection("diet_program").document("active_diet")
                    .update("tasks", updatedTasks, "lastUpdated", System.currentTimeMillis())
                    .await()

                _dietProgress.value = _dietProgress.value?.copy(tasks = updatedTasks)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun completeDay(maxDays: Int, onSuccess: () -> Unit, onFinished: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val current = _dietProgress.value ?: return

        val nextDay = current.currentDay + 1

        viewModelScope.launch {
            try {
                if (current.currentDay >= maxDays) {
                    // Diet Selesai
                    db.collection("users").document(userId)
                        .collection("diet_program").document("active_diet")
                        .update("isCompleted", true)
                        .await()
                    onFinished()
                } else {
                    // Lanjut Hari Berikutnya
                    val emptyTasks = mapOf(
                        "sarapan" to false, "siang" to false, "malam" to false,
                        "camilan" to false, "air" to false
                    )

                    db.collection("users").document(userId)
                        .collection("diet_program").document("active_diet")
                        .update(
                            "currentDay", nextDay,
                            "tasks", emptyTasks,
                            "lastUpdated", System.currentTimeMillis()
                        ).await()

                    _dietProgress.value = current.copy(currentDay = nextDay, tasks = emptyTasks)
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ==========================================
    // 3. CVD HISTORY & SAW LOGIC
    // ==========================================

    private fun checkLatestCVDData() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("cvd_history")
                    .whereEqualTo("userId", userId)
                    .get().await()

                if (!snapshot.isEmpty) {
                    val latestDoc = snapshot.documents.maxByOrNull { doc ->
                        doc.getTimestamp("date")?.seconds ?: 0L
                    }
                    if (latestDoc != null) {
                        val score = latestDoc.getDouble("userRiskScore") ?: 0.0
                        val category = latestDoc.getString("riskCategory") ?: "Tidak Diketahui"
                        _state.value = _state.value.copy(cvdScoreVal = score, cvdRiskCategory = category)
                        _cvdDataAvailable.value = true
                    } else { _cvdDataAvailable.value = false }
                } else { _cvdDataAvailable.value = false }
            } catch (e: Exception) { _cvdDataAvailable.value = false }
        }
    }

    fun updateBloodPressure(v: String) { _state.value = _state.value.copy(bloodPressure = v) }
    fun updateCholesterol(v: String) { _state.value = _state.value.copy(cholesterol = v) }
    fun updateFoodPreference(v: String) { _state.value = _state.value.copy(foodPreference = v) }
    fun updateActivityLevel(v: String) { _state.value = _state.value.copy(activityLevel = v) }
    fun updateCommitment(v: String) { _state.value = _state.value.copy(commitment = v) }
    fun updateUseCVDData(v: Boolean) {
        if (v && !_cvdDataAvailable.value) return
        _state.value = _state.value.copy(useCvdScore = v)
    }
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

        val c1_BP = when {
            s.bloodPressure.contains("Normal", true) -> 1.0
            s.bloodPressure.contains("Kadang", true) -> 3.0
            else -> 5.0
        }
        val c2_Chol = when {
            s.cholesterol.contains("Normal", true) -> 1.0
            s.cholesterol.contains("Agak", true) -> 3.0
            else -> 5.0
        }
        val c3_Cond = if (s.healthConditions.contains("Tidak ada") || s.healthConditions.isEmpty()) 1.0 else {
            1.0 + (s.healthConditions.size * 2.0).coerceAtMost(4.0)
        }
        val riskPercent = s.cvdScoreVal * 100
        val c4_CVD = when {
            riskPercent < 10 -> 1.0
            riskPercent < 20 -> 3.0
            else -> 5.0
        }

        // Key menggunakan String ("1", "2") agar sesuai dengan ID dokumen Firestore
        val alternatives = mapOf(
            "1" to listOf(5.0, 3.0, 4.0, 5.0),
            "2" to listOf(3.0, 4.0, 5.0, 4.0),
            "3" to listOf(2.0, 5.0, 3.0, 3.0),
            "4" to listOf(4.0, 4.0, 4.0, 4.0)
        )

        val finalScores = mutableMapOf<String, Double>()
        val maxVal = 5.0

        alternatives.forEach { (dietId, attributes) ->
            val rBP = attributes[0] / maxVal
            val rChol = attributes[1] / maxVal
            val rCond = attributes[2] / maxVal
            val rCVD = attributes[3] / maxVal

            var prefBonus = 0.0
            if (s.foodPreference.contains("Nabati", true) && (dietId == "1" || dietId == "2")) prefBonus = 1.0
            if (s.foodPreference.contains("Hewani", true) && (dietId == "4")) prefBonus = 1.0
            val rPref = prefBonus

            val score = (wBP * rBP * c1_BP) + (wChol * rChol * c2_Chol) + (wCond * rCond * c3_Cond) + (wCVD * rCVD * c4_CVD) + (wPref * rPref)
            finalScores[dietId] = score
        }

        val bestDietEntry = finalScores.maxByOrNull { it.value } ?: finalScores.entries.first()
        val dietNames = mapOf("1" to "Diet DASH", "2" to "Diet Mediterania", "3" to "Diet Rendah Lemak", "4" to "Diet Jantung Sehat")

        return CalculationResult(
            bestDietId = bestDietEntry.key,
            bestDietName = dietNames[bestDietEntry.key] ?: "Diet Sehat",
            scores = finalScores
        )
    }

    suspend fun generateGeminiAnalysis(dietName: String): String {
        val s = _state.value
        val conditions = s.healthConditions.joinToString(", ").ifEmpty { "Sehat" }
        val riskText = if (s.useCvdScore) "risiko jantung ${(s.cvdScoreVal*100).toInt()}% (${s.cvdRiskCategory})" else "profil umum"
        val prompt = "Sebagai ahli gizi, berikan analisis SANGAT SINGKAT (maksimal 1 paragraf, 3-4 kalimat). Mengapa $dietName cocok untuk saya? Data: TD ${s.bloodPressure}, Kolesterol ${s.cholesterol}, Kondisi: $conditions, Status: $riskText. Jawab to the point."
        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "Diet ini dipilih karena profil nutrisinya paling sesuai untuk kondisi Anda."
        } catch (e: Exception) { "Diet ini memiliki skor kecocokan tertinggi berdasarkan data kesehatan Anda." }
    }
}