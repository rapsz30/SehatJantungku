package com.example.sehatjantungku.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sehatjantungku.R
import com.example.sehatjantungku.data.model.DietPlan
import java.util.Calendar

class DietNotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "diet_channel_id"
        const val CHANNEL_NAME = "Pengingat Jadwal Diet"

        // Request Code unik untuk tiap waktu makan
        const val REQ_CODE_SARAPAN = 101
        const val REQ_CODE_SIANG = 102
        const val REQ_CODE_MALAM = 103
        const val REQ_CODE_CAMILAN = 104
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // PENTING: Agar muncul sebagai Floating Notification (Heads-up)
            ).apply {
                description = "Notifikasi untuk jadwal makan diet"
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC // Agar muncul di Lock Screen
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showNotification(title: String, message: String, notificationId: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Untuk Android di bawah Oreo
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, builder.build())
    }

    // Fungsi Utama untuk Menjadwalkan Semua Waktu Makan
    fun scheduleDietPlan(dietPlan: DietPlan) {
        // Jadwalkan Sarapan
        scheduleAlarm(dietPlan.waktuSarapan, "Waktunya Sarapan!", "Menu: ${dietPlan.sarapanA}", REQ_CODE_SARAPAN)

        // Jadwalkan Makan Siang
        scheduleAlarm(dietPlan.waktuMakanSiang, "Waktunya Makan Siang!", "Menu: ${dietPlan.makansiangA}", REQ_CODE_SIANG)

        // Jadwalkan Makan Malam
        scheduleAlarm(dietPlan.waktuMakanMalam, "Waktunya Makan Malam!", "Menu: ${dietPlan.makanmalamA}", REQ_CODE_MALAM)

        // Jadwalkan Camilan
        scheduleAlarm(dietPlan.waktuCamilan, "Waktunya Camilan Sehat!", "Menu: ${dietPlan.camilanA}", REQ_CODE_CAMILAN)
    }

    // Fungsi Pembatalan Alarm (Panggil saat user berhenti diet)
    fun cancelAllAlarms() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val codes = listOf(REQ_CODE_SARAPAN, REQ_CODE_SIANG, REQ_CODE_MALAM, REQ_CODE_CAMILAN)

        for (code in codes) {
            val intent = Intent(context, DietReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun scheduleAlarm(timeString: String, title: String, message: String, reqCode: Int) {
        if (timeString.isBlank()) return

        try {
            val regex = Regex("(\\d{1,2})[.:](\\d{2})")
            val matchResult = regex.find(timeString)

            if (matchResult == null) return

            val (hourStr, minuteStr) = matchResult.destructured
            val hour = hourStr.toInt()
            val minute = minuteStr.toInt()

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)

                if (before(Calendar.getInstance())) {
                    add(Calendar.DATE, 1)
                }
            }

            val intent = Intent(context, DietReceiver::class.java).apply {
                putExtra("TITLE", title)
                putExtra("MESSAGE", message)
                putExtra("ID", reqCode)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reqCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Gunakan setExactAndAllowWhileIdle agar akurat meski HP dalam mode Doze
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}