package com.example.sehatjantungku.data.model

data class DietPlan(
    val id: Int = 0,
    val dietName: String = "",
    val deskripsi: String = "",
    val waktuDiet: Int = 30,
    val aturanDiet: String = "",

    // Menu & Waktu
    val sarapanA: String = "",
    val sarapanB: String = "",
    val sarapanC: String = "",
    val tipsSarapan: String = "",
    val waktuSarapan: String = "",
    val deskripsiSarapan: String = "",

    val makansiangA: String = "",
    val makansiangB: String = "",
    val makansiangC: String = "",
    val tipsMakanSiang: String = "",
    val waktuMakanSiang: String = "",
    val deskripsiMakanSiang: String = "",

    val makanmalamA: String = "",
    val makanmalamB: String = "",
    val makanmalamC: String = "",
    val tipsMakanMalam: String = "",
    val waktuMakanMalam: String = "",
    val deskripsiMakanMalam: String = "",

    val camilanA: String = "",
    val camilanB: String = "",
    val camilanC: String = "",
    val tipsCamilan: String = "",
    val waktuCamilan: String = "",
    val deskripsiCamilan: String = ""
)