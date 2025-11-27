package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVDResultScreen(
    navController: NavController,
    heartAge: Int,
    riskScore: Int
) {
    val pinkMain = Color(0xFFFF6FB1)
    val pinkLight = Color(0xFFFF8CCF)
    val purpleLight = Color(0xFFCC7CF0)

    // Calculate risk category
    val riskCategory = when {
        riskScore < 20 -> "Sangat Rendah"
        riskScore < 40 -> "Rendah"
        riskScore < 60 -> "Sedang"
        else -> "Tinggi"
    }

    val riskColor = when {
        riskScore < 20 -> Color(0xFF4CAF50)
        riskScore < 40 -> Color(0xFF8BC34A)
        riskScore < 60 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hasil Prediksi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Heart Age Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Usia Jantung Anda",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "$heartAge tahun",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = pinkMain
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Usia Pembuluh Darah",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Risk Chart Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        "Risiko 10 Tahun",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Your Risk Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Risiko Anda", fontSize = 14.sp)
                            Text("$riskScore%", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(riskScore / 100f)
                                    .height(30.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(pinkMain, purpleLight)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Normal Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Normal", fontSize = 14.sp)
                            Text("30-40%", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.35f)
                                    .height(30.dp)
                                    .background(
                                        Color(0xFF8BC34A),
                                        RoundedCornerShape(8.dp)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Optimal Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Optimal", fontSize = 14.sp)
                            Text("10-20%", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.15f)
                                    .height(30.dp)
                                    .background(
                                        Color(0xFF4CAF50),
                                        RoundedCornerShape(8.dp)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Risk Category Badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(riskColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Kategori: $riskCategory",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = riskColor
                        )
                    }
                }
            }

            // Recommendations Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        "Rekomendasi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val recommendations = when {
                        riskScore < 20 -> listOf(
                            "✓ Pertahankan pola hidup sehat Anda",
                            "✓ Lakukan check-up rutin setiap tahun",
                            "✓ Tetap aktif dan olahraga teratur",
                            "✓ Jaga pola makan seimbang"
                        )
                        riskScore < 40 -> listOf(
                            "⚠ Tingkatkan aktivitas fisik menjadi 30 menit/hari",
                            "⚠ Kurangi konsumsi garam dan lemak jenuh",
                            "⚠ Pantau tekanan darah secara berkala",
                            "⚠ Konsultasi dengan dokter untuk evaluasi"
                        )
                        riskScore < 60 -> listOf(
                            "⚠ Segera konsultasi dengan dokter jantung",
                            "⚠ Kurangi berat badan jika berlebih",
                            "⚠ Hindari makanan tinggi kolesterol",
                            "⚠ Kelola stress dengan baik",
                            "⚠ Pertimbangkan terapi obat jika diperlukan"
                        )
                        else -> listOf(
                            "🚨 SEGERA konsultasi dengan dokter spesialis jantung",
                            "🚨 Lakukan pemeriksaan menyeluruh",
                            "🚨 Ubah gaya hidup secara signifikan",
                            "🚨 Hentikan kebiasaan merokok jika ada",
                            "🚨 Ikuti program diet jantung sehat",
                            "🚨 Pertimbangkan rawat inap jika diperlukan"
                        )
                    }

                    recommendations.forEach { recommendation ->
                        Text(
                            recommendation,
                            fontSize = 14.sp,
                            color = Color(0xFF333333),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = pinkMain
                    )
                ) {
                    Text("Hitung Ulang", modifier = Modifier.padding(vertical = 4.dp))
                }

                Button(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = pinkMain
                    )
                ) {
                    Text("Selesai", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
