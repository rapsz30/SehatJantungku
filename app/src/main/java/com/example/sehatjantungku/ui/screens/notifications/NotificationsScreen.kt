package com.example.sehatjantungku.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sehatjantungku.ui.theme.PinkMain

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val notifications = listOf(
        Notification(1, "Reminder Minum Obat", "Waktunya minum obat jantung Anda", "5 menit lalu", false),
        Notification(2, "Artikel Baru", "Ada artikel baru tentang diet jantung sehat", "1 jam lalu", false),
        Notification(3, "Check-up Reminder", "Jangan lupa check-up bulanan Anda", "2 jam lalu", true),
        Notification(4, "Pencapaian", "Selamat! Anda sudah menyelesaikan 7 hari diet", "1 hari lalu", true)
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PinkMain,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            items(notifications) { notification ->
                NotificationItem(notification)
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFFFF0F5)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PinkMain.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = notification.time,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
