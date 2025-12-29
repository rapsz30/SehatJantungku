package com.example.sehatjantungku.data.model

import com.google.firebase.Timestamp

data class CVDRiskResult(
    val id: String = "",
    val userId: String = "",
    val date: Timestamp = Timestamp.now(),
    val heartAge: Int = 0,
    val userRiskScore: Double = 0.0,
    val riskCategory: String = "",
    val inputSummary: String = ""
)