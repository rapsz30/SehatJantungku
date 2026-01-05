package com.example.sehatjantungku.ui.screens.diet

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext // Penting: Tambahkan import ini
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietResultScreen(
    navController: NavController,
    dietId: String,
    sharedViewModel: DietProgramViewModel
) {
    // 1. Ambil Context di sini
    val context = LocalContext.current

    val dietData by sharedViewModel.fetchedDietPlan.collectAsState()
    val isLoading by sharedViewModel.isLoadingPlan.collectAsState()

    var aiAnalysis by remember { mutableStateOf("Menganalisis kecocokan...") }
    var isAiLoading by remember { mutableStateOf(true) }
    val pinkMain = Color(0xFFFF6FB1)

    // Load Data
    LaunchedEffect(dietId) {
        sharedViewModel.fetchDietPlanFromFirebase(dietId)
    }

    // Load Gemini
    LaunchedEffect(dietData) {
        dietData?.let { plan ->
            val analysis = sharedViewModel.generateGeminiAnalysis(plan.dietName)
            aiAnalysis = analysis
            isAiLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rekomendasi Diet", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = pinkMain)
            }
        } else if (dietData == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(48.dp))
                    Text("Gagal memuat data diet.", color = Color.Red, modifier = Modifier.padding(16.dp))
                    Button(onClick = { sharedViewModel.fetchDietPlanFromFirebase(dietId) }) { Text("Coba Lagi") }
                }
            }
        } else {
            val diet = dietData!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF9FAFB))
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Card(colors = CardDefaults.cardColors(containerColor = pinkMain), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Icon(Icons.Default.RestaurantMenu, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Pilihan Terbaik Untuk Anda:", color = Color.White.copy(0.9f))
                        Text(diet.dietName, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Deskripsi
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Tentang Diet Ini", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(diet.deskripsi, fontSize = 14.sp, color = Color(0xFF4B5563), lineHeight = 22.sp)
                    }
                }

                // AI Analysis
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFCC7CF0))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analisis AI Personal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (isAiLoading) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFFCC7CF0), trackColor = Color(0xFFF3E8FF))
                            Text("Sedang menyusun analisis...", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                        } else {
                            Text(aiAnalysis, fontSize = 14.sp, lineHeight = 22.sp, color = Color(0xFF374151))
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            val dietIdStr = diet.id.toString()

                            // 2. Kirim context ke ViewModel
                            sharedViewModel.startNewDiet(
                                dietId = dietIdStr,
                                dietName = diet.dietName,
                                context = context, // <-- PERBAIKAN DI SINI
                                onSuccess = {
                                    navController.navigate("diet_start/$dietIdStr") {
                                        popUpTo("diet_result") { inclusive = true }
                                    }
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = pinkMain),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mulai Program Diet Ini", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Icon(Icons.Default.Cancel, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pilih Diet Lain", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}