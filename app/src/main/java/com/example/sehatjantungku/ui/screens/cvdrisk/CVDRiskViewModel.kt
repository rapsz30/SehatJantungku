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
                lnAge * 3.11296 + lnSBP * 1.92672 + smoke * 0.70953 + lnBMI * 0.79277 + diab * 0.5316
            } else {
                lnAge * 3.11296 + lnSBP * 1.85508 + smoke * 0.70953 + lnBMI * 0.79277 + diab * 0.5316
            }
        } else {
            // Female
            if (trtbp == 0.0) {
                lnAge * 2.72107 + lnSBP * 2.81291 + smoke * 0.61868 + lnBMI * 0.51125 + diab * 0.77763
            } else {
                lnAge * 2.72107 + lnSBP * 2.88267 + smoke * 0.61868 + lnBMI * 0.51125 + diab * 0.77763
            }
        }
    }

    // --------------------- SIGMA BETA X → OPTIMAL ---------------------
    private fun calculateLogHazardRatioOptimal(isMale: Boolean): Double {

        val ageLn = 3.40119782        // FIX: langsung pakai, jangan ln()
        val sbpLn = ln(110.0)
        val bmiLn = ln(22.0)

        return if (isMale) {
            3.11296 * ageLn + 1.85508 * sbpLn + 0.79277 * bmiLn
        } else {
            2.72107 * ageLn + 2.81291 * sbpLn + 0.51125 * bmiLn
        }
    }

    // --------------------- SIGMA BETA X → NORMAL ---------------------
    private fun calculateLogHazardRatioNormal(isMale: Boolean): Double {
        val ageLn = ln(30.0)   // 3.40119782
        val sbpLn = ln(125.0)
        val bmiLn = ln(22.5)

        return if (isMale) {
            // male = 3.11296 * ageLn + 1.85508 * ln(125) + 0.79277 * ln(22.5)
            3.11296 * ageLn +
                    1.85508 * sbpLn +
                    0.79277 * bmiLn
        } else {
            // female = 2.72107 * ageLn + 2.81291 * ln(125) + 0.51125 * ln(22.5)
            2.72107 * ageLn +
                    2.81291 * sbpLn +
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

    // ---------------------- FINAL CALCULATION ----------------------
    fun calculateRisk(): Pair<String, Int> {

        val s = _state.value
        val age = s.age.toDoubleOrNull() ?: 0.0
        val sbp = s.bloodPressure.toDoubleOrNull() ?: 0.0
        val bmi = s.bmi.toDoubleOrNull() ?: 0.0
        val isMale = s.gender == "Pria"

        if (age <= 0 || sbp <= 0 || bmi <= 0 || s.gender.isEmpty()) {
            val fallback = age.roundToInt().coerceAtLeast(1)
            return Pair("0.0,0.0,0.0", fallback)
        }

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

        val optimalBetaX = calculateLogHazardRatioOptimal(isMale)
        val optimalRisk = calculateRiskDecimal(optimalBetaX, isMale)

        val normalBetaX = calculateLogHazardRatioNormal(isMale)
        val normalRisk = calculateRiskDecimal(normalBetaX, isMale)

        // heart age placeholder
        val riskPercent = (userRisk * 100).roundToInt()
        val heartAge = (age + riskPercent * 0.25).roundToInt()
            .coerceAtLeast(age.roundToInt())

        val riskString = String.format("%.6f,%.6f,%.6f", userRisk, optimalRisk, normalRisk)

        return Pair(riskString, heartAge)
    }
}
