package com.example.sehatjantungku.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatjantungku.data.model.User
import com.example.sehatjantungku.data.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider // Tambahan Import
import com.google.firebase.auth.FirebaseAuth // Tambahan Import
import com.google.firebase.firestore.FirebaseFirestore // Tambahan Import
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository by lazy { AuthRepository() }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.loginUser(email, pass)
            isLoading = false
            if (result.isSuccess) isSuccess = true else errorMessage = result.exceptionOrNull()?.message
        }
    }

    fun register(name: String, email: String, phone: String, pass: String, birthDate: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.registerUser(name, email, phone, pass, birthDate)
            isLoading = false
            if (result.isSuccess) isSuccess = true else errorMessage = result.exceptionOrNull()?.message
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.sendPasswordReset(email)
            isLoading = false
            if (result.isSuccess) isSuccess = true else errorMessage = result.exceptionOrNull()?.message
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            isLoading = true
            val result = repository.getUserProfile()
            if (result.isSuccess) userData = result.getOrNull()
            isLoading = false
        }
    }

    fun changePassword(currentPass: String, newPass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            isSuccess = false
            val result = repository.updatePassword(currentPass, newPass)
            isLoading = false
            if (result.isSuccess) isSuccess = true else errorMessage = result.exceptionOrNull()?.message
        }
    }

    fun deleteAccount(password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onError("User tidak ditemukan.")
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email ?: "", password)

        user.reauthenticate(credential).addOnSuccessListener {
            firestore.collection("users").document(user.uid).delete()
                .addOnSuccessListener {
                    user.delete().addOnSuccessListener {
                        onSuccess()
                    }.addOnFailureListener { e ->
                        onError("Gagal menghapus akun: ${e.message}")
                    }
                }
                .addOnFailureListener { e ->
                    onError("Gagal menghapus data user: ${e.message}")
                }
        }.addOnFailureListener { e ->
            onError("Password salah atau terjadi kesalahan: ${e.message}")
        }
    }

    fun clearStatus() {
        errorMessage = null
        isSuccess = false
    }

    fun clearUserData() {
        userData = null
        clearStatus()
        Log.d("AuthViewModel", "User data cleared")
    }
}