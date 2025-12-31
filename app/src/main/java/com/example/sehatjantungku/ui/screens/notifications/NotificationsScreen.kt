package com.example.sehatjantungku.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sehatjantungku.ui.theme.PinkMain

// Menambahkan Enum Type untuk Ikon Dinamis
enum class NotificationType {
    REMINDER, ARTICLE, CHECKUP, ACHIEVEMENT, INFO
}

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean,
    val type: NotificationType = NotificationType.INFO
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    // Data Dummy dengan Tipe Spesifik
    val notifications = listOf(
        Notification(1, "Reminder Minum Obat", "Waktunya minum obat jantung Anda", "5m lalu", false, NotificationType.REMINDER),
        Notification(2, "Artikel Baru", "Baca tips terbaru: Diet Rendah Garam", "1j lalu", false, NotificationType.ARTICLE),
        Notification(3, "Jadwal Check-up", "Jangan lupa jadwal kontrol ke Dokter Budi besok pagi", "2j lalu", true, NotificationType.CHECKUP),
        Notification(4, "Hebat!", "Anda konsisten mengisi jurnal diet selama 7 hari", "1h lalu", true, NotificationType.ACHIEVEMENT)
    )

    // Background abu muda (Senada dengan Home/Content)
    val bgLight = Color(0xFFFAFAFA)

    Scaffold(
        topBar = {
            // Header Putih Bersih dengan Shadow
            Surface(shadowElevation = 2.dp) {
                TopAppBar(
                    title = {
                        Text(
                            "Notifikasi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black
                    )
                )
            }
        },
        containerColor = bgLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Jarak antar item rapi
        ) {
            items(notifications) { notification ->
                NotificationItem(notification)
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    // Logika Warna & Ikon berdasarkan Tipe
    val (icon, iconColor) = when (notification.type) {
        NotificationType.REMINDER -> Pair(Icons.Default.Alarm, Color(0xFFFF9800)) // Oranye
        NotificationType.ARTICLE -> Pair(Icons.Default.Article, Color(0xFF2196F3)) // Biru
        NotificationType.CHECKUP -> Pair(Icons.Default.MedicalServices, Color(0xFFFF5252)) // Merah
        NotificationType.ACHIEVEMENT -> Pair(Icons.Default.EmojiEvents, Color(0xFFFFC107)) // Emas
        else -> Pair(Icons.Default.Notifications, PinkMain)
    }

    // Warna Background: Putih jika sudah baca, Pink sangat muda jika belum
    val cardBg = if (notification.isRead) Color.White else Color(0xFFFFF5F8)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // --- BAGIAN IKON ---
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )

                // Indikator Merah (Jika belum dibaca)
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-2).dp, y = 2.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .shadow(1.dp, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- BAGIAN TEKS ---
            Column(modifier = Modifier.weight(1f)) {
                // Baris Judul & Waktu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = notification.time,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Pesan
                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = if (!notification.isRead) Color.DarkGray else Color.Gray,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}