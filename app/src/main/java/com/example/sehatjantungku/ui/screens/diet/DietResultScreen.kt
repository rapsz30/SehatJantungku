package com.example.sehatjantungku.ui.screens.diet

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
fun DietResultScreen(
    navController: NavController,
    dietType: String
) {
    val scores = listOf(85, 75, 90, 60, 50)
    val bestDiet = "Mediterranean"
    val pinkMain = Color(0xFFFF6FB1)
    val pinkLight = Color(0xFFFF8CCF)
    val purpleLight = Color(0xFFCC7CF0)

    val dietNames = listOf("Plant-Based", "DASH", "Mediterranean", "Low-Sodium", "Low-Fat")

    val dietDescriptions = mapOf(
        "Plant-Based" to "Diet berbasis tumbuhan yang fokus pada sayur, buah, biji-bijian, dan kacang-kacangan",
        "DASH" to "Dietary Approaches to Stop Hypertension - ideal untuk mengontrol tekanan darah",
        "Mediterranean" to "Diet Mediterania yang seimbang dengan lemak sehat dari minyak zaitun dan ikan",
        "Low-Sodium" to "Diet rendah garam untuk kesehatan jantung dan tekanan darah",
        "Low-Fat" to "Diet rendah lemak untuk menurunkan berat badan dan kolesterol"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hasil Rekomendasi Diet", fontWeight = FontWeight.Bold) },
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
                shape = RoundedCornerShape(16.dp),
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
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Diet Terbaik untuk Anda",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            bestDiet,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Alasan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        dietDescriptions[bestDiet] ?: "",
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        lineHeight = 20.sp
                    )
                }
            }

            // Ranking Card
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
                        "Tampilkan Ranking Lengkap:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val sortedDiets = dietNames.zip(scores).sortedByDescending { it.second }

                    sortedDiets.forEachIndexed { index, (diet, score) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            if (index == 0) pinkMain else Color(0xFFEEEEEE),
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${index + 1}",
                                        color = if (index == 0) Color.White else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    diet,
                                    fontSize = 14.sp,
                                    fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            Text(
                                "Skor: $score",
                                fontSize = 14.sp,
                                color = if (index == 0) pinkMain else Color.Gray,
                                fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        if (index < sortedDiets.size - 1) {
                            Divider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = Color(0xFFEEEEEE)
                            )
                        }
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
                    Text("Kembali", modifier = Modifier.padding(vertical = 4.dp))
                }

                Button(
                    onClick = { navController.navigate("diet_start/$bestDiet") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = pinkMain
                    )
                ) {
                    Text("Mulai Diet", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
