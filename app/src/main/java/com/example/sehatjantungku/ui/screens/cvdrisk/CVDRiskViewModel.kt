package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatjantungku.BuildConfig
import com.example.sehatjantungku.data.model.CVDRiskResult
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

// State untuk UI Rekomendasi
sealed class RecommendationState {
    object Idle : RecommendationState()
    object Loading : RecommendationState()
    data class Success(val recommendations: List<String>) : RecommendationState()
    data class Error(val message: String) : RecommendationState()
}

data class CVDRiskState(
    val gender: String = "",
    val age: String = "",
    val height: String = "",
    val weight: String = "",
    val bmi: String = "",
    val bloodPressure: String = "",
    val diabetes: Boolean = false,
    val smoker: Boolean = false,
    val hypertension: Boolean = false
)

class CVDRiskViewModel : ViewModel() {

    private val _state = MutableStateFlow(CVDRiskState())
    val state: StateFlow<CVDRiskState> = _state.asStateFlow()

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()

    // State untuk menampung hasil rekomendasi Gemini
    private val _recommendationState = MutableStateFlow<RecommendationState>(RecommendationState.Idle)
    val recommendationState: StateFlow<RecommendationState> = _recommendationState.asStateFlow()

    // --- Update Functions ---
    fun updateGender(value: String) { _state.value = _state.value.copy(gender = value) }
    fun updateAge(value: String) { _state.value = _state.value.copy(age = value) }
    fun updateHeight(value: String) { _state.value = _state.value.copy(height = value) }
    fun updateWeight(value: String) { _state.value = _state.value.copy(weight = value) }
    fun updateBloodPressure(value: String) { _state.value = _state.value.copy(bloodPressure = value) }
    fun updateDiabetes(value: Boolean) { _state.value = _state.value.copy(diabetes = value) }
    fun updateSmoker(value: Boolean) { _state.value = _state.value.copy(smoker = value) }
    fun updateHypertension(value: Boolean) { _state.value = _state.value.copy(hypertension = value) }

    fun calculateBMI() {
        val hInput = _state.value.height.toDoubleOrNull() ?: 0.0
        val w = _state.value.weight.toDoubleOrNull() ?: 0.0

        if (hInput > 0 && w > 0) {
            val h = if (hInput > 3.0) hInput / 100.0 else hInput
            val bmi = w / (h * h)
            _state.value = _state.value.copy(bmi = String.format("%.2f", bmi))
        } else {
            _state.value = _state.value.copy(bmi = "")
        }
    }

    // ... (Fungsi calculateSigmaBetaX, calculateRiskScore, OptimalRisk, NormalRisk, HeartAgeExact SAMA SEPERTI SEBELUMNYA - TIDAK DIUBAH) ...
    // ... (Anda bisa menyalin fungsi-fungsi perhitungan matematika dari kode lama Anda di sini) ...

    private fun calculateSigmaBetaX(
        isMale: Boolean,
        age: Double,
        sbp: Double,
        bmi: Double,
        isSmoker: Boolean,
        hasDiabetes: Boolean, isTreated: Boolean
    ): Double {
        val lnAge = ln(age); val lnSBP = ln(sbp); val lnBMI = ln(bmi)
        val smoke = if (isSmoker) 1.0 else 0.0; val diab = if (hasDiabetes) 1.0 else 0.0
        return if (isMale) {
            val sbpCoeff = if (isTreated) 1.85508 else 1.92672
            (lnAge * 3.11296) + (lnSBP * sbpCoeff) + (smoke * 0.70953) + (lnBMI * 0.79277) + (diab * 0.5316)
        } else {
            val sbpCoeff = if (isTreated) 2.88267 else 2.81291
            (lnAge * 2.72107) + (lnSBP * sbpCoeff) + (smoke * 0.61868) + (lnBMI * 0.51125) + (diab * 0.77763)
        }
    }

    private fun calculateRiskScore(sigmaBetaX: Double, isMale: Boolean): Double {
        val baseline = if (isMale) 0.88431 else 0.94833
        val meanBeta = if (isMale) 23.9388 else 26.0145
        return 1.0 - baseline.pow(exp(sigmaBetaX - meanBeta))
    }

    private fun calculateOptimalRisk(isMale: Boolean): Double {
        val optimalBetaX = if (isMale) {
            3.11296 * 3.40119782 + 1.85508 * ln(110.0) + 0.79277 * ln(22.0)
        } else {
            2.72107 * 3.40119782 + 2.81291 * ln(110.0) + 0.51125 * ln(22.0)
        }
        return calculateRiskScore(optimalBetaX, isMale)
    }

    private fun calculateNormalRisk(isMale: Boolean): Double {
        val normalBetaX = if (isMale) {
            3.11296 * 3.40119782 + 1.85508 * ln(125.0) + 0.79277 * ln(22.5)
        } else {
            2.72107 * 3.40119782 + 2.81291 * ln(125.0) + 0.51125 * ln(22.5)
        }
        return calculateRiskScore(normalBetaX, isMale)
    }

    private fun calculateHeartAgeExact(isMale: Boolean, userRiskScoreDecimal: Double): Int {
        val coeffSbp = if(isMale) 1.85508 else 2.81921
        val coeffSmoke = if(isMale) 0.70953 else 0.61868
        val coeffBmi = if(isMale) 0.79277 else 0.51125
        val coeffDiab = if(isMale) 0.5316 else 0.77763
        val constVal = if(isMale) 23.9388 else 26.0145
        val betaAge = if(isMale) 3.11296 else 2.72107
        val baseline = if(isMale) 0.88431 else 0.94833

        val mSbp = ln(125.0); val mBmi = ln(22.5); val mSmoke = 0.0; val mDiab = 0.0
        val sigmaRef = (coeffSbp * mSbp) + (coeffSmoke * mSmoke) + (coeffBmi * mBmi) + (coeffDiab * mDiab)
        val constiNum = exp(-(sigmaRef - constVal) / betaAge)
        val constiDenom = (-ln(baseline)).pow(1.0 / betaAge)
        val consti = constiNum * (1.0 / constiDenom)
        val safeRisk = userRiskScoreDecimal.coerceIn(0.0001, 0.9999)
        val term = (-ln(1.0 - safeRisk)).pow(1.0 / betaAge)
        return (consti * term).roundToInt().coerceAtLeast(1)
    }

    fun calculateRisk(): Pair<String, Int> {
        val s = _state.value
        val age = s.age.toDoubleOrNull() ?: 0.0
        val sbp = s.bloodPressure.toDoubleOrNull() ?: 0.0
        val bmi = s.bmi.toDoubleOrNull() ?: 0.0
        val isMale = s.gender == "Pria"

        if (age <= 0 || sbp <= 0 || bmi <= 0 || s.gender.isEmpty()) {
            return Pair("0.0,0.0,0.0", 0)
        }

        val userSigmaBeta = calculateSigmaBetaX(isMale, age, sbp, bmi, s.smoker, s.diabetes, s.hypertension)
        val userRisk = calculateRiskScore(userSigmaBeta, isMale)
        val optimalRisk = calculateOptimalRisk(isMale)
        val normalRisk = calculateNormalRisk(isMale)
        val heartAge = calculateHeartAgeExact(isMale, userRisk)

        val riskString = String.format("%.6f,%.6f,%.6f", userRisk, optimalRisk, normalRisk)
        return Pair(riskString, heartAge)
    }

    // =========================================================================
    // FITUR REKOMENDASI AI GEMINI (BARU)
    // =========================================================================
    fun fetchRecommendation(heartAge: Int, userRiskPercent: Float) {
        // Jika sudah ada data sukses, tidak perlu fetch ulang (opsional)
        if (_recommendationState.value is RecommendationState.Success) return

        viewModelScope.launch {
            _recommendationState.value = RecommendationState.Loading
            try {
                val realAge = _state.value.age.toIntOrNull() ?: 30 // Fallback default jika kosong

                // 1. Kategori Skor Risiko
                val riskCategory = when {
                    userRiskPercent < 10 -> "Risiko Rendah (< 10%)"
                    userRiskPercent <= 20 -> "Risiko Sedang (10% - 20%)"
                    else -> "Risiko Tinggi (> 20%)"
                }

                // 2. Kategori Umur Jantung
                val ageDiff = heartAge - realAge
                val heartAgeCategory = when {
                    ageDiff <= 0 -> "Risiko Rendah (Baik, Umur Jantung ≤ Umur Asli)"
                    ageDiff <= 5 -> "Risiko Sedang (Umur Jantung +1 s.d +5 tahun dari asli)"
                    else -> "Risiko Tinggi (Umur Jantung > +5 tahun dari asli)"
                }

                val prompt = """
                    Anda adalah asisten kesehatan jantung personal. Berikan 4-5 rekomendasi kesehatan yang spesifik, ramah, dan memotivasi berdasarkan profil pengguna berikut:
                    
                    - Umur Asli: $realAge tahun
                    - Umur Jantung: $heartAge tahun ($heartAgeCategory)
                    - Risiko CVD: ${String.format("%.1f", userRiskPercent)}% ($riskCategory)
                    
                    Instruksi Format:
                    - Berikan HANYA daftar rekomendasi dalam format poin-poin.
                    - Pisahkan setiap poin rekomendasi dengan baris baru (Enter).
                    - Jangan gunakan simbol bullet (seperti *, -) atau angka di awal kalimat, cukup teks polos.
                    - Gunakan Bahasa Indonesia.
                """.trimIndent()

                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )

                val response = generativeModel.generateContent(prompt)
                val rawText = response.text ?: ""

                // Bersihkan output agar menjadi list string yang rapi
                val recommendationList = rawText.split("\n")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .map { it.removePrefix("- ").removePrefix("* ").removePrefix("• ") }

                if (recommendationList.isEmpty()) {
                    _recommendationState.value = RecommendationState.Error("Tidak ada rekomendasi yang dihasilkan.")
                } else {
                    _recommendationState.value = RecommendationState.Success(recommendationList)
                }

            } catch (e: Exception) {
                _recommendationState.value = RecommendationState.Error("Gagal memuat rekomendasi AI: ${e.localizedMessage}")
            }
        }
    }

    // --- Firebase Save ---
    fun saveToFirebase(heartAge: Int, userRisk: Double, category: String) {
        viewModelScope.launch {
            _saveStatus.value = SaveStatus.Loading
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                _saveStatus.value = SaveStatus.Error("User tidak ditemukan (belum login)")
                return@launch
            }
            val resultData = CVDRiskResult(
                userId = user.uid,
                date = Timestamp.now(),
                heartAge = heartAge,
                userRiskScore = userRisk,
                riskCategory = category,
                inputSummary = "${_state.value.gender}, ${_state.value.age}th, BMI:${_state.value.bmi}"
            )
            FirebaseFirestore.getInstance().collection("cvd_history")
                .add(resultData)
                .addOnSuccessListener { _saveStatus.value = SaveStatus.Success }
                .addOnFailureListener { e -> _saveStatus.value = SaveStatus.Error(e.message ?: "Gagal menyimpan") }
        }
    }

    fun resetSaveStatus() {
        _saveStatus.value = SaveStatus.Idle
    }
}

sealed class SaveStatus {
    object Idle : SaveStatus()
    object Loading : SaveStatus()
    object Success : SaveStatus()
    data class Error(val message: String) : SaveStatus()
}