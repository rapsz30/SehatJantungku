package com.example.sehatjantungku.ui.screens.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DietCompletionScreen(
    navController: NavController,
    completionRate: Int = 95
) {
    val badgeType = when {
        completionRate >= 100 -> "Gold"
        completionRate >= 80 -> "Silver"
        else -> "Bronze"
    }

    val badgeColor = when (badgeType) {
        "Gold" -> Color(0xFFFFD700)
        "Silver" -> Color(0xFFC0C0C0)
        else -> Color(0xFFCD7F32)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Badge
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(badgeColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Badge",
                modifier = Modifier.size(80.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Selamat! 🎉",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF6FB1)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Program Diet Selesai",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Anda telah menyelesaikan program diet 21 hari dengan tingkat keberhasilan $completionRate%!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Badge $badgeType telah ditambahkan ke profil Anda",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color(0xFFFF6FB1),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Statistics Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Statistik Program",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatRow("Total Hari", "21 hari")
                StatRow("Tugas Diselesaikan", "${(21 * completionRate / 100)} / 21")
                StatRow("Tingkat Keberhasilan", "$completionRate%")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Button(
            onClick = {
                // Restart program
                navController.navigate("diet_result/Plant-Based/80,75,70,65,60") {
                    popUpTo("diet_completion") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF6FB1),
                                Color(0xFFCC7CF0)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ulangi Program",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                navController.navigate("diet_program") {
                    popUpTo("diet_completion") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFFF6FB1)
            )
        ) {
            Text("Ubah Program Diet")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true }
                }
            }
        ) {
            Text("Kembali ke Home", color = Color.Gray)
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
