package com.example.sehatjantungku.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DietReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("TITLE") ?: "Pengingat Diet"
        val message = intent.getStringExtra("MESSAGE") ?: "Waktunya makan sesuai program dietmu!"
        val notifId = intent.getIntExtra("ID", 0)

        val helper = DietNotificationHelper(context)
        helper.showNotification(title, message, notifId)
    }
}