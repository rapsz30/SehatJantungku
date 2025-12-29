package com.example.sehatjantungku.ui.screens.diet

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sehatjantungku.data.model.DietPlan
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietResultScreen(
    navController: NavController,
    dietId: String,
    sharedViewModel: DietProgramViewModel
) {
    val context = LocalContext.current

    // State Data
    var dietData by remember { mutableStateOf<DietPlan?>(null) }
    var aiAnalysis by remember { mutableStateOf("Menganalisis kecocokan...") }
    var isAiLoading by remember { mutableStateOf(true) }

    val pinkMain = Color(0xFFFF6FB1)

    // Load Data
    LaunchedEffect(dietId) {
        val plan = loadDietPlanFromJson(context, dietId)
        dietData = plan

        if (plan != null) {
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
        if (dietData == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = pinkMain)
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
                // 1. Header Diet Name
                Card(
                    colors = CardDefaults.cardColors(containerColor = pinkMain),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Icon(Icons.Default.RestaurantMenu, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Pilihan Terbaik Untuk Anda:", color = Color.White.copy(0.9f))
                        Text(
                            diet.dietName,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // 2. Deskripsi (Sekarang Di Atas)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Tentang Diet Ini", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            diet.deskripsi,
                            fontSize = 14.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 22.sp
                        )
                    }
                }

                // 3. Gemini Analysis (Sekarang Di Bawah Deskripsi)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFCC7CF0))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analisis AI Personal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        if (isAiLoading) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFCC7CF0),
                                trackColor = Color(0xFFF3E8FF)
                            )
                            Text("Sedang menyusun analisis...", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                        } else {
                            Text(
                                aiAnalysis,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF374151)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 4. Action Buttons (Dua Tombol)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Tombol Start
                    Button(
                        onClick = {
                            // Navigasi ke Diet Start (Memulai Program)
                            navController.navigate("diet_start/${diet.dietName}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = pinkMain),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mulai Program Diet Ini", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    // Tombol Batal/Pilih Lain
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
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

// Helper JSON Load (Tetap sama)
fun loadDietPlanFromJson(context: Context, id: String): DietPlan? {
    try {
        val inputStream = context.assets.open("dietplan.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = reader.use { it.readText() }
        val jsonObject = JSONObject(jsonString)

        if (jsonObject.has(id)) {
            val item = jsonObject.getJSONObject(id)
            return DietPlan(
                id = item.getString("id"),
                dietName = item.getString("dietName"),
                deskripsi = item.getString("deskripsi"),
                waktuDiet = item.getString("waktuDiet"),
                aturanDiet = item.getString("aturanDiet")
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}