package com.example.sehatjantungku.ui.screens.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
            title = { Text("Panduan Pengisian", fontWeight = FontWeight.Bold) },
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
                    Text("OK", color = pinkMain, fontWeight = FontWeight.Bold)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9FAFB)) // Light gray background for better contrast
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "Lengkapi data di bawah untuk mendapatkan rekomendasi diet yang dipersonalisasi.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            // Question 1 - Blood Pressure (Dropdown)
            DropdownQuestion(
                number = "1️",
                question = "Bagaimana kondisi tekanan darah kamu?",
                options = listOf("Normal / terkontrol", "Kadang tinggi", "Sering tinggi / didiagnosis darah tinggi", "Tidak tahu"),
                selectedOption = state.bloodPressure,
                onOptionSelected = { viewModel.updateBloodPressure(it) }
            )

            // Question 2 - Cholesterol (Dropdown)
            DropdownQuestion(
                number = "2️",
                question = "Bagaimana kondisi kolesterol kamu?",
                options = listOf("Normal", "Agak tinggi", "Tinggi / pernah disarankan diet", "Tidak tahu"),
                selectedOption = state.cholesterol,
                onOptionSelected = { viewModel.updateCholesterol(it) }
            )

            // Question 3 - Health Conditions (Tetap Checkbox karena Multiple Choice)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "3️ Kondisi kesehatan lainnya",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Bagian didalam Column untuk Health Conditions
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Tekanan darah tinggi", "Kolesterol tinggi", "Diabetes", "Asam urat", "Tidak ada").forEach { condition ->
                            val isSelected = state.healthConditions.contains(condition)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected) Color(0xFFFCE7F3) else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.toggleHealthCondition(condition) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { viewModel.toggleHealthCondition(condition) },
                                    colors = CheckboxDefaults.colors(checkedColor = pinkMain)
                                )
                                // Perhatikan penggunaan .sp di bawah ini:
                                Text(
                                    text = condition,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Question 4 - Food Preference (Dropdown)
            DropdownQuestion(
                number = "4️",
                question = "Preferensi makanan kamu?",
                options = listOf("Nabati dominan", "Hewani dominan", "Nabati dan hewani seimbang", "Praktis / instan"),
                selectedOption = state.foodPreference,
                onOptionSelected = { viewModel.updateFoodPreference(it) }
            )

            // Question 5 - Activity Level (Dropdown)
            DropdownQuestion(
                number = "5️",
                question = "Seberapa aktif kamu bergerak?",
                options = listOf("Jarang bergerak", "Kadang aktif", "Cukup aktif (2-3x/minggu)", "Sangat aktif (≥4x/minggu)"),
                selectedOption = state.activityLevel,
                onOptionSelected = { viewModel.updateActivityLevel(it) }
            )

            // Question 6 - Commitment (Dropdown)
            DropdownQuestion(
                number = "6️",
                question = "Keyakinan untuk konsisten?",
                options = listOf("Sulit", "Lumayan", "Cukup yakin", "Sangat yakin"),
                selectedOption = state.commitment,
                onOptionSelected = { viewModel.updateCommitment(it) }
            )

            // Question 7 - CVD Risk (Toggle/Card Style)
            CVDSelectionCard(
                cvdAvailable = cvdDataAvailable,
                useCVDData = useCVDData,
                onSelectionChange = {
                    useCVDData = it
                    viewModel.updateUseCVDData(it)
                }
            )

            // Submit Button
            Button(
                onClick = {
                    val result = viewModel.calculateDiet()
                    navController.navigate("diet_result/${result.bestDiet}/${result.scores.joinToString(",")}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pinkMain),
                enabled = state.bloodPressure.isNotEmpty() && state.cholesterol.isNotEmpty() &&
                        state.foodPreference.isNotEmpty() && state.activityLevel.isNotEmpty() &&
                        state.commitment.isNotEmpty()
            ) {
                Text("Dapatkan Rekomendasi Diet", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownQuestion(
    number: String,
    question: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "$number $question",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedOption.ifEmpty { "Pilih salah satu..." },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6FB1),
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onOptionSelected(option)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CVDSelectionCard(
    cvdAvailable: Boolean,
    useCVDData: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    val pinkMain = Color(0xFFFF6FB1)
    val blueMain = Color(0xFF3B82F6)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "7️ Pertimbangkan Skor Risiko Jantung?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Option: Yes
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = cvdAvailable) { onSelectionChange(true) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (useCVDData && cvdAvailable) blueMain.copy(alpha = 0.1f) else Color.White,
                    border = BorderStroke(1.dp, if (useCVDData && cvdAvailable) blueMain else Color(0xFFE5E7EB))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RadioButton(
                            selected = useCVDData && cvdAvailable,
                            onClick = { if (cvdAvailable) onSelectionChange(true) },
                            enabled = cvdAvailable
                        )
                        Text(
                            "Gunakan Data",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (cvdAvailable) Color.Unspecified else Color.LightGray
                        )
                        if (!cvdAvailable) {
                            Text("Belum Ada", fontSize = 10.sp, color = Color.Red)
                        }
                    }
                }

                // Option: No
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectionChange(false) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (!useCVDData) pinkMain.copy(alpha = 0.1f) else Color.White,
                    border = BorderStroke(1.dp, if (!useCVDData) pinkMain else Color(0xFFE5E7EB))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RadioButton(
                            selected = !useCVDData,
                            onClick = { onSelectionChange(false) }
                        )
                        Text("Saran Umum", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}