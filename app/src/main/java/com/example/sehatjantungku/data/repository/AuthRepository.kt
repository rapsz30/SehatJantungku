package com.example.sehatjantungku.data.repository

import com.example.sehatjantungku.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Fungsi untuk Registrasi User Baru dengan Tanggal Lahir
    suspend fun registerUser(name: String, email: String, phone: String, pass: String, birthDate: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: ""
            // Menyimpan objek User ke Firestore
            val user = User(uid = uid, name = name, email = email, phone = phone, birthDate = birthDate)
            db.collection("users").document(uid).set(user).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fungsi untuk mengambil data profil dari Firestore
    suspend fun getUserProfile(): Result<User?> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.success(null)
            val snapshot = db.collection("users").document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
            Result.success(user)
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
    // Fungsi untuk Lupa Password
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Fungsi Logout
    fun logout() {
        auth.signOut()
    }
}