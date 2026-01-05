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
        val title = intent.getStringExtra("TITLE") ?: "Pengingat Diet"
        val message = intent.getStringExtra("MESSAGE") ?: "Waktunya makan sesuai program dietmu!"
        val notifId = intent.getIntExtra("ID", 0)

        val helper = DietNotificationHelper(context)
        helper.showNotification(title, message, notifId)

        saveToHistory(title, message)
    }

    private fun saveToHistory(title: String, message: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val notifData = hashMapOf(
            "title" to title,
            "message" to message,
            "time" to currentTime,
            "type" to "REMINDER",
            "isRead" to false,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users").document(userId)
            .collection("notifications")
            .add(notifData)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}