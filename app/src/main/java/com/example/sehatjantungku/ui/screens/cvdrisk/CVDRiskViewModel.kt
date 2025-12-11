package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.ln // Natural logarithm (basis e)
import kotlin.math.exp // e^x
import kotlin.math.roundToInt
import kotlin.math.pow // base^exponent

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

    fun updateGender(gender: String) {
        _state.value = _state.value.copy(gender = gender)
    }

    fun updateAge(age: String) {
        _state.value = _state.value.copy(age = age)
    }

    fun updateHeight(height: String) {
        _state.value = _state.value.copy(height = height)
    }

    fun updateWeight(weight: String) {
        _state.value = _state.value.copy(weight = weight)
    }

    fun updateBloodPressure(bp: String) {
        _state.value = _state.value.copy(bloodPressure = bp)
    }

    fun updateDiabetes(value: Boolean) {
        _state.value = _state.value.copy(diabetes = value)
    }

    fun updateSmoker(value: Boolean) {
        _state.value = _state.value.copy(smoker = value)
    }

    fun updateHypertension(value: Boolean) {
        _state.value = _state.value.copy(hypertension = value)
    }

    fun calculateBMI() {
        val height = _state.value.height.toDoubleOrNull() ?: 0.0
        val weight = _state.value.weight.toDoubleOrNull() ?: 0.0

        if (height > 0 && weight > 0) {
            val bmi = weight / (height.pow(2))
            _state.value = _state.value.copy(bmi = String.format("%.2f", bmi))
        } else {
            _state.value = _state.value.copy(bmi = "")
        }
    }

    /**
     * Helper function untuk menghitung Σβx Risk (untuk user).
     */
    private fun calculateLogHazardRatio(
        gender: String,
        age: Double,
        bloodPressure: Double,
        bmi: Double,
        hasHypertension: Boolean,
        isSmoker: Boolean,
        hasDiabetes: Boolean
    ): Double {
        // M Values (Variabel Biner dan Logaritma)
        val trtbp = if (hasHypertension) 1.0 else 0.0
        val smoke = if (isSmoker) 1.0 else 0.0
        val diab = if (hasDiabetes) 1.0 else 0.0

        val umur = if (age > 0) ln(age) else 0.0
        val sbp = if (bloodPressure > 0) ln(bloodPressure) else 0.0
        val bmindex = if (bmi > 0) ln(bmi) else 0.0

        return if (gender == "Pria") {
            // Male
            if (trtbp == 0.0) {
                // Male No Treatment
                umur * 3.11296 + sbp * 1.92672 + smoke * 0.70953 + bmindex * 0.79277 + diab * 0.5316
            } else {
                // Male Treatment
                umur * 3.11296 + sbp * 1.85508 + smoke * 0.70953 + bmindex * 0.79277 + diab * 0.5316
            }
        } else {
            // Female
            if (trtbp == 0.0) {
                // Female No Treatment
                umur * 2.72107 + sbp * 2.81291 + smoke * 0.61868 + bmindex * 0.51125 + diab * 0.77763
            } else {
                // Female Treatment
                umur * 2.72107 + sbp * 2.88267 + smoke * 0.61868 + bmindex * 0.51125 + diab * 0.77763
            }
        }
    }

    /**
     * Helper function untuk menghitung Σβx Optimal.
     */
    private fun calculateLogHazardRatioOptimal(isMale: Boolean): Double {
        val ageLn = ln(3.40119782) // Asumsi 3.40119782 adalah Age Ln Optimal
        val sbpLn = ln(110.0)
        val bmiLn = ln(22.0)

        return if (isMale) {
            3.11296 * ageLn + 1.85508 * sbpLn + 0.79277 * bmiLn
        } else {
            2.72107 * ageLn + 2.81291 * sbpLn + 0.51125 * bmiLn
        }
    }

    /**
     * Helper function untuk menghitung Σβx Normal.
     */
    private fun calculateLogHazardRatioNormal(isMale: Boolean): Double {
        val ageLn = ln(3.40119782) // Asumsi 3.40119782 adalah Age Ln Normal
        val sbpLnMale = ln(125.0)
        val sbpLnFemale = ln(110.0)
        val bmiLnMale = ln(22.0)
        val bmiLnFemale = ln(22.5)

        return if (isMale) {
            3.11296 * ageLn + 1.85508 * sbpLnMale + 0.79277 * bmiLnMale
        } else {
            2.72107 * ageLn + 2.81291 * sbpLnFemale + 0.51125 * bmiLnFemale
        }
    }


    /**
     * Helper function untuk menghitung Risk Score (desimal murni) dari Σβx.
     * Formula: 1 - BaselineSurvival^exp(Σβx - BaselineLogHazardRatio)
     */
    private fun calculateRiskDecimal(riskLogHazardRatio: Double, isMale: Boolean): Double {
        val baselineSurvival = if (isMale) 0.88431 else 0.94833
        val baselineLogHazardRatio = if (isMale) 23.9388 else 26.0145

        // Exponent = exp(Σβx - Baseline Log Hazard Ratio)
        val exponent = exp(riskLogHazardRatio - baselineLogHazardRatio)

        // Risk Score = 1 - BaselineSurvival^Exponent
        return 1.0 - baselineSurvival.pow(exponent)
    }

    /**
     * Menghitung semua Risk Scores.
     * Mengembalikan Pair<String, Int> di mana:
     * - First (String): Comma-separated string of risk scores (UserRisk, OptimalRisk, NormalRisk) dalam format desimal.
     * - Second (Int): Heart Age
     */
    fun calculateRisk(): Pair<String, Int> {
        val currentState = _state.value
        val age = currentState.age.toDoubleOrNull() ?: 0.0
        val bp = currentState.bloodPressure.toDoubleOrNull() ?: 0.0
        val bmi = currentState.bmi.toDoubleOrNull() ?: 0.0
        val isMale = (currentState.gender == "Pria")

        if (age <= 0 || bp <= 0 || bmi <= 0 || currentState.gender.isEmpty()) {
            val defaultAge = age.roundToInt().coerceAtLeast(1)
            return Pair("0.0,0.0,0.0", defaultAge)
        }

        // --- 1. Hitung User Risk ---
        val sigmaBetaXRisk = calculateLogHazardRatio(
            gender = currentState.gender,
            age = age,
            bloodPressure = bp,
            bmi = bmi,
            hasHypertension = currentState.hypertension,
            isSmoker = currentState.smoker,
            hasDiabetes = currentState.diabetes
        )
        val userRisk = calculateRiskDecimal(sigmaBetaXRisk, isMale)

        // --- 2. Hitung Optimal Risk ---
        val sigmaBetaXOptimal = calculateLogHazardRatioOptimal(isMale)
        val optimalRisk = calculateRiskDecimal(sigmaBetaXOptimal, isMale)

        // --- 3. Hitung Normal Risk ---
        val sigmaBetaXNormal = calculateLogHazardRatioNormal(isMale)
        val normalRisk = calculateRiskDecimal(sigmaBetaXNormal, isMale)

        // --- 4. Heart Age Placeholder (Menggunakan rumus simulasi lama) ---
        val userRiskPercentage = (userRisk * 100).roundToInt().coerceIn(0, 100)
        val heartAgePlaceholder = (age + (userRiskPercentage * 0.25)).roundToInt().coerceAtLeast(age.roundToInt())

        // Format hasil sebagai string: "UserRisk,OptimalRisk,NormalRisk" (hingga 6 desimal)
        val riskString = String.format("%.6f,%.6f,%.6f", userRisk, optimalRisk, normalRisk)

        return Pair(riskString, heartAgePlaceholder)
    }
}