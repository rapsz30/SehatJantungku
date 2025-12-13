package com.example.sehatjantungku.ui.screens.diet

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DietCompletionScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("SehatJantungku", Context.MODE_PRIVATE)

    val dietName = sharedPreferences.getString("activeDietProgram", "Plant-Based") ?: "Plant-Based"
    val completionRate = sharedPreferences.getInt("dietCompletionRate", 85)

    LaunchedEffect(Unit) {
        if (completionRate >= 80) {
            val badgeType = if (completionRate >= 100) "gold" else "silver"
            val existingBadges = sharedPreferences.getStringSet("dietBadges", mutableSetOf()) ?: mutableSetOf()
            val newBadges = existingBadges.toMutableSet()
            newBadges.add("$dietName-$badgeType-${System.currentTimeMillis()}")
            sharedPreferences.edit().putStringSet("dietBadges", newBadges).apply()
        }
    }

    val badgeInfo = when {
        completionRate >= 100 -> Triple("Master Diet!", "🏆", Color(0xFFFFC107))
        completionRate >= 80 -> Triple("Excellent!", "🥈", Color(0xFFFF6FB1))
        else -> Triple("Good Effort!", "⭐", Color(0xFF2196F3))
    }

    val pinkMain = Color(0xFFFF6FB1)
    val pinkLight = Color(0xFFFF8CCF)
    val purpleLight = Color(0xFFCC7CF0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(pinkLight, purpleLight)
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Program Selesai! 🎉",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Selamat atas pencapaian Anda!",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        completionRate >= 100 -> Color(0xFFFFF9C4)
                        completionRate >= 80 -> Color(0xFFFCE7F3)
                        else -> Color(0xFFE3F2FD)
                    }
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            badgeInfo.second,
                            fontSize = 56.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        badgeInfo.first,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        if (completionRate >= 100)
                            "Sempurna! Anda menyelesaikan 100% program diet!"
                        else if (completionRate >= 80)
                            "Luar biasa! Anda menyelesaikan lebih dari 80% program!"
                        else
                            "Usaha yang bagus! Terus tingkatkan konsistensi Anda.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "$completionRate%",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        "Tingkat Penyelesaian",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Ringkasan Program",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SummaryRow("Program Diet", dietName)
                    SummaryRow("Durasi", "21 Hari")
                    SummaryRow("Tugas Diselesaikan", "${(21 * 5 * completionRate / 100)} dari ${21 * 5} tugas")
                    SummaryRow(
                        "Badge Diperoleh",
                        if (completionRate >= 100) "🥇 Gold"
                        else if (completionRate >= 80) "🥈 Silver"
                        else "🥉 Bronze",
                        valueColor = pinkMain
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            "Badge tersimpan di Profile!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Pencapaian Anda telah ditambahkan ke profil. Lihat koleksi badge Anda di menu Profile.",
                            fontSize = 12.sp,
                            color = Color(0xFF047857),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        // Restart same program
                        sharedPreferences.edit().apply {
                            putInt("dietCurrentDay", 1)
                            putInt("dietCompletionRate", 0)
                            apply()
                        }
                        navController.navigate("diet_start/$dietName") {
                            popUpTo("diet_completion") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(pinkLight, pinkMain)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "Ulangi Program Diet Ini",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        sharedPreferences.edit().remove("activeDietProgram").apply()
                        navController.navigate("diet_program") {
                            popUpTo("diet_completion") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = pinkMain)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Ubah Program Diet", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(
                    onClick = {
                        navController.navigate("profile")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Lihat Badge di Profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF1F2937)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}
