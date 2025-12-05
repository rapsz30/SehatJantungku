package com.example.sehatjantungku.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusat Bantuan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Pertanyaan yang Sering Diajukan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            FAQItem(
                question = "Bagaimana cara menggunakan fitur CVD Risk Predictor?",
                answer = "Fitur CVD Risk Predictor dapat diakses dari menu utama. Isi formulir dengan data kesehatan Anda secara lengkap dan akurat, kemudian klik tombol 'Hitung Risiko Total' untuk melihat hasil prediksi."
            )

            FAQItem(
                question = "Apakah data saya aman?",
                answer = "Ya, semua data Anda dienkripsi dan disimpan dengan aman. Kami mematuhi standar keamanan data kesehatan internasional."
            )

            FAQItem(
                question = "Bagaimana cara mengubah program diet?",
                answer = "Anda dapat mengubah program diet dengan mengakses menu Personalized Diet Program, lalu isi kembali formulir preferensi diet Anda."
            )

            FAQItem(
                question = "Apakah saya bisa berkonsultasi dengan dokter?",
                answer = "Ya, fitur konsultasi sedang dalam pengembangan dan akan segera tersedia di update mendatang."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hubungi Kami",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ContactCard(
                icon = Icons.Default.Email,
                title = "Email",
                subtitle = "support@sehatjantungku.com"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ContactCard(
                icon = Icons.Default.Phone,
                title = "Telepon",
                subtitle = "+62 812-3456-7890"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ContactCard(
                icon = Icons.Default.Chat,
                title = "WhatsApp",
                subtitle = "+62 812-3456-7890"
            )
        }
    }
}

@Composable
private fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color(0xFFFF6FB1)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFF6FB1),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
