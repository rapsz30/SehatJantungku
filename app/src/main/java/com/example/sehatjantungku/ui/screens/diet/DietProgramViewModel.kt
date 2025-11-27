package com.example.sehatjantungku.ui.screens.diet

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DietProgramState(
    val goal: String = "",
    val saltReduction: String = "",
    val fatReduction: String = "",
    val vegetablePreference: String = "",
    val meatRestriction: String = "",
    val dailyVegetable: String = ""
)

data class DietResult(
    val bestDiet: String,
    val scores: List<Int>
)

class DietProgramViewModel : ViewModel() {
    private val _state = MutableStateFlow(DietProgramState())
    val state: StateFlow<DietProgramState> = _state

    fun updateGoal(value: String) {
        _state.value = _state.value.copy(goal = value)
    }

    fun updateSaltReduction(value: String) {
        _state.value = _state.value.copy(saltReduction = value)
    }

    fun updateFatReduction(value: String) {
        _state.value = _state.value.copy(fatReduction = value)
    }

    fun updateVegetablePreference(value: String) {
        _state.value = _state.value.copy(vegetablePreference = value)
    }

    fun updateMeatRestriction(value: String) {
        _state.value = _state.value.copy(meatRestriction = value)
    }

    fun updateDailyVegetable(value: String) {
        _state.value = _state.value.copy(dailyVegetable = value)
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

        // Calculate scores based on answers
        when (currentState.goal) {
            "Menurunkan berat badan" -> {
                scores["Low-Fat"] = scores["Low-Fat"]!! + 3
                scores["Plant-Based"] = scores["Plant-Based"]!! + 2
            }
            "Menjaga berat badan" -> {
                scores["Mediterranean"] = scores["Mediterranean"]!! + 3
                scores["DASH"] = scores["DASH"]!! + 2
            }
        }

        when (currentState.saltReduction) {
            "Sangat ketat" -> scores["Low-Sodium"] = scores["Low-Sodium"]!! + 4
            "Cukup ketat" -> {
                scores["DASH"] = scores["DASH"]!! + 3
                scores["Low-Sodium"] = scores["Low-Sodium"]!! + 2
            }
        }

        when (currentState.fatReduction) {
            "Ya, sangat ingin" -> scores["Low-Fat"] = scores["Low-Fat"]!! + 4
            "Ingin sedikit saja" -> scores["Mediterranean"] = scores["Mediterranean"]!! + 2
        }

        when (currentState.vegetablePreference) {
            "Ya" -> {
                scores["Plant-Based"] = scores["Plant-Based"]!! + 3
                scores["DASH"] = scores["DASH"]!! + 2
            }
        }

        when (currentState.meatRestriction) {
            "Ya, batasi banyak" -> scores["Plant-Based"] = scores["Plant-Based"]!! + 4
            "Batasi sedikit" -> scores["Mediterranean"] = scores["Mediterranean"]!! + 2
        }

        when (currentState.dailyVegetable) {
            "Sangat nyaman" -> {
                scores["Plant-Based"] = scores["Plant-Based"]!! + 3
                scores["DASH"] = scores["DASH"]!! + 2
            }
        }

        val bestDiet = scores.maxByOrNull { it.value }?.key ?: "DASH"
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
