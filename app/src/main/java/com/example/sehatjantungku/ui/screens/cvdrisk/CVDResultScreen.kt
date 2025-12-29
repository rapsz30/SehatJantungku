package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.roundToInt
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVDResultScreen(
    navController: NavController,
    heartAge: Int,
    riskScoresString: String, // String: "userRisk,optimalRisk,normalRisk"
    viewModel: CVDRiskViewModel = viewModel()
) {
    val saveStatus by viewModel.saveStatus.collectAsState()

    // --- Parsing Data ---
    val risks = remember(riskScoresString) {
        riskScoresString.split(",").map { it.toDoubleOrNull() ?: 0.0 }
    }
    val userRiskDecimal = risks.getOrElse(0) { 0.0 }
    val optimalRiskDecimal = risks.getOrElse(1) { 0.0 }
    val normalRiskDecimal = risks.getOrElse(2) { 0.0 }

    // Persentase User (0-100)
    val userRiskPercent = (userRiskDecimal * 100).toFloat()

    // Menentukan Kategori
    val (riskCategory, riskColor, riskIcon) = when {
        userRiskPercent < 20 -> Triple("Rendah", Color(0xFF4CAF50), Icons.Default.CheckCircle)
        userRiskPercent < 40 -> Triple("Sedang", Color(0xFFFF9800), Icons.Default.Warning)
        else -> Triple("Tinggi", Color(0xFFF44336), Icons.Default.Warning)
    }

    // Effect untuk menangani status penyimpanan
    LaunchedEffect(saveStatus) {
        if (saveStatus is SaveStatus.Success) {
            // Optional: Tampilkan Toast atau biarkan Dialog muncul (di bawah)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hasil Analisis", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->

        if (saveStatus is SaveStatus.Success) {
            AlertDialog(
                onDismissRequest = { viewModel.resetSaveStatus() },
                icon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50)) },
                title = { Text("Tersimpan!") },
                text = { Text("Data kesehatan jantung Anda berhasil disimpan ke database.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetSaveStatus() }) {
                        Text("OK")
                    }
                }
            )
        }

        if (saveStatus is SaveStatus.Error) {
            AlertDialog(
                onDismissRequest = { viewModel.resetSaveStatus() },
                icon = { Icon(Icons.Default.Warning, null, tint = Color.Red) },
                title = { Text("Gagal Menyimpan") },
                text = { Text((saveStatus as SaveStatus.Error).message) },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetSaveStatus() }) {
                        Text("Tutup")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9FAFB)) // Light Gray Background
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Header Card (Heart Age)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.elevatedCardElevation(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFF6FB1), Color(0xFFD64486))
                            )
                        )
                        .padding(24.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Usia Jantung Anda",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$heartAge Tahun",
                            color = Color.White,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SuggestionChip(
                            onClick = {},
                            label = { Text("Kategori: $riskCategory", color = Color.White, fontWeight = FontWeight.Bold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = riskColor.copy(alpha = 0.8f)),
                            border = null
                        )
                    }
                }
            }

            // 2. Risk Visualizer Section
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Risiko 10 Tahun ke Depan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    RiskBarItem(
                        label = "Risiko Anda",
                        valueDecimal = userRiskDecimal,
                        color = riskColor,
                        isMain = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RiskBarItem(
                        label = "Rata-rata Normal",
                        valueDecimal = normalRiskDecimal,
                        color = Color(0xFF8BC34A)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    RiskBarItem(
                        label = "Kondisi Optimal",
                        valueDecimal = optimalRiskDecimal,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            // 3. Recommendation Section
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Rekomendasi Kesehatan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))

                    val recommendations = getRecommendations(userRiskPercent)
                    recommendations.forEach { text ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(end = 8.dp))
                            Text(text, fontSize = 14.sp, color = Color(0xFF4B5563), lineHeight = 20.sp)
                        }
                    }
                }
            }

            // 4. Save & Action Button
            Button(
                onClick = {
                    viewModel.saveToFirebase(heartAge, userRiskDecimal, riskCategory)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                enabled = saveStatus !is SaveStatus.Success && saveStatus !is SaveStatus.Loading
            ) {
                if (saveStatus is SaveStatus.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else if (saveStatus is SaveStatus.Success) {
                    Icon(Icons.Default.CheckCircle, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tersimpan")
                } else {
                    Icon(Icons.Default.Save, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Hasil", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Hitung Ulang")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun RiskBarItem(
    label: String,
    valueDecimal: Double,
    color: Color,
    isMain: Boolean = false
) {
    val percentage = (valueDecimal * 100).toFloat()
    val displayValue = String.format("%.1f%%", percentage)
    // Cap visual max width at 100% even if higher, minimal visual 1%
    val visualProgress = (percentage / 100f).coerceIn(0.01f, 1f)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                label,
                fontSize = if (isMain) 16.sp else 14.sp,
                fontWeight = if (isMain) FontWeight.Bold else FontWeight.Medium,
                color = if (isMain) Color.Black else Color.Gray
            )
            Text(
                displayValue,
                fontSize = if (isMain) 18.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isMain) 12.dp else 8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF3F4F6))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(visualProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(color)
            )
        }
    }
}

fun getRecommendations(riskPercent: Float): List<String> {
    return when {
        riskPercent < 5 -> listOf(
            "Pertahankan pola hidup sehat Anda saat ini.",
            "Lakukan check-up kesehatan rutin setidaknya setahun sekali.",
            "Tetap aktif berolahraga minimal 150 menit per minggu.",
            "Jaga pola makan seimbang dengan perbanyak sayur dan buah."
        )
        riskPercent < 10 -> listOf(
            "Tingkatkan aktivitas fisik menjadi rutin 30 menit setiap hari.",
            "Kurangi konsumsi garam, gula, dan lemak jenuh.",
            "Pantau tekanan darah Anda secara mandiri atau di klinik.",
            "Pertimbangkan konsultasi dokter untuk evaluasi faktor risiko."
        )
        riskPercent < 20 -> listOf(
            "Segera konsultasi dengan dokter jantung untuk pemeriksaan lanjut.",
            "Wajib menurunkan berat badan jika berlebih (obesitas).",
            "Hindari makanan cepat saji dan tinggi kolesterol sepenuhnya.",
            "Kelola stres dengan baik (yoga, meditasi, atau hobi).",
            "Cek profil lipid (kolesterol) darah Anda segera."
        )
        else -> listOf(
            "SANGAT DISARANKAN konsultasi ke dokter spesialis jantung SEGERA.",
            "Lakukan pemeriksaan EKG dan Treadmill test.",
            "Ubah gaya hidup secara drastis (berhenti merokok total).",
            "Ikuti program diet jantung sehat yang ketat.",
            "Patuhi jadwal minum obat jika sudah diresepkan dokter."
        )
    }
}