package com.example.sehatjantungku.data.repository

import com.example.sehatjantungku.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Fungsi untuk Registrasi User Baru
    suspend fun registerUser(name: String, email: String, phone: String, pass: String): Result<Unit> {
        return try {
            // 1. Buat akun di Firebase Auth
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: ""

            // 2. Simpan data profil tambahan ke Firestore
            val user = User(uid = uid, name = name, email = email, phone = phone)
            db.collection("users").document(uid).set(user).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fungsi untuk Login
    suspend fun loginUser(email: String, pass: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fungsi untuk Lupa Password (Reset Email)
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fungsi Logout (Opsional untuk digunakan di Profile)
    fun logout() {
        auth.signOut()
    }
}