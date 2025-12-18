package com.example.sehatjantungku.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatjantungku.data.model.User
import com.example.sehatjantungku.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isSuccess by mutableStateOf(false)

    // Tambahkan ini untuk menyimpan data user di Profile
    var userData by mutableStateOf<User?>(null)
        private set

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.loginUser(email, pass)
            isLoading = false
            if (result.isSuccess) isSuccess = true else errorMessage = result.exceptionOrNull()?.message
        }
    }

    // Perbarui fungsi register untuk menerima birthDate
    fun register(name: String, email: String, phone: String, pass: String, birthDate: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.registerUser(name, email, phone, pass, birthDate)
            isLoading = false
            if (result.isSuccess) isSuccess = true else errorMessage = result.exceptionOrNull()?.message
        }
    }

    // Tambahkan fungsi ini untuk memperbaiki error di ForgotPasswordScreen
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.sendPasswordReset(email)
            isLoading = false
            if (result.isSuccess) isSuccess = true else errorMessage = result.exceptionOrNull()?.message
        }
    }

    // Tambahkan fungsi ini untuk mengambil data ke ProfileScreen
    fun fetchUserProfile() {
        viewModelScope.launch {
            isLoading = true
            val result = repository.getUserProfile()
            if (result.isSuccess) {
                userData = result.getOrNull()
            }
            isLoading = false
        }
    }

    fun clearStatus() {
        errorMessage = null
        isSuccess = false
    }
}