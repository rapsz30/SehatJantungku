package com.example.sehatjantungku.ui.screens.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietProgramScreen(
    navController: NavController,
    viewModel: DietProgramViewModel = viewModel()
) {
    val pinkMain = Color(0xFFFF6FB1)
    val state by viewModel.state.collectAsState()
    var showInfo by remember { mutableStateOf(false) }
    var useCVDData by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("SehatJantungku", Context.MODE_PRIVATE)
    val cvdDataAvailable = remember { sharedPreferences.contains("cvd_risk_score") }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = { Text("Panduan Pengisian") },
            text = {
                Column {
                    Text("📋 Pilih jawaban yang paling sesuai dengan kondisi dan preferensi Anda.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🎯 Jawaban Anda akan membantu kami memberikan rekomendasi diet terbaik.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("💪 Tidak ada jawaban yang salah - semua disesuaikan dengan kebutuhan Anda.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfo = false }) {
                    Text("OK", color = pinkMain)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Program Diet Personal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showInfo = true }) {
                        Icon(Icons.Default.Info, "Info", tint = pinkMain)
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
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Question 1 - Blood Pressure
            QuestionItem(
                number = "1️",
                question = "Bagaimana kondisi tekanan darah kamu saat ini?",
                options = listOf("Normal / terkontrol", "Kadang tinggi", "Sering tinggi / didiagnosis darah tinggi", "Tidak tahu"),
                selectedOption = state.bloodPressure,
                onOptionSelected = { viewModel.updateBloodPressure(it) }
            )

            // Question 2 - Cholesterol
            QuestionItem(
                number = "2️",
                question = "Bagaimana kondisi kolesterol kamu?",
                options = listOf("Normal", "Agak tinggi", "Tinggi / pernah disarankan diet", "Tidak tahu"),
                selectedOption = state.cholesterol,
                onOptionSelected = { viewModel.updateCholesterol(it) }
            )

            // Question 3 - Health Conditions (Multiple checkboxes)
            Column {
                Text(
                    "3️ Apakah kamu memiliki kondisi kesehatan berikut?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("Tekanan darah tinggi", "Kolesterol tinggi", "Diabetes", "Asam urat", "Tidak ada").forEach { condition ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp,
                                    color = if (state.healthConditions.contains(condition)) pinkMain else Color(0xFFE5E7EB),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(
                                    color = if (state.healthConditions.contains(condition)) Color(0xFFFCE7F3) else Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.toggleHealthCondition(condition) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = state.healthConditions.contains(condition),
                                onCheckedChange = { viewModel.toggleHealthCondition(condition) },
                                colors = CheckboxDefaults.colors(checkedColor = pinkMain)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                condition,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.weight(1f)
                            )
                            if (state.healthConditions.contains(condition)) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = pinkMain,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Question 4 - Food Preference
            QuestionItem(
                number = "4️",
                question = "Makanan seperti apa yang paling nyaman buat kamu jalani?",
                options = listOf("Nabati dominan", "Hewani dominan", "Nabati dan hewani seimbang", "Praktis / instan (makanan cepat saji atau instan)"),
                selectedOption = state.foodPreference,
                onOptionSelected = { viewModel.updateFoodPreference(it) }
            )

            // Question 5 - Activity Level
            QuestionItem(
                number = "5️",
                question = "Dalam keseharian, seberapa aktif kamu bergerak atau berolahraga?",
                options = listOf("Jarang bergerak (lebih banyak duduk)", "Kadang aktif (jalan santai, aktivitas ringan)", "Cukup aktif (olahraga ringan 2-3x/minggu)", "Sangat aktif (olahraga rutin ≥4x/minggu)"),
                selectedOption = state.activityLevel,
                onOptionSelected = { viewModel.updateActivityLevel(it) }
            )

            // Question 6 - Commitment
            QuestionItem(
                number = "6️",
                question = "Seberapa yakin kamu bisa konsisten menjalani pola makan sehat?",
                options = listOf("Sulit", "Lumayan", "Cukup yakin", "Sangat yakin"),
                selectedOption = state.commitment,
                onOptionSelected = { viewModel.updateCommitment(it) }
            )

            // Question 7 - CVD Risk Consideration
            Column {
                Text(
                    "7️ Apakah kamu ingin rekomendasi diet ini mempertimbangkan hasil risiko penyakit jantung kamu?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Option 1: Yes, consider CVD risk
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = if (useCVDData && cvdDataAvailable) Color(0xFF3B82F6) else Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                color = if (useCVDData && cvdDataAvailable) Color(0xFFEFF6FF) else Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = cvdDataAvailable) {
                                useCVDData = true
                                viewModel.updateUseCVDData(true)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = useCVDData && cvdDataAvailable,
                            onCheckedChange = {
                                useCVDData = it
                                viewModel.updateUseCVDData(it)
                            },
                            enabled = cvdDataAvailable,
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF3B82F6))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Ya, pertimbangkan risiko jantung saya",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (cvdDataAvailable) Color(0xFF1F2937) else Color(0xFF9CA3AF)
                            )
                            if (!cvdDataAvailable) {
                                Text(
                                    "(Lakukan CVD Risk Predictor terlebih dahulu)",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        if (useCVDData && cvdDataAvailable) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Option 2: No, general recommendation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = if (!useCVDData) pinkMain else Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                color = if (!useCVDData) Color(0xFFFCE7F3) else Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                useCVDData = false
                                viewModel.updateUseCVDData(false)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = !useCVDData,
                            onCheckedChange = {
                                useCVDData = !it
                                viewModel.updateUseCVDData(!it)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = pinkMain)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Tidak, saya ingin rekomendasi umum saja",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.weight(1f)
                        )
                        if (!useCVDData) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = pinkMain,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    val result = viewModel.calculateDiet()
                    navController.navigate("diet_result/${result.bestDiet}/${result.scores.joinToString(",")}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pinkMain),
                enabled = state.bloodPressure.isNotEmpty() && state.cholesterol.isNotEmpty() &&
                        state.foodPreference.isNotEmpty() && state.activityLevel.isNotEmpty() &&
                        state.commitment.isNotEmpty()
            ) {
                Text("Dapatkan Rekomendasi Diet", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun QuestionItem(
    number: String,
    question: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val pinkMain = Color(0xFFFF6FB1)

    Column {
        Text(
            "$number $question",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = if (selectedOption == option) pinkMain else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = if (selectedOption == option) Color(0xFFFCE7F3) else Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onOptionSelected(option) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOption == option,
                        onClick = { onOptionSelected(option) },
                        colors = RadioButtonDefaults.colors(selectedColor = pinkMain)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        option,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937),
                        modifier = Modifier.weight(1f)
                    )
                    if (selectedOption == option) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = pinkMain,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
