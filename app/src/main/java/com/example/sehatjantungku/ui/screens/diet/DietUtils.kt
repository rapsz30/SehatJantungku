package com.example.sehatjantungku.ui.screens.diet

import android.content.Context
import com.example.sehatjantungku.data.model.DietPlan
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

fun loadDietPlanFromJson(context: Context, id: String): DietPlan? {
    try {
        val inputStream = context.assets.open("dietplan.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = reader.use { it.readText() }
        val jsonObject = JSONObject(jsonString)

        if (jsonObject.has(id)) {
            val item = jsonObject.getJSONObject(id)

            // PERBAIKAN: Konversi String dari JSON ke Int
            // Menggunakan toIntOrNull() agar aman jika data kosong/error
            val idInt = item.optString("id").toIntOrNull() ?: 0
            val waktuDietInt = item.optString("waktuDiet").toIntOrNull() ?: 30

            return DietPlan(
                id = idInt, // Masukkan Int
                dietName = item.optString("dietName"),
                deskripsi = item.optString("deskripsi"),
                waktuDiet = waktuDietInt, // Masukkan Int
                aturanDiet = item.optString("aturanDiet"),

                // Parsing field lainnya (tetap String)
                sarapanA = item.optString("sarapanA"),
                sarapanB = item.optString("sarapanB"),
                sarapanC = item.optString("sarapanC"),
                tipsSarapan = item.optString("tipsSarapan"),
                waktuSarapan = item.optString("waktuSarapan"),
                deskripsiSarapan = item.optString("deskripsiSarapan"),

                makansiangA = item.optString("makansiangA"),
                makansiangB = item.optString("makansiangB"),
                makansiangC = item.optString("makansiangC"),
                tipsMakanSiang = item.optString("tipsMakanSiang"),
                waktuMakanSiang = item.optString("waktuMakanSiang"),
                deskripsiMakanSiang = item.optString("deskripsiMakanSiang"),

                makanmalamA = item.optString("makanmalamA"),
                makanmalamB = item.optString("makanmalamB"),
                makanmalamC = item.optString("makanmalamC"),
                tipsMakanMalam = item.optString("tipsMakanMalam"),
                waktuMakanMalam = item.optString("waktuMakanMalam"),
                deskripsiMakanMalam = item.optString("deskripsiMakanMalam"),

                camilanA = item.optString("camilanA"),
                camilanB = item.optString("camilanB"),
                camilanC = item.optString("camilanC"),
                tipsCamilan = item.optString("tipsCamilan"),
                waktuCamilan = item.optString("waktuCamilan"),
                deskripsiCamilan = item.optString("deskripsiCamilan")
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}