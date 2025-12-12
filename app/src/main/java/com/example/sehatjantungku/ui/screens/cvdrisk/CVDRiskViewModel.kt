package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.ln
import kotlin.math.exp
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

    // -------------------------- INPUT HANDLER --------------------------
    fun updateGender(value: String) { _state.value = _state.value.copy(gender = value) }
    fun updateAge(value: String) { _state.value = _state.value.copy(age = value) }
    fun updateHeight(value: String) { _state.value = _state.value.copy(height = value) }
    fun updateWeight(value: String) { _state.value = _state.value.copy(weight = value) }
    fun updateBloodPressure(value: String) { _state.value = _state.value.copy(bloodPressure = value) }
    fun updateDiabetes(value: Boolean) { _state.value = _state.value.copy(diabetes = value) }
    fun updateSmoker(value: Boolean) { _state.value = _state.value.copy(smoker = value) }
    fun updateHypertension(value: Boolean) { _state.value = _state.value.copy(hypertension = value) }

    fun calculateBMI() {
        val h = _state.value.height.toDoubleOrNull() ?: 0.0
        val w = _state.value.weight.toDoubleOrNull() ?: 0.0
        if (h > 0 && w > 0) {
            // BMI = weight / height^2 (weight in kg, height in meters)
            val bmi = w / (h * h)
            _state.value = _state.value.copy(bmi = String.format("%.2f", bmi))
        } else {
            _state.value = _state.value.copy(bmi = "")
        }
    }

    // ---------------------- SIGMA BETA X → USER RISK ----------------------
    private fun calculateLogHazardRatio(
        gender: String,
        age: Double,
        bloodPressure: Double,
        bmi: Double,
        hasHypertension: Boolean,
        isSmoker: Boolean,
        hasDiabetes: Boolean
    ): Double {

        val lnAge = ln(age)
        val lnSBP = ln(bloodPressure)
        val lnBMI = ln(bmi)

        val trtbp = if (hasHypertension) 1.0 else 0.0
        val smoke = if (isSmoker) 1.0 else 0.0
        val diab = if (hasDiabetes) 1.0 else 0.0

        return if (gender == "Pria") {
            // Male
            if (trtbp == 0.0) {
                // Untreated (No Hypertension History/Medication)
                lnAge * 3.11296 + lnSBP * 1.92672 + smoke * 0.70953 + lnBMI * 0.79277 + diab * 0.5316
            } else {
                // Treated (Hypertension History/Medication)
                lnAge * 3.11296 + lnSBP * 1.85508 + smoke * 0.70953 + lnBMI * 0.79277 + diab * 0.5316
            }
        } else {
            // Female
            if (trtbp == 0.0) {
                // Untreated
                lnAge * 2.72107 + lnSBP * 2.81291 + smoke * 0.61868 + lnBMI * 0.51125 + diab * 0.77763
            } else {
                // Treated
                lnAge * 2.72107 + lnSBP * 2.88267 + smoke * 0.61868 + lnBMI * 0.51125 + diab * 0.77763
            }
        }
    }

    // --------------------- SIGMA BETA X → OPTIMAL ---------------------
    private fun calculateLogHazardRatioOptimal(isMale: Boolean): Double {

        // Optimal Risk: Age 30, SBP 110, BMI 22, No Treatment/Smoke/Diabetes
        // Age ln(30) = 3.40119782
        val ageLn = 3.40119782        // FIX: langsung pakai, jangan ln()
        val sbpLn = ln(110.0)
        val bmiLn = ln(22.0)

        // For Optimal, use Treated BP coefficient as it represents lower risk
        // Smoke, Diab, Trtbp = 0
        return if (isMale) {
            3.11296 * ageLn + 1.85508 * sbpLn + 0.79277 * bmiLn // Male Treated SBP Coeff
        } else {
            2.72107 * ageLn + 2.81291 * sbpLn + 0.51125 * bmiLn // Female Untreated SBP Coeff (Using the lower of the two female coeffs)
        }
    }

    // --------------------- SIGMA BETA X → NORMAL ---------------------
    private fun calculateLogHazardRatioNormal(isMale: Boolean): Double {

        // Normal Risk: Age 30, SBP 125, BMI 22.5, No Treatment/Smoke/Diabetes
        val ageLn = ln(30.0)      // = 3.40119782
        val sbpLn = ln(125.0)
        val bmiLn = ln(22.5)

        // For Normal, use Untreated BP coefficient
        // Smoke, Diab, Trtbp = 0
        return if (isMale) {
            3.11296 * ageLn +
                    1.92672 * sbpLn + // Male Untreated SBP Coeff
                    0.79277 * bmiLn
        } else {
            2.72107 * ageLn +
                    2.81291 * sbpLn + // Female Untreated SBP Coeff
                    0.51125 * bmiLn
        }
    }

    // -------------------------- RISK SCORE --------------------------
    private fun calculateRiskDecimal(betaX: Double, isMale: Boolean): Double {

        val baselineSurvival = if (isMale) 0.88431 else 0.94833
        val baselineLogHazard = if (isMale) 23.9388 else 26.0145

        // exp(Σβx - B)
        val exponent = exp(betaX - baselineLogHazard)

        // Risk = 1 - S0 ^ exponent
        return 1.0 - baselineSurvival.pow(exponent)
    }

    // --------------------- PERHITUNGAN CONSTI (BARU) ---------------------
    // Sesuai dengan rumus yang diberikan: consti = consti_num * (1 / consti_denom)
    private fun calculateConsti(isMale: Boolean, normalBetaX: Double): Double {
        val baselineSurvival = if (isMale) 0.88431 else 0.94833 // S0
        val constB = if (isMale) 23.9388 else 26.0145 // Baseline Log Hazard (B)
        val betaAge = if (isMale) 3.11296 else 2.72107 // Beta_Age
        val expo = 1.0 / betaAge // 1/Beta_Age

        // consti num = exp( -((Σβx_Normal - const_umur) - B) / β_age )
        // Berdasarkan rumus yang Anda berikan: male consti num = exp( -((coeff sbp × sbp) + ... - const) / 3.11296 )
        // Saya akan menggunakan normalBetaX - (Beta_Age * ln(30)) sebagai 'Const_Umur' untuk memastikan konsistensi dengan model log-hazard,
        // namun untuk mematuhi rumus yang Anda berikan, saya akan menggunakan 'normalBetaX' dikurangi 'constB' karena
        // normalBetaX = (Beta_Age * ln(30)) + (faktor lain).

        // Pilihan 1: Asumsi bahwa normalBetaX sudah mencakup semua faktor
        // consti num = exp( (normalBetaX - constB) / betaAge ) // Rumus lama

        // Pilihan 2: Menggunakan rumus yang Anda berikan, yaitu bagian log hazard *tanpa* ln(age) (nilai 30)
        // normalBetaX = 3.11296 * ln(30) + (faktor-faktor lain)
        // Faktor-faktor lain = normalBetaX - (betaAge * ln(30))
        val ln30 = ln(30.0)
        val factorsOtherThanAge = normalBetaX - (betaAge * ln30)

        // Rumus yang diminta (diasumsikan semua faktor non-age adalah 'normal factors'):
        // male consti num = exp( -((coeff sbp × sbp) + ... - const) / 3.11296 )
        // Di mana (coeff sbp * sbp + ...) adalah factorsOtherThanAge
        val constiNum = exp( -(factorsOtherThanAge - constB) / betaAge )

        // consti denom = (-ln(S0))^(1/β_age)
        val constiDenom = (-ln(baselineSurvival)).pow(expo)

        // consti = consti num * (1 / consti denom)
        return constiNum * (1.0 / constiDenom)
    }

    // -------------------------- HEART AGE (DIPERBAIKI) --------------------------
    // Menghitung Heart Age dengan rumus yang telah disesuaikan
    private fun calculateHeartAge(
        isMale: Boolean,
        userRisk: Double,
        normalBetaX: Double, // Log Hazard Ratio Normal
        hasHypertension: Boolean // Status Hipertensi User
    ): Int {

        val consti = calculateConsti(isMale, normalBetaX)
        val expo = if (isMale) 1.0 / 3.11296 else 1.0 / 2.72107 // 1/Beta_Age

        // 1. Perhitungan 'term' (Term berdasarkan risiko user)
        // term = (-ln(1-risk))^(1/β_age)
        // Catatan: Rumus yang Anda berikan memisahkan Male No Treat, Male Treat, dll.
        // Di sini, kita hanya memiliki satu userRisk, dan expo (1/Beta_Age) sudah mencerminkan gender.
        val term = (-ln(1.0 - userRisk)).pow(expo)

        // 2. Heart Age = consti * term
        val heartAge = consti * term

        // Menggunakan nilai minimal 1 tahun
        return heartAge.roundToInt().coerceAtLeast(1)
    }


    // ---------------------- FINAL CALCULATION ----------------------
    fun calculateRisk(): Pair<String, Int> {

        val s = _state.value
        val age = s.age.toDoubleOrNull() ?: 0.0
        val sbp = s.bloodPressure.toDoubleOrNull() ?: 0.0
        val bmi = s.bmi.toDoubleOrNull() ?: 0.0
        val isMale = s.gender == "Pria"

        if (age <= 0 || sbp <= 0 || bmi <= 0 || s.gender.isEmpty()) {
            val fallback = age.roundToInt().coerceAtLeast(1)
            // Fallback untuk semua risiko 0.0 jika data tidak valid, dan heart age = usia asli (minimal 1)
            return Pair("0.0,0.0,0.0", fallback)
        }

        // Normal Risk (Diperlukan untuk menghitung Consti Heart Age)
        val normalBetaX = calculateLogHazardRatioNormal(isMale)
        val normalRisk = calculateRiskDecimal(normalBetaX, isMale)

        // User Risk
        val userBetaX = calculateLogHazardRatio(
            gender = s.gender,
            age = age,
            bloodPressure = sbp,
            bmi = bmi,
            hasHypertension = s.hypertension,
            isSmoker = s.smoker,
            hasDiabetes = s.diabetes
        )
        val userRisk = calculateRiskDecimal(userBetaX, isMale)

        // Optimal Risk
        val optimalBetaX = calculateLogHazardRatioOptimal(isMale)
        val optimalRisk = calculateRiskDecimal(optimalBetaX, isMale)

        // Heart age (Menggunakan userRisk, normalBetaX, dan status hipertensi user)
        val heartAge = calculateHeartAge(isMale, userRisk, normalBetaX, s.hypertension)

        val riskString = String.format("%.6f,%.6f,%.6f", userRisk, optimalRisk, normalRisk)

        return Pair(riskString, heartAge)
    }
}