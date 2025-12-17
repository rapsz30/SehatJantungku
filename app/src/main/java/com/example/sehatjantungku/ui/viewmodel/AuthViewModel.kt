package com.example.sehatjantungku.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatjantungku.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    // State untuk memantau proses loading
    var isLoading by mutableStateOf(false)
        private set

    // State untuk menyimpan pesan error jika terjadi kegagalan
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // State untuk menandai keberhasilan operasi (untuk trigger navigasi)
    var isSuccess by mutableStateOf(false)

    // Fungsi Login
    fun login(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            errorMessage = "Email dan Password tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.loginUser(email, pass)
            isLoading = false

            if (result.isSuccess) {
                isSuccess = true
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Login Gagal"
            }
        }
    }

    // Fungsi Register
    fun register(name: String, email: String, phone: String, pass: String) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            errorMessage = "Semua bidang harus diisi"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.registerUser(name, email, phone, pass)
            isLoading = false

            if (result.isSuccess) {
                isSuccess = true
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Registrasi Gagal"
            }
        }
    }

    // Fungsi Lupa Password
    fun forgotPassword(email: String) {
        if (email.isEmpty()) {
            errorMessage = "Masukkan email terlebih dahulu"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.sendPasswordReset(email)
            isLoading = false

            if (result.isSuccess) {
                isSuccess = true
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Gagal mengirim email reset"
            }
        }
    }

    // Fungsi untuk reset state error/success secara manual jika diperlukan
    fun clearStatus() {
        errorMessage = null
        isSuccess = false
    }
}