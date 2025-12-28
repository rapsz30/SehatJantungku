package com.example.sehatjantungku.ui.screens.chatbot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatjantungku.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class ChatMessage(
    val message: String,
    val isUser: Boolean,
    val isError: Boolean = false
)

class ChatbotViewModel : ViewModel() {

    private val _messages = MutableStateFlow(
        listOf(
            ChatMessage(
                message = "Halo! Saya asisten kesehatan jantung Anda. Ada yang bisa saya bantu hari ini?",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private var generativeModel: GenerativeModel? = null

    init {
        initializeGemini()
    }

    private fun initializeGemini() {
        val apiKey = BuildConfig.GEMINI_API_KEY
        Log.d("ChatbotViewModel", "API Key Loaded: ${if (apiKey.isNotBlank()) "YES" else "NO"}")

        if (apiKey.isNotBlank() && apiKey != "null") {
            try {
                generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = apiKey
                )
            } catch (e: Exception) {
                Log.e("ChatbotViewModel", "Error init Gemini: ${e.message}")
                addBotMessage("Gagal menginisialisasi sistem AI. ${e.localizedMessage}", isError = true)
            }
        } else {
            addBotMessage("Error: API Key tidak ditemukan. Pastikan local.properties sudah benar.", isError = true)
        }
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        // 1. Tampilkan pesan user
        val currentList = _messages.value.toMutableList()
        currentList.add(ChatMessage(userMessage, isUser = true))
        _messages.value = currentList

        val model = generativeModel
        if (model == null) {
            addBotMessage("Layanan AI belum siap (API Key Missing/Invalid).", isError = true)
            return
        }

        // 2. Tampilkan indikator loading (optional)
        // Disini kita langsung kirim request
        viewModelScope.launch {
            try {
                // Tambah pesan loading sementara
                val loadingMsg = ChatMessage("Sedang mengetik...", isUser = false)
                _messages.value = _messages.value + loadingMsg

                val response = model.generateContent(userMessage)
                val responseText = response.text ?: "Maaf, saya tidak mengerti."

                // Hapus pesan loading dan masukkan jawaban asli
                _messages.value = _messages.value.dropLast(1) + ChatMessage(responseText, isUser = false)

            } catch (e: Exception) {
                // Hapus pesan loading dan masukkan pesan error
                _messages.value = _messages.value.dropLast(1)
                addBotMessage("Gagal terhubung: ${e.localizedMessage}. Cek koneksi internet Anda.", isError = true)
                Log.e("ChatbotViewModel", "Error generateContent: ${e.message}")
            }
        }
    }

    private fun addBotMessage(text: String, isError: Boolean = false) {
        val updatedList = _messages.value.toMutableList()
        updatedList.add(ChatMessage(text, isUser = false, isError = isError))
        _messages.value = updatedList
    }
}