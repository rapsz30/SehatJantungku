package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.pow

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
        }
    }
    
    fun calculateRisk(): Pair<Int, Int> {
        var riskScore = 0
        
        // Age factor
        val age = _state.value.age.toIntOrNull() ?: 0
        riskScore += when {
            age > 65 -> 30
            age > 55 -> 20
            age > 45 -> 10
            else -> 5
        }
        
        // BMI factor
        val bmi = _state.value.bmi.toDoubleOrNull() ?: 0.0
        riskScore += when {
            bmi > 30 -> 25
            bmi > 25 -> 15
            else -> 5
        }
        
        // Blood pressure
        val bp = _state.value.bloodPressure.toIntOrNull() ?: 0
        riskScore += when {
            bp > 140 -> 20
            bp > 130 -> 15
            else -> 5
        }
        
        // Health conditions
        if (_state.value.diabetes) riskScore += 15
        if (_state.value.smoker) riskScore += 15
        if (_state.value.hypertension) riskScore += 15
        
        // Heart age calculation (simplified)
        val heartAge = age + (riskScore / 5)
        
        return Pair(riskScore, heartAge)
    }
}
