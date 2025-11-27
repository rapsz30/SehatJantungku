package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sehatjantungku.ui.theme.PinkMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVDRiskResultScreen(
    navController: NavController,
    riskScore: Int,
    heartAge: Int
) {
    val riskCategory = when {
        riskScore < 30 -> "Sangat Rendah"
        riskScore < 50 -> "Rendah"
        riskScore < 70 -> "Sedang"
        else -> "Tinggi"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hasil Prediksi") },
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
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Usia Jantung Anda",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "$heartAge tahun",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = PinkMain
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Risiko 10 Tahun",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Risk bars
                        RiskBar("Risiko Anda", riskScore, PinkMain)
                        Spacer(modifier = Modifier.height(8.dp))
                        RiskBar("Normal", 50, Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.height(8.dp))
                        RiskBar("Optimal", 30, Color(0xFF2196F3))
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Kategori: $riskCategory",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Rekomendasi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (riskCategory) {
                                "Tinggi" -> "• Segera konsultasi dengan dokter\n• Lakukan pemeriksaan rutin\n• Ubah pola hidup menjadi lebih sehat\n• Kurangi konsumsi garam dan lemak"
                                "Sedang" -> "• Mulai pola hidup sehat\n• Olahraga teratur 3-4x seminggu\n• Kontrol berat badan\n• Kurangi stres"
                                else -> "• Pertahankan pola hidup sehat\n• Olahraga rutin\n• Pola makan seimbang\n• Cek kesehatan berkala"
                            },
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinkMain),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kembali")
                }
            }
        }
    }
}

@Composable
fun RiskBar(label: String, value: Int, color: Color) {
    Column {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(Color.LightGray, RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value / 100f)
                    .height(12.dp)
                    .background(color, RoundedCornerShape(6.dp))
            )
        }
        Text("$value%", fontSize = 10.sp, color = Color.Gray)
    }
}
