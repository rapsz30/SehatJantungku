package com.example.sehatjantungku.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DietReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Ambil data dari Intent
        val title = intent.getStringExtra("TITLE") ?: "Pengingat Diet"
        val message = intent.getStringExtra("MESSAGE") ?: "Waktunya makan sesuai program dietmu!"
        val notifId = intent.getIntExtra("ID", 0)

        // 1. Tampilkan Notifikasi Sistem (Floating / Lock Screen)
        val helper = DietNotificationHelper(context)
        helper.showNotification(title, message, notifId)

        // 2. Simpan ke Firestore agar muncul di halaman NotificationsScreen
        saveToHistory(title, message)
    }

    private fun saveToHistory(title: String, message: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // Format waktu saat ini, misal "12:00"
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Buat data map yang strukturnya sesuai dengan model di NotificationsScreen
        val notifData = hashMapOf(
            "title" to title,
            "message" to message,
            "time" to currentTime,
            "type" to "REMINDER", // Tipe ini akan menampilkan ikon jam warna oranye
            "isRead" to false,
            "timestamp" to System.currentTimeMillis()
        )

        // Simpan ke sub-collection notifications user
        db.collection("users").document(userId)
            .collection("notifications")
            .add(notifData)
            .addOnSuccessListener {
                // Berhasil disimpan (tidak perlu aksi UI karena ini di background)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}