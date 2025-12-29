package com.example.sehatjantungku.ui.screens.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatjantungku.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// State untuk menyimpan input user
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

class DietProgramViewModel : ViewModel() {

    private val _state = MutableStateFlow(DietProgramState())
    val state: StateFlow<DietProgramState> = _state.asStateFlow()

    private val _cvdDataAvailable = MutableStateFlow(false)
    val cvdDataAvailable: StateFlow<Boolean> = _cvdDataAvailable.asStateFlow()

    // Setup Gemini
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    init {
        checkLatestCVDData()
    }

    // --- PERBAIKAN: FIREBASE FETCH (Lebih Aman) ---
    private fun checkLatestCVDData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            _cvdDataAvailable.value = false
            return
        }

        viewModelScope.launch {
            try {
                // Mengambil semua data milik user (tanpa orderBy di query untuk menghindari error Index)
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("cvd_history")
                    .whereEqualTo("userId", user.uid)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    // Kita urutkan secara manual di sini (Client-side sorting)
                    // Cari dokumen dengan timestamp 'date' paling baru
                    val latestDoc = snapshot.documents.maxByOrNull { doc ->
                        doc.getTimestamp("date")?.seconds ?: 0L
                    }

                    if (latestDoc != null) {
                        val score = latestDoc.getDouble("userRiskScore") ?: 0.0
                        val category = latestDoc.getString("riskCategory") ?: "Tidak Diketahui"

                        _state.value = _state.value.copy(
                            cvdScoreVal = score,
                            cvdRiskCategory = category
                        )
                        _cvdDataAvailable.value = true
                    } else {
                        _cvdDataAvailable.value = false
                    }
                } else {
                    _cvdDataAvailable.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace() // Cek Logcat jika masih error
                _cvdDataAvailable.value = false
            }
        }
    }

    // --- INPUT HANDLERS (Sama seperti sebelumnya) ---
    fun updateBloodPressure(v: String) { _state.value = _state.value.copy(bloodPressure = v) }
    fun updateCholesterol(v: String) { _state.value = _state.value.copy(cholesterol = v) }
    fun updateFoodPreference(v: String) { _state.value = _state.value.copy(foodPreference = v) }
    fun updateActivityLevel(v: String) { _state.value = _state.value.copy(activityLevel = v) }
    fun updateCommitment(v: String) { _state.value = _state.value.copy(commitment = v) }

    // Logika pemilihan form no 7
    fun updateUseCVDData(v: Boolean) {
        if (v && !_cvdDataAvailable.value) return // Cegah pilih jika data tidak ada
        _state.value = _state.value.copy(useCvdScore = v)
    }

    fun toggleHealthCondition(condition: String) {
        val current = _state.value.healthConditions.toMutableList()
        if (current.contains(condition)) current.remove(condition) else current.add(condition)
        _state.value = _state.value.copy(healthConditions = current)
    }

    // --- ALGORITMA SAW (Simple Additive Weighting) ---
    fun calculateBestDiet(): CalculationResult {
        val s = _state.value

        // 1. Bobot (Weights)
        // Jika useCvdScore FALSE (Saran Umum), bobot wCVD = 0
        val wBP = if (s.useCvdScore) 0.20 else 0.30
        val wChol = if (s.useCvdScore) 0.15 else 0.25
        val wCond = 0.20
        val wCVD = if (s.useCvdScore) 0.35 else 0.0
        val wPref = 0.10

        // 2. Normalisasi Input (Cost/Benefit) -> Skala 1-5

        // C1: Tekanan Darah (Semakin tinggi semakin butuh diet khusus -> Nilai 5)
        val c1_BP = when {
            s.bloodPressure.contains("Normal", true) -> 1.0
            s.bloodPressure.contains("Kadang", true) -> 3.0
            else -> 5.0
        }

        // C2: Kolesterol
        val c2_Chol = when {
            s.cholesterol.contains("Normal", true) -> 1.0
            s.cholesterol.contains("Agak", true) -> 3.0
            else -> 5.0
        }

        // C3: Kondisi Kesehatan
        val c3_Cond = if (s.healthConditions.contains("Tidak ada") || s.healthConditions.isEmpty()) 1.0 else {
            1.0 + (s.healthConditions.size * 2.0).coerceAtMost(4.0)
        }

        // C4: CVD Risk (Persentase)
        val riskPercent = s.cvdScoreVal * 100
        val c4_CVD = when {
            riskPercent < 10 -> 1.0
            riskPercent < 20 -> 3.0
            else -> 5.0
        }

        // 3. Matriks Kecocokan Alternatif (Benefit Matrix)
        // 1=DASH, 2=Mediterania, 3=TLC, 4=Jantung Sehat
        val alternatives = mapOf(
            "1" to listOf(5.0, 3.0, 4.0, 5.0),
            "2" to listOf(3.0, 4.0, 5.0, 4.0),
            "3" to listOf(2.0, 5.0, 3.0, 3.0),
            "4" to listOf(4.0, 4.0, 4.0, 4.0)
        )

        // 4. Hitung Nilai Akhir
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

            // Rumus SAW
            val score = (wBP * rBP * c1_BP) +
                    (wChol * rChol * c2_Chol) +
                    (wCond * rCond * c3_Cond) +
                    (wCVD * rCVD * c4_CVD) +
                    (wPref * rPref)

            finalScores[dietId] = score
        }

        val bestDietEntry = finalScores.maxByOrNull { it.value } ?: finalScores.entries.first()

        val dietNames = mapOf(
            "1" to "Diet DASH",
            "2" to "Diet Mediterania",
            "3" to "Diet Rendah Lemak (TLC)",
            "4" to "Diet Jantung Sehat"
        )

        return CalculationResult(
            bestDietId = bestDietEntry.key,
            bestDietName = dietNames[bestDietEntry.key] ?: "Diet Sehat",
            scores = finalScores
        )
    }

    // --- GEMINI ANALYSIS (Singkat) ---
    suspend fun generateGeminiAnalysis(dietName: String): String {
        val s = _state.value
        val conditions = s.healthConditions.joinToString(", ").ifEmpty { "Sehat" }
        val riskText = if (s.useCvdScore) "risiko jantung ${(s.cvdScoreVal*100).toInt()}% (${s.cvdRiskCategory})" else "profil umum"

        val prompt = """
            Sebagai ahli gizi, berikan analisis SANGAT SINGKAT (maksimal 1 paragraf pendek, 3-4 kalimat).
            Mengapa $dietName cocok untuk saya?
            Data saya: TD ${s.bloodPressure}, Kolesterol ${s.cholesterol}, Kondisi: $conditions, Status: $riskText.
            Jawab langsung ke intinya dengan nada menyemangati.
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "Diet ini dipilih karena profil nutrisinya paling sesuai untuk membantu kondisi kesehatan Anda saat ini."
        } catch (e: Exception) {
            "Berdasarkan analisis sistem, diet ini memiliki skor kecocokan tertinggi dengan kondisi kesehatan Anda."
        }
    }
}