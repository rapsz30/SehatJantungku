package com.example.sehatjantungku.ui.screens.chatbot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatjantungku.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ChatMessage(
    val message: String,
    val isUser: Boolean,
    val isError: Boolean = false
)

class ChatbotViewModel : ViewModel() {

    private val _messages = MutableStateFlow(
        listOf(
            ChatMessage(
                message = "Halo! Saya asisten kesehatan jantung Anda.\n\n" +
                        "Saya bisa membantu menjelaskan hasil risiko jantung Anda, program diet, atau info seputar Framingham Heart Study.\n\n" +
                        "⚠️ Harap diingat: Saya bukan pengganti dokter.",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Chat Session agar AI ingat konteks percakapan
    private var chatSession: com.google.ai.client.generativeai.Chat? = null

    // Context String (Data User)
    private var userContextInfo = "Data Pengguna: Belum dimuat."

    init {
        // Load data dulu, baru inisialisasi AI dengan konteks data tersebut
        viewModelScope.launch {
            fetchUserHealthContext()
            initializeGeminiChat()
        }
    }

    // 1. Ambil Data Kesehatan User dari Firestore
    private suspend fun fetchUserHealthContext() {
        val userId = auth.currentUser?.uid ?: return
        var cvdInfo = "Belum ada tes risiko CVD."
        var dietInfo = "Belum mengikuti program diet."

        try {
            // Ambil CVD Terakhir
            val cvdSnapshot = db.collection("cvd_history")
                .whereEqualTo("userId", userId)
                .get().await()

            if (!cvdSnapshot.isEmpty) {
                // Cari yang tanggalnya paling baru (manual sort jika query index belum ada)
                val latest = cvdSnapshot.documents.maxByOrNull { it.getTimestamp("date")?.seconds ?: 0L }
                latest?.let {
                    val score = it.getDouble("userRiskScore") ?: 0.0
                    val category = it.getString("riskCategory") ?: "-"
                    cvdInfo = "Skor Risiko Framingham Terakhir: ${(score * 100).toInt()}%, Kategori: $category."
                }
            }

            // Ambil Diet Aktif
            val dietDoc = db.collection("users").document(userId)
                .collection("diet_program").document("active_diet")
                .get().await()

            if (dietDoc.exists()) {
                val dietName = dietDoc.getString("dietName") ?: "-"
                val day = dietDoc.getLong("currentDay") ?: 1
                val isCompleted = dietDoc.getBoolean("isCompleted") ?: false
                if (!isCompleted) {
                    dietInfo = "Sedang menjalani $dietName, Hari ke-$day."
                } else {
                    dietInfo = "Telah menyelesaikan program $dietName."
                }
            }

        } catch (e: Exception) {
            Log.e("ChatbotViewModel", "Error fetching data: ${e.message}")
        }

        userContextInfo = """
            DATA PENGGUNA SAAT INI (Gunakan ini untuk personalisasi jawaban):
            - $cvdInfo
            - $dietInfo
        """.trimIndent()
    }

    private fun initializeGeminiChat() {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "null") {
            addBotMessage("Error: API Key tidak ditemukan.", isError = true)
            return
        }

        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = apiKey
            )

            // 2. Setting "Otak" / Persona Chatbot (System Instruction)
            val systemInstruction = """
                Kamu adalah Asisten Kesehatan Jantung untuk aplikasi 'SehatJantungku'.
                
                TUGAS UTAMA:
                Membantu pengguna memahami kesehatan jantung, hasil risiko CVD (Framingham Heart Study), diet jantung (DASH, Mediterania), dan gaya hidup sehat.
                
                $userContextInfo
                
                BATASAN & ATURAN PENTING (WAJIB DIPATUHI):
                1. Sistem TIDAK digunakan untuk diagnosis medis maupun penentuan terapi klinis.
                2. Rekomendasi yang diberikan bersifat SARAN dan pendukung keputusan, BUKAN keputusan final.
                3. Pengguna TETAP DISARANKAN untuk berkonsultasi dengan tenaga kesehatan profesional untuk penanganan medis lebih lanjut.
                4. Jika pengguna bertanya tentang diagnosis penyakit spesifik (misal: "Apakah saya kena serangan jantung sekarang?"), segera suruh ke IGD/Dokter.
                5. Jika ditanya hal di luar topik kesehatan jantung (misal: coding, politik, resep kue non-sehat), tolak dengan sopan dan arahkan kembali ke topik jantung.
                6. Jawablah dengan empatik, suportif, dan berbasis data medis umum yang valid.
            """.trimIndent()

            // Inisialisasi History Chat dengan System Instruction di awal
            chatSession = generativeModel.startChat(
                history = listOf(
                    content(role = "user") { text(systemInstruction) },
                    content(role = "model") { text("Mengerti. Saya siap membantu sebagai asisten kesehatan jantung dengan mematuhi semua batasan medis tersebut.") }
                )
            )

        } catch (e: Exception) {
            addBotMessage("Gagal inisialisasi AI: ${e.localizedMessage}", isError = true)
        }
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        // Update UI: Pesan User
        val currentList = _messages.value.toMutableList()
        currentList.add(ChatMessage(userMessage, isUser = true))
        _messages.value = currentList

        if (chatSession == null) {
            // Coba init ulang jika null (misal internet putus di awal)
            viewModelScope.launch {
                initializeGeminiChat()
                if (chatSession == null) {
                    addBotMessage("Sistem AI sedang memuat data...", isError = true)
                    return@launch
                }
                processResponse(userMessage)
            }
        } else {
            processResponse(userMessage)
        }
    }

    private fun processResponse(message: String) {
        viewModelScope.launch {
            try {
                // Loading indicator
                val loadingMsg = ChatMessage("Sedang menganalisis...", isUser = false)
                _messages.value = _messages.value + loadingMsg

                // Kirim pesan ke Chat Session (History otomatis tersimpan)
                val response = chatSession!!.sendMessage(message)
                val responseText = response.text ?: "Maaf, saya tidak mengerti."

                // Hapus loading, masukkan jawaban
                _messages.value = _messages.value.dropLast(1) + ChatMessage(responseText, isUser = false)

            } catch (e: Exception) {
                _messages.value = _messages.value.dropLast(1)
                addBotMessage("Gagal terhubung. Pastikan koneksi internet lancar.", isError = true)
            }
        }
    }

    private fun addBotMessage(text: String, isError: Boolean = false) {
        val updatedList = _messages.value.toMutableList()
        updatedList.add(ChatMessage(text, isUser = false, isError = isError))
        _messages.value = updatedList
    }
}