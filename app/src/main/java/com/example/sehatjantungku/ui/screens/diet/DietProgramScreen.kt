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
    val cvdDataAvailable = remember { mutableStateOf(false) } // Check from SharedPreferences

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
            // Question 1
            QuestionItem(
                number = "1️⃣",
                question = "Apa tujuan Anda saat ini?",
                options = listOf("Menurunkan berat badan", "Menjaga berat badan", "Menambah berat badan"),
                selectedOption = state.goal,
                onOptionSelected = { viewModel.updateGoal(it) }
            )

            // Question 2
            QuestionItem(
                number = "2️⃣",
                question = "Seberapa ketat Anda ingin mengurangi garam?",
                options = listOf("Sangat ketat", "Cukup ketat", "Tidak terlalu ketat"),
                selectedOption = state.saltReduction,
                onOptionSelected = { viewModel.updateSaltReduction(it) }
            )

            // Question 3
            QuestionItem(
                number = "3️⃣",
                question = "Apakah Anda ingin mengurangi makanan berlemak/berminyak?",
                options = listOf("Ya, sangat ingin", "Ingin sedikit saja", "Tidak terlalu peduli"),
                selectedOption = state.fatReduction,
                onOptionSelected = { viewModel.updateFatReduction(it) }
            )

            // Question 4
            QuestionItem(
                number = "4️⃣",
                question = "Anda lebih suka makanan kaya sayur & buah?",
                options = listOf("Ya", "Biasa saja", "Tidak terlalu"),
                selectedOption = state.vegetablePreference,
                onOptionSelected = { viewModel.updateVegetablePreference(it) }
            )

            // Question 5
            QuestionItem(
                number = "5️⃣",
                question = "Apakah Anda ingin membatasi konsumsi telur, daging merah, dan santan?",
                options = listOf("Ya, batasi banyak", "Batasi sedikit", "Tidak masalah"),
                selectedOption = state.meatRestriction,
                onOptionSelected = { viewModel.updateMeatRestriction(it) }
            )

            // Question 6
            QuestionItem(
                number = "6️⃣",
                question = "Apakah Anda nyaman makan buah & sayur setiap hari?",
                options = listOf("Sangat nyaman", "Kadang-kadang", "Tidak terlalu suka"),
                selectedOption = state.dailyVegetable,
                onOptionSelected = { viewModel.updateDailyVegetable(it) }
            )

            // CVD Data Checkbox (if available)
            if (cvdDataAvailable.value) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Data CVD Risk Tersedia",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E40AF)
                                )
                                Text(
                                    "Gunakan hasil prediksi CVD Anda untuk rekomendasi diet yang lebih tepat sasaran",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E40AF),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, Color(0xFF3B82F6), RoundedCornerShape(12.dp))
                                .clickable { useCVDData = !useCVDData }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = useCVDData,
                                onCheckedChange = { useCVDData = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF3B82F6))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Pertimbangkan hasil CVD Risk saya",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E40AF),
                                modifier = Modifier.weight(1f)
                            )
                            if (useCVDData) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
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
                enabled = state.goal.isNotEmpty() && state.saltReduction.isNotEmpty() &&
                        state.fatReduction.isNotEmpty() && state.vegetablePreference.isNotEmpty() &&
                        state.meatRestriction.isNotEmpty() && state.dailyVegetable.isNotEmpty()
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
