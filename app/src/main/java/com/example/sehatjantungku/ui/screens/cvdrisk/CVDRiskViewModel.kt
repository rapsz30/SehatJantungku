package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatjantungku.data.model.CVDRiskResult
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
            // Konversi ke Meter jika input user dalam CM (misal > 3.0 dianggap cm)
            val h = if (hInput > 3.0) hInput / 100.0 else hInput
            val bmi = w / (h * h)
            _state.value = _state.value.copy(bmi = String.format("%.2f", bmi))
        } else {
            _state.value = _state.value.copy(bmi = "")
        }
    }

    // =========================================================================
    // 1. LOGIKA CVD RISK (NORMAL, OPTIMAL, USER)
    //    Sesuai request pertama (Treatment vs No Treatment)
    // =========================================================================

    private fun calculateSigmaBetaX(
        isMale: Boolean,
        age: Double,
        sbp: Double,
        bmi: Double,
        isSmoker: Boolean,
        hasDiabetes: Boolean,
        isTreated: Boolean // Hipertensi Yes/No
    ): Double {
        val lnAge = ln(age)
        val lnSBP = ln(sbp)
        val lnBMI = ln(bmi)
        val smoke = if (isSmoker) 1.0 else 0.0
        val diab = if (hasDiabetes) 1.0 else 0.0

        return if (isMale) {
            val ageCoeff = 3.11296
            val smokeCoeff = 0.70953
            val bmiCoeff = 0.79277
            val diabCoeff = 0.5316
            // Jika treated (hipertensi), coeff SBP = 1.85508, jika tidak = 1.92672
            val sbpCoeff = if (isTreated) 1.85508 else 1.92672

            (lnAge * ageCoeff) + (lnSBP * sbpCoeff) + (smoke * smokeCoeff) + (lnBMI * bmiCoeff) + (diab * diabCoeff)
        } else {
            val ageCoeff = 2.72107
            val smokeCoeff = 0.61868
            val bmiCoeff = 0.51125
            val diabCoeff = 0.77763
            // Jika treated (hipertensi), coeff SBP = 2.88267, jika tidak = 2.81291
            val sbpCoeff = if (isTreated) 2.88267 else 2.81291

            (lnAge * ageCoeff) + (lnSBP * sbpCoeff) + (smoke * smokeCoeff) + (lnBMI * bmiCoeff) + (diab * diabCoeff)
        }
    }

    private fun calculateRiskScore(sigmaBetaX: Double, isMale: Boolean): Double {
        val baselineSurvival = if (isMale) 0.88431 else 0.94833
        val meanBeta = if (isMale) 23.9388 else 26.0145

        // Rumus: 1 - Baseline ^ exp(Sigma - Mean)
        val exponent = exp(sigmaBetaX - meanBeta)
        return 1.0 - baselineSurvival.pow(exponent)
    }

    // --- Optimal Risk (Fixed Inputs) ---
    private fun calculateOptimalRisk(isMale: Boolean): Double {
        val optimalBetaX = if (isMale) {
            // SBP 110, BMI 22
            3.11296 * 3.40119782 + 1.85508 * ln(110.0) + 0.79277 * ln(22.0)
        } else {
            2.72107 * 3.40119782 + 2.81291 * ln(110.0) + 0.51125 * ln(22.0)
        }
        return calculateRiskScore(optimalBetaX, isMale)
    }

    // --- Normal Risk (Fixed Inputs) ---
    private fun calculateNormalRisk(isMale: Boolean): Double {
        val normalBetaX = if (isMale) {
            // SBP 125, BMI 22.5
            3.11296 * 3.40119782 + 1.85508 * ln(125.0) + 0.79277 * ln(22.5)
        } else {
            2.72107 * 3.40119782 + 2.81291 * ln(125.0) + 0.51125 * ln(22.5)
        }
        return calculateRiskScore(normalBetaX, isMale)
    }

    // =========================================================================
    // 2. LOGIKA HEART AGE
    //    Sesuai rumus khusus yang baru Anda kirim (M Values Fixed & Coeff Khusus)
    // =========================================================================

    private fun calculateHeartAgeExact(isMale: Boolean, userRiskScoreDecimal: Double): Int {
        // M Values untuk Referensi "Consti" (Normal Baseline)
        val mSbp = ln(125.0)  // SBP = ln(125)
        val mBmi = ln(22.5)   // BMI = ln(22.5)
        val mSmoke = 0.0      // Baseline diasumsikan 0 (sehat) untuk consti
        val mDiab = 0.0       // Baseline diasumsikan 0 (sehat) untuk consti

        val coeffSbp: Double
        val coeffSmoke: Double
        val coeffBmi: Double
        val coeffDiab: Double
        val constVal: Double
        val betaAge: Double
        val baselineSurvival: Double

        // Menggunakan Koefisien KHUSUS Heart Age dari request Anda
        if (isMale) {
            coeffSbp = 1.85508
            coeffSmoke = 0.70953
            coeffBmi = 0.79277
            coeffDiab = 0.5316
            constVal = 23.9388
            betaAge = 3.11296
            baselineSurvival = 0.88431
        } else {
            coeffSbp = 2.81921 // Nilai unik sesuai request heart age
            coeffSmoke = 0.61868
            coeffBmi = 0.51125
            coeffDiab = 0.77763
            constVal = 26.0145
            betaAge = 2.72107
            baselineSurvival = 0.94833
        }

        // 1. Hitung Consti (Numerator & Denominator)
        // Rumus Numerator: exp (-((coeff sbp * sbp) + ... - const)/beta)
        val sigmaRef = (coeffSbp * mSbp) + (coeffSmoke * mSmoke) + (coeffBmi * mBmi) + (coeffDiab * mDiab)
        val constiNum = exp(-(sigmaRef - constVal) / betaAge)

        // Rumus Denominator: (-ln(baseline))^(1/beta)
        val expo = 1.0 / betaAge
        val constiDenom = (-ln(baselineSurvival)).pow(expo)

        val consti = constiNum * (1.0 / constiDenom)

        // 2. Hitung Term (Menggunakan Risk Score User yang sudah dihitung sebelumnya)
        // Rumus: (-ln(1 - Risk)) ^ expo
        // Kita batasi risk max 0.9999 agar tidak error ln(0)
        val safeRisk = userRiskScoreDecimal.coerceIn(0.0001, 0.9999)
        val term = (-ln(1.0 - safeRisk)).pow(expo)

        // 3. Hasil Akhir
        val heartAge = consti * term

        return heartAge.roundToInt().coerceAtLeast(1)
    }

    // =========================================================================
    // MAIN CALCULATE FUNCTION
    // =========================================================================
    fun calculateRisk(): Pair<String, Int> {
        val s = _state.value
        val age = s.age.toDoubleOrNull() ?: 0.0
        val sbp = s.bloodPressure.toDoubleOrNull() ?: 0.0
        val bmi = s.bmi.toDoubleOrNull() ?: 0.0
        val isMale = s.gender == "Pria"

        if (age <= 0 || sbp <= 0 || bmi <= 0 || s.gender.isEmpty()) {
            return Pair("0.0,0.0,0.0", 0)
        }

        // 1. Hitung RISK SCORE USER (Menggunakan rumus Treat/NoTreat)
        val userSigmaBeta = calculateSigmaBetaX(
            isMale = isMale,
            age = age,
            sbp = sbp,
            bmi = bmi,
            isSmoker = s.smoker,
            hasDiabetes = s.diabetes,
            isTreated = s.hypertension
        )
        val userRisk = calculateRiskScore(userSigmaBeta, isMale)

        // 2. Hitung RISK OPTIMAL & NORMAL
        val optimalRisk = calculateOptimalRisk(isMale)
        val normalRisk = calculateNormalRisk(isMale)

        // 3. Hitung HEART AGE (Menggunakan userRisk sebagai input 'Term')
        val heartAge = calculateHeartAgeExact(isMale, userRisk)

        val riskString = String.format("%.6f,%.6f,%.6f", userRisk, optimalRisk, normalRisk)
        return Pair(riskString, heartAge)
    }

    // ---------------------- FIREBASE SAVE ----------------------
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