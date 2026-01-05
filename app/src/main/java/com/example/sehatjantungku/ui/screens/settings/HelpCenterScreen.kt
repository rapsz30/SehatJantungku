package com.example.sehatjantungku.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavController) {
    val pinkMain = Color(0xFFFF6FB1)
    val bgGray = Color(0xFFF9FAFB)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pusat Bantuan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGray)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Pertanyaan Umum (FAQ)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            FAQItem(
                question = "Bagaimana Risiko Jantung (CVD) dihitung?",
                answer = "Kami menggunakan algoritma standar medis (seperti Framingham Risk Score) yang memperhitungkan usia, tekanan darah, kolesterol, status merokok, dan riwayat diabetes untuk mengestimasi risiko penyakit kardiovaskular dalam 10 tahun ke depan."
            )

            FAQItem(
                question = "Apa itu 'Umur Jantung'?",
                answer = "Umur Jantung adalah perkiraan usia biologis sistem kardiovaskular Anda. Jika Umur Jantung > Umur Asli, artinya risiko Anda lebih tinggi dari rata-rata orang seusia Anda."
            )

            FAQItem(
                question = "Bagaimana aplikasi memilih diet untuk saya?",
                answer = "Aplikasi menggunakan metode pembobotan cerdas (SAW) yang menggabungkan Skor Risiko Jantung, kondisi kesehatan klinis (Hipertensi/Kolesterol), dan preferensi makanan Anda untuk merekomendasikan program diet yang paling aman dan efektif."
            )

            FAQItem(
                question = "Apakah saya bisa mengganti program diet?",
                answer = "Ya. Anda dapat menghentikan program diet yang sedang berjalan di menu 'Opsi' pada halaman pelacakan diet, lalu melakukan personalisasi ulang untuk memilih program baru."
            )

            FAQItem(
                question = "Kenapa notifikasi makan tidak muncul?",
                answer = "Pastikan Anda telah memberikan izin notifikasi pada pengaturan HP Anda. Aplikasi kami dirancang untuk tetap mengirim pengingat jadwal makan bahkan saat layar terkunci."
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Hubungi Kami",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            ContactCard(
                icon = Icons.Default.Email,
                title = "Email Support",
                subtitle = "support@sehatjantungku.com"
            )

            ContactCard(
                icon = Icons.Default.Chat,
                title = "Live Chat (WhatsApp)",
                subtitle = "+62 812-3456-7890"
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = question,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))
                    Text(
                        text = answer,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280), // Gray 500
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactCard(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFF6FB1).copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFFF6FB1),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}