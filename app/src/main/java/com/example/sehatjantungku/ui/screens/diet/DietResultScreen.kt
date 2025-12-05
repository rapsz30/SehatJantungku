package com.example.sehatjantungku.ui.screens.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
fun DietResultScreen(
    navController: NavController,
    bestDiet: String,
    scores: List<Int>
) {
    val pinkMain = Color(0xFFFF6FB1)
    val pinkLight = Color(0xFFFF8CCF)
    val purpleLight = Color(0xFFCC7CF0)

    val dietDescriptions = mapOf(
        "Plant-Based" to "Berdasarkan preferensi Anda dan hasil CVD Risk, diet berbasis tumbuhan sangat cocok untuk menurunkan risiko kardiovaskular sambil memenuhi kebutuhan nutrisi Anda.",
        "DASH" to "Hasil CVD Risk Predictor Anda menunjukkan pentingnya menurunkan tekanan darah. Diet DASH dirancang khusus untuk kesehatan jantung dan sangat sesuai dengan kondisi Anda.",
        "Mediterranean" to "Dengan mempertimbangkan hasil CVD Risk Anda, diet Mediterania menawarkan keseimbangan nutrisi yang terbukti baik untuk kesehatan jantung.",
        "Low-Sodium" to "Fokus Anda pada pengurangan garam menunjukkan bahwa diet rendah natrium adalah pilihan terbaik untuk kesehatan kardiovaskular Anda.",
        "Low-Fat" to "Anda ingin mengurangi lemak dan berminyak. Diet rendah lemak akan membantu mencapai tujuan berat badan dan kesehatan jantung Anda."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rekomendasi Diet Anda", fontWeight = FontWeight.Bold) },
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
            // Best Diet Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(pinkLight, purpleLight)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Diet Terbaik untuk Anda",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            bestDiet,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Reason Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Mengapa diet ini?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        dietDescriptions[bestDiet] ?: "Diet ini paling sesuai dengan preferensi dan tujuan kesehatan Anda.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        lineHeight = 22.sp
                    )
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        // Save to localStorage
                        navController.navigate("diet_start/$bestDiet")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = pinkMain
                    )
                ) {
                    Text("Mulai Program Diet", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = pinkMain
                    )
                ) {
                    Text("Isi Ulang Form", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
