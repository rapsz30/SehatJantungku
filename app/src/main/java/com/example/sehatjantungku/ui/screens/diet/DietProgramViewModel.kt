package com.example.sehatjantungku.ui.screens.diet

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DietProgramState(
    val bloodPressure: String = "",
    val cholesterol: String = "",
    val healthConditions: Set<String> = emptySet(),
    val foodPreference: String = "",
    val activityLevel: String = "",
    val commitment: String = "",
    val useCVDData: Boolean = false
)

data class DietResult(
    val bestDiet: String,
    val scores: List<Int>
)

class DietProgramViewModel : ViewModel() {
    private val _state = MutableStateFlow(DietProgramState())
    val state: StateFlow<DietProgramState> = _state

    fun updateBloodPressure(value: String) {
        _state.value = _state.value.copy(bloodPressure = value)
    }

    fun updateCholesterol(value: String) {
        _state.value = _state.value.copy(cholesterol = value)
    }

    fun toggleHealthCondition(condition: String) {
        val current = _state.value.healthConditions.toMutableSet()
        if (current.contains(condition)) {
            current.remove(condition)
        } else {
            current.add(condition)
        }
        _state.value = _state.value.copy(healthConditions = current)
    }

    fun updateFoodPreference(value: String) {
        _state.value = _state.value.copy(foodPreference = value)
    }

    fun updateActivityLevel(value: String) {
        _state.value = _state.value.copy(activityLevel = value)
    }

    fun updateCommitment(value: String) {
        _state.value = _state.value.copy(commitment = value)
    }

    fun updateUseCVDData(value: Boolean) {
        _state.value = _state.value.copy(useCVDData = value)
    }

    fun calculateDiet(): DietResult {
        val scores = mutableMapOf(
            "Plant-Based" to 0,
            "DASH" to 0,
            "Mediterranean" to 0,
            "Low-Sodium" to 0,
            "Low-Fat" to 0
        )

        val currentState = _state.value

        // Question 1: Blood Pressure
        when (currentState.bloodPressure) {
            "Normal / terkontrol" -> {
                scores["Mediterranean"] = scores["Mediterranean"]!! + 2
            }
            "Kadang tinggi" -> {
                scores["DASH"] = scores["DASH"]!! + 3
                scores["Low-Sodium"] = scores["Low-Sodium"]!! + 2
            }
            "Sering tinggi / didiagnosis darah tinggi" -> {
                scores["DASH"] = scores["DASH"]!! + 4
                scores["Low-Sodium"] = scores["Low-Sodium"]!! + 3
            }
            "Tidak tahu" -> {
                scores["Mediterranean"] = scores["Mediterranean"]!! + 1
            }
        }

        // Question 2: Cholesterol
        when (currentState.cholesterol) {
            "Normal" -> {
                scores["Mediterranean"] = scores["Mediterranean"]!! + 2
            }
            "Agak tinggi" -> {
                scores["Low-Fat"] = scores["Low-Fat"]!! + 2
                scores["Plant-Based"] = scores["Plant-Based"]!! + 2
            }
            "Tinggi / pernah disarankan diet" -> {
                scores["Low-Fat"] = scores["Low-Fat"]!! + 4
                scores["Plant-Based"] = scores["Plant-Based"]!! + 3
            }
            "Tidak tahu" -> {
                scores["Mediterranean"] = scores["Mediterranean"]!! + 1
            }
        }

        // Question 3: Health Conditions
        if (currentState.healthConditions.contains("Tekanan darah tinggi")) {
            scores["DASH"] = scores["DASH"]!! + 3
            scores["Low-Sodium"] = scores["Low-Sodium"]!! + 2
        }
        if (currentState.healthConditions.contains("Kolesterol tinggi")) {
            scores["Low-Fat"] = scores["Low-Fat"]!! + 3
            scores["Plant-Based"] = scores["Plant-Based"]!! + 2
        }
        if (currentState.healthConditions.contains("Diabetes")) {
            scores["Mediterranean"] = scores["Mediterranean"]!! + 3
            scores["Plant-Based"] = scores["Plant-Based"]!! + 2
        }
        if (currentState.healthConditions.contains("Asam urat")) {
            scores["Low-Fat"] = scores["Low-Fat"]!! + 2
            scores["Mediterranean"] = scores["Mediterranean"]!! + 2
        }

        // Question 4: Food Preference
        when (currentState.foodPreference) {
            "Nabati dominan" -> {
                scores["Plant-Based"] = scores["Plant-Based"]!! + 4
                scores["DASH"] = scores["DASH"]!! + 2
            }
            "Hewani dominan" -> {
                scores["Low-Fat"] = scores["Low-Fat"]!! + 2
            }
            "Nabati dan hewani seimbang" -> {
                scores["Mediterranean"] = scores["Mediterranean"]!! + 3
                scores["DASH"] = scores["DASH"]!! + 2
            }
            "Praktis / instan (makanan cepat saji atau instan)" -> {
                scores["Low-Sodium"] = scores["Low-Sodium"]!! + 2
            }
        }

        // Question 5: Activity Level
        when (currentState.activityLevel) {
            "Jarang bergerak (lebih banyak duduk)" -> {
                scores["Low-Fat"] = scores["Low-Fat"]!! + 2
            }
            "Kadang aktif (jalan santai, aktivitas ringan)" -> {
                scores["Mediterranean"] = scores["Mediterranean"]!! + 2
            }
            "Cukup aktif (olahraga ringan 2-3x/minggu)" -> {
                scores["Mediterranean"] = scores["Mediterranean"]!! + 3
                scores["DASH"] = scores["DASH"]!! + 2
            }
            "Sangat aktif (olahraga rutin ≥4x/minggu)" -> {
                scores["Mediterranean"] = scores["Mediterranean"]!! + 3
                scores["Plant-Based"] = scores["Plant-Based"]!! + 2
            }
        }

        // Question 6: Commitment
        when (currentState.commitment) {
            "Sulit" -> {
                scores["Low-Sodium"] = scores["Low-Sodium"]!! + 1
            }
            "Lumayan" -> {
                scores["Mediterranean"] = scores["Mediterranean"]!! + 1
            }
            "Cukup yakin" -> {
                scores["DASH"] = scores["DASH"]!! + 2
                scores["Mediterranean"] = scores["Mediterranean"]!! + 2
            }
            "Sangat yakin" -> {
                scores["Plant-Based"] = scores["Plant-Based"]!! + 3
                scores["DASH"] = scores["DASH"]!! + 2
            }
        }

        val bestDiet = scores.maxByOrNull { it.value }?.key ?: "Mediterranean"
        val scoresList = listOf(
            scores["Plant-Based"] ?: 0,
            scores["DASH"] ?: 0,
            scores["Mediterranean"] ?: 0,
            scores["Low-Sodium"] ?: 0,
            scores["Low-Fat"] ?: 0
        )

        return DietResult(bestDiet, scoresList)
    }
}
