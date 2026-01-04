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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sehatjantungku.ui.components.BottomNavBar
import com.example.sehatjantungku.ui.theme.PinkMain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

// --- DATA MODEL & ENUM ---

enum class NotificationType {
    REMINDER, ARTICLE, CHECKUP, ACHIEVEMENT, INFO
}

data class Notification(
    val id: String, // Diubah ke String untuk menampung ID dokumen Firestore
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean,
    val type: NotificationType,
    val timestamp: Long = 0L // Untuk sorting
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    // State untuk List Notifikasi
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load Data Real-time dari Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            db.collection("users").document(userId)
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Urutkan dari yang terbaru
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        isLoading = false
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val fetchedList = snapshot.documents.map { doc ->
                            // Mapping tipe string dari Firestore ke Enum
                            val typeString = doc.getString("type") ?: "INFO"
                            val typeEnum = try {
                                NotificationType.valueOf(typeString)
                            } catch (e: Exception) {
                                NotificationType.INFO
                            }

                            Notification(
                                id = doc.id,
                                title = doc.getString("title") ?: "Tanpa Judul",
                                message = doc.getString("message") ?: "",
                                time = doc.getString("time") ?: "",
                                isRead = doc.getBoolean("isRead") ?: false,
                                type = typeEnum,
                                timestamp = doc.getLong("timestamp") ?: 0L
                            )
                        }
                        notifications = fetchedList
                        isLoading = false
                    }
                }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notifikasi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = "notifications"
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9FAFB)) // Background abu-abu muda
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PinkMain
                )
            } else if (notifications.isEmpty()) {
                // Tampilan Kosong
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsOff,
                        contentDescription = "No Notifications",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Belum ada notifikasi",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // List Notifikasi
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { notification ->
                        NotificationItem(notification)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    // Tentukan Warna & Ikon berdasarkan Tipe
    val (icon, iconColor, bgColor) = when (notification.type) {
        NotificationType.REMINDER -> Triple(Icons.Default.Alarm, Color(0xFFFF9800), Color(0xFFFFF3E0)) // Oranye
        NotificationType.ARTICLE -> Triple(Icons.Default.Article, Color(0xFF2196F3), Color(0xFFE3F2FD)) // Biru
        NotificationType.CHECKUP -> Triple(Icons.Default.MedicalServices, Color(0xFFF44336), Color(0xFFFFEBEE)) // Merah
        NotificationType.ACHIEVEMENT -> Triple(Icons.Default.EmojiEvents, Color(0xFFFFC107), Color(0xFFFFF8E1)) // Kuning Emas
        NotificationType.INFO -> Triple(Icons.Default.Info, Color(0xFF9E9E9E), Color(0xFFF5F5F5)) // Abu-abu
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // Ikon di tengah vertikal terhadap teks
        ) {
            // --- BAGIAN IKON ---
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
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