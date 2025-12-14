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
        println("DEBUG GEMINI API KEY LOADED = '${BuildConfig.GEMINI_API_KEY}'")
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


    // Pengecekan aman untuk kunci yang tidak boleh kosong
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val generativeModel = if (apiKey.isNotBlank()) {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey
        )
    } else {
        // Menangani kasus jika kunci kosong (tidak akan crash, tapi tidak berfungsi)
        // Kita bisa membuat instance dengan kunci placeholder atau melempar pengecualian
        // yang lebih informatif di sini, tapi untuk saat ini, kita gunakan nilai kosong
        // untuk memastikan kode bisa di-build.
        throw IllegalStateException("API Key tidak ditemukan di BuildConfig.")
    }

    // Ganti ini untuk menggunakan instance chat yang sudah dibuat
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