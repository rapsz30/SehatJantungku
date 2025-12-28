package com.example.sehatjantungku.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatjantungku.data.model.User
import com.example.sehatjantungku.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    // Inisialisasi Repository dengan Lazy agar lebih aman
    private val repository by lazy { AuthRepository() }

    // State UI
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isSuccess by mutableStateOf(false)

    var userData by mutableStateOf<User?>(null)
        private set

    init {
        Log.d("AuthViewModel", "ViewModel berhasil dibuat")
    }

    // --- Login ---
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.loginUser(email, pass)
            isLoading = false
            if (result.isSuccess) isSuccess = true else errorMessage = result.exceptionOrNull()?.message
        }
    }

    // --- Register ---
    fun register(name: String, email: String, phone: String, pass: String, birthDate: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.registerUser(name, email, phone, pass, birthDate)
            isLoading = false
            if (result.isSuccess) isSuccess = true else errorMessage = result.exceptionOrNull()?.message
        }
    }

    // --- Forgot Password ---
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.sendPasswordReset(email)
            isLoading = false
            if (result.isSuccess) isSuccess = true else errorMessage = result.exceptionOrNull()?.message
        }
    }

    // --- Fetch Profile ---
    fun fetchUserProfile() {
        viewModelScope.launch {
            isLoading = true
            val result = repository.getUserProfile()
            if (result.isSuccess) userData = result.getOrNull()
            isLoading = false
        }
    }

    // --- Ganti Password ---
    fun changePassword(currentPass: String, newPass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            isSuccess = false

            Log.d("AuthViewModel", "Mencoba ganti password...")
            val result = repository.updatePassword(currentPass, newPass)

            isLoading = false
            if (result.isSuccess) {
                Log.d("AuthViewModel", "Password Berhasil diganti")
                isSuccess = true
            } else {
                val error = result.exceptionOrNull()?.message ?: "Gagal mengganti password"
                Log.e("AuthViewModel", "Gagal ganti password: $error")
                errorMessage = error
            }
        }
    }

    fun clearStatus() {
        errorMessage = null
        isSuccess = false
    }
}