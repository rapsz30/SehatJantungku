package com.example.sehatjantungku.ui.screens.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.sehatjantungku.BuildConfig

data class ChatMessage(
    val message: String,
    val isUser: Boolean
)

class ChatbotViewModel : ViewModel() {
    init {
        println("DEBUG GEMINI KEY = '${BuildConfig.GEMINI_API_KEY}'")
    }


    // State chat yang diamati UI
    private val _messages = MutableStateFlow(
        listOf(
            ChatMessage(
                message = "Halo! Saya Asisten Kesehatan Jantung Anda. " +
                        "Silakan tanyakan apa pun seputar kesehatan jantung, diet, atau gaya hidup sehat ❤️",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()


    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        // PERBAIKAN UTAMA: Mengambil API Key dari BuildConfig yang dibuat oleh Gradle
        // Sebelumnya: apiKey = System.getProperty("GEMINI_API_KEY") ?: throw IllegalStateException("GEMINI_API_KEY not found")
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    // Inisialisasi chat dengan history
    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") { text("Halo, saya ingin bertanya tentang kesehatan jantung.") },
            content(role = "model") { text("Tentu! Saya Asisten Kesehatan Jantung Anda. Tanyakan apa pun seputar kesehatan jantung, diet, atau gaya hidup sehat. Saya akan bantu.") }
        )
    )

    // Kirim pesan ke Gemini
    fun sendMessage(message: String) {
        if (message.isBlank()) return

        // 1. Tambahkan pesan user ke state
        _messages.value = _messages.value + ChatMessage(
            message = message,
            isUser = true
        )

        // 2. Tambahkan placeholder loading
        _messages.value = _messages.value + ChatMessage(
            message = "Mengetik...",
            isUser = false
        )

        viewModelScope.launch {
            try {
                // 3. Kirim pesan ke Gemini
                val response = chat.sendMessage(message)

                // 4. Hapus pesan loading
                val updatedMessages = _messages.value.dropLast(1)

                // 5. Ambil teks respons (atau pesan error default)
                val aiText = response.text
                    ?: "Maaf, saya belum bisa memberikan jawaban saat ini."

                // 6. Tambahkan respons AI yang valid
                _messages.value = updatedMessages + ChatMessage(
                    message = aiText,
                    isUser = false
                )

            } catch (e: Exception) {
                // 7. Tangani error (API, network, dll)
                val updatedMessages = _messages.value.dropLast(1)
                _messages.value = updatedMessages + ChatMessage(
                    message = "Terjadi kesalahan saat menghubungi layanan AI: ${e.message}. Silakan coba lagi nanti.",
                    isUser = false
                )
            }
        }
    }
}