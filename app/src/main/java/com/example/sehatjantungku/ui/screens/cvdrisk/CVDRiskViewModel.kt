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

// ... (Data class CVDRiskState tetap sama) ...
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

    // State untuk status penyimpanan
    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()

    // ... (Fungsi update input dan calculateBMI tetap sama, jangan dihapus) ...
    // Copy-paste fungsi updateGender, updateAge, dll dari kode lama Anda di sini...
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


    // ... (Fungsi Kalkulasi Log Hazard tetap sama, jangan dihapus) ...
    // Copy-paste fungsi calculateLogHazardRatio, Normal, Optimal, Consti, HeartAge, dll dari kode lama...

    private fun calculateLogHazardRatio(gender: String, age: Double, bloodPressure: Double, bmi: Double, hasHypertension: Boolean, isSmoker: Boolean, hasDiabetes: Boolean): Double {
        val lnAge = ln(age); val lnSBP = ln(bloodPressure); val lnBMI = ln(bmi)
        val trtbp = if (hasHypertension) 1.0 else 0.0; val smoke = if (isSmoker) 1.0 else 0.0; val diab = if (hasDiabetes) 1.0 else 0.0
        return if (gender == "Pria") {
            if (trtbp == 0.0) lnAge * 3.11296 + lnSBP * 1.92672 + smoke * 0.70953 + lnBMI * 0.79277 + diab * 0.5316
            else lnAge * 3.11296 + lnSBP * 1.85508 + smoke * 0.70953 + lnBMI * 0.79277 + diab * 0.5316
        } else {
            if (trtbp == 0.0) lnAge * 2.72107 + lnSBP * 2.81291 + smoke * 0.61868 + lnBMI * 0.51125 + diab * 0.77763
            else lnAge * 2.72107 + lnSBP * 2.88267 + smoke * 0.61868 + lnBMI * 0.51125 + diab * 0.77763
        }
    }

    // ... Pastikan fungsi calculateLogHazardRatioOptimal, calculateLogHazardRatioNormal, calculateRiskDecimal, calculateConsti, calculateHeartAge ada di sini ...
    // Saya menyingkatnya agar jawaban tidak terlalu panjang, tapi logika hitungannya SAMA PERSIS dengan file lama Anda.

    private fun calculateLogHazardRatioOptimal(isMale: Boolean): Double {
        val ageLn = 3.40119782; val sbpLn = ln(110.0); val bmiLn = ln(22.0)
        return if (isMale) 3.11296 * ageLn + 1.85508 * sbpLn + 0.79277 * bmiLn else 2.72107 * ageLn + 2.81291 * sbpLn + 0.51125 * bmiLn
    }
    private fun calculateLogHazardRatioNormal(isMale: Boolean): Double {
        val ageLn = ln(30.0); val sbpLn = ln(125.0); val bmiLn = ln(22.5)
        return if (isMale) 3.11296 * ageLn + 1.92672 * sbpLn + 0.79277 * bmiLn else 2.72107 * ageLn + 2.81291 * sbpLn + 0.51125 * bmiLn
    }
    private fun calculateRiskDecimal(betaX: Double, isMale: Boolean): Double {
        val baselineSurvival = if (isMale) 0.88431 else 0.94833
        val baselineLogHazard = if (isMale) 23.9388 else 26.0145
        return 1.0 - baselineSurvival.pow(exp(betaX - baselineLogHazard))
    }
    private fun calculateConsti(isMale: Boolean, normalBetaX: Double): Double {
        val baselineSurvival = if (isMale) 0.88431 else 0.94833
        val constB = if (isMale) 23.9388 else 26.0145
        val betaAge = if (isMale) 3.11296 else 2.72107
        val expo = 1.0 / betaAge
        val ln30 = ln(30.0)
        val factorsOtherThanAge = normalBetaX - (betaAge * ln30)
        val constiNum = exp( -(factorsOtherThanAge - constB) / betaAge )
        val constiDenom = (-ln(baselineSurvival)).pow(expo)
        return constiNum * (1.0 / constiDenom)
    }
    private fun calculateHeartAge(isMale: Boolean, userRisk: Double, normalBetaX: Double, hasHypertension: Boolean): Int {
        val consti = calculateConsti(isMale, normalBetaX)
        val expo = if (isMale) 1.0 / 3.11296 else 1.0 / 2.72107
        val term = (-ln(1.0 - userRisk)).pow(expo)
        val heartAge = consti * term
        return heartAge.roundToInt().coerceAtLeast(1)
    }

    // ... (calculateRisk function tetap sama) ...
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
        val normalBetaX = calculateLogHazardRatioNormal(isMale)
        val normalRisk = calculateRiskDecimal(normalBetaX, isMale)
        val userBetaX = calculateLogHazardRatio(s.gender, age, sbp, bmi, s.hypertension, s.smoker, s.diabetes)
        val userRisk = calculateRiskDecimal(userBetaX, isMale)
        val optimalBetaX = calculateLogHazardRatioOptimal(isMale)
        val optimalRisk = calculateRiskDecimal(optimalBetaX, isMale)
        val heartAge = calculateHeartAge(isMale, userRisk, normalBetaX, s.hypertension)

        val riskString = String.format("%.6f,%.6f,%.6f", userRisk, optimalRisk, normalRisk)
        return Pair(riskString, heartAge)
    }

    // ---------------------- FUNGSI SAVE KE FIREBASE ----------------------
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
                inputSummary = "${_state.value.gender}, ${_state.value.age}th"
            )

            FirebaseFirestore.getInstance().collection("cvd_history")
                .add(resultData)
                .addOnSuccessListener {
                    _saveStatus.value = SaveStatus.Success
                }
                .addOnFailureListener { e ->
                    _saveStatus.value = SaveStatus.Error(e.message ?: "Gagal menyimpan")
                }
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