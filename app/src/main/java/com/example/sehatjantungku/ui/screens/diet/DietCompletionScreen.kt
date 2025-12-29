package com.example.sehatjantungku.ui.screens.diet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun DietCompletionScreen(
    navController: NavController,
    viewModel: DietProgramViewModel = viewModel()
) {
    val pinkMain = Color(0xFFFF6FB1)
    val goldColor = Color(0xFFFFD700)

    // Animasi Scale untuk Badge
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 200),
        label = "BadgeScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Text
            Text(
                "LUAR BIASA!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = pinkMain,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Anda telah menyelesaikan program diet.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ANIMATED BADGE
            Box(
                modifier = Modifier
                    .scale(scale)
                    .size(180.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(goldColor.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = goldColor,
                    modifier = Modifier.size(100.dp)
                )

                // Bintang hiasan
                Icon(Icons.Default.Star, null, tint = pinkMain, modifier = Modifier.align(Alignment.TopEnd).offset((-20).dp, 20.dp).size(30.dp))
                Icon(Icons.Default.Star, null, tint = pinkMain, modifier = Modifier.align(Alignment.BottomStart).offset(20.dp, (-20).dp).size(20.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Card Info
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF2F8)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, null, tint = pinkMain, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Lencana Baru Diterima!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF831843)
                    )
                    Text(
                        "Cek koleksi lencana Anda di menu Profil.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color(0xFF9D174D),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- ACTION BUTTONS ---
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // BUTTON 1: ULANGI DIET (RESET)
                OutlinedButton(
                    onClick = {
                        viewModel.repeatCurrentDiet { dietId ->
                            // [PERBAIKAN] Clear stack sampai Home, baru masuk Tracker
                            // Ini memastikan Tracker (DietStartScreen) dimuat ulang dari awal
                            navController.navigate("diet_start/$dietId") {
                                popUpTo("home") { inclusive = false }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, pinkMain),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = pinkMain)
                ) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ulangi Program Ini", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // BUTTON 2: KEMBALI KE HOME (FINISH)
                Button(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = pinkMain),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.Home, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kembali ke Beranda", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}