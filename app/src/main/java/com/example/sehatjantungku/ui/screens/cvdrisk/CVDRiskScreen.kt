package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sehatjantungku.ui.theme.PinkMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVDRiskScreen(
    navController: NavController,
    viewModel: CVDRiskViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showInfoDialog by remember { mutableStateOf(false) }

    // Dialog Info (Tetap sama)
    if (showInfoDialog) {
        Dialog(onDismissRequest = { showInfoDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = PinkMain,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Cara Pengisian Form",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "1. Data Pribadi: Pilih jenis kelamin dan masukkan umur Anda.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        "2. Data Fisik: Masukkan tinggi badan dalam meter (contoh: 1.70) dan berat badan dalam kg, lalu klik \"Hitung BMI\".",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        "3. Data Kesehatan: Masukkan tekanan darah sistolik (angka atas) dan jawab pertanyaan tentang kondisi kesehatan Anda.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        "4. Hitung Risiko: Setelah semua data terisi, klik \"Hitung Risiko Total\" untuk melihat prediksi risiko kardiovaskular Anda.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    Button(
                        onClick = { showInfoDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinkMain
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mengerti")
                    }
                }
            }
        }
    }

    // MEMBUNGKUS SELURUH KONTEN DENGAN SCAFFOLD
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "CVD Risk Predictor",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Hitung risiko penyakit kardiovaskular Anda",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Menggunakan Transparent agar gradient di bawah terlihat
                ),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF8CCF), Color(0xFFCC7CF0))
                    )
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues) // MENGGUNAKAN PADDING DARI SCAFFOLD
                .padding(horizontal = 20.dp), // Padding horizontal tetap
        ) {
            // Konten form dipindahkan ke sini
            item {
                Spacer(modifier = Modifier.height(20.dp)) // Jarak awal setelah TopBar

                Text(
                    text = "Data Pribadi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    "Jenis Kelamin",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 24.dp)
                    ) {
                        RadioButton(
                            selected = state.gender == "Pria",
                            onClick = { viewModel.updateGender("Pria") },
                            colors = RadioButtonDefaults.colors(selectedColor = PinkMain)
                        )
                        Text(
                            "Pria",
                            fontSize = 14.sp,
                            color = Color(0xFF374151)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.gender == "Wanita",
                            onClick = { viewModel.updateGender("Wanita") },
                            colors = RadioButtonDefaults.colors(selectedColor = PinkMain)
                        )
                        Text(
                            "Wanita",
                            fontSize = 14.sp,
                            color = Color(0xFF374151)
                        )
                    }
                }

                Text(
                    "Umur",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = state.age,
                    onValueChange = { viewModel.updateAge(it) },
                    placeholder = { Text("Masukkan umur (tahun)", fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkMain,
                        unfocusedBorderColor = Color(0xFFD1D5DB)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            item {
                Text(
                    text = "Data Fisik",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Tinggi (m)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = state.height,
                            onValueChange = { viewModel.updateHeight(it) },
                            placeholder = { Text("1.70", fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PinkMain,
                                unfocusedBorderColor = Color(0xFFD1D5DB)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Berat (kg)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = state.weight,
                            onValueChange = { viewModel.updateWeight(it) },
                            placeholder = { Text("65", fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PinkMain,
                                unfocusedBorderColor = Color(0xFFD1D5DB)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Button(
                    onClick = { viewModel.calculateBMI() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PinkMain
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Hitung BMI", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }

                Text(
                    "BMI",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = state.bmi,
                    onValueChange = { },
                    placeholder = { Text("Hasil BMI akan muncul di sini", fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color(0xFF374151),
                        disabledBorderColor = Color(0xFFD1D5DB),
                        disabledPlaceholderColor = Color(0xFF9CA3AF),
                        disabledContainerColor = Color(0xFFF3F4F6)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            item {
                Text(
                    text = "Data Kesehatan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    "Tekanan Darah (mmHg)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = state.bloodPressure,
                    onValueChange = { viewModel.updateBloodPressure(it) },
                    placeholder = { Text("Contoh: 120", fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkMain,
                        unfocusedBorderColor = Color(0xFFD1D5DB)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                HealthQuestionItem("Diabetes", state.diabetes) { viewModel.updateDiabetes(it) }
                Spacer(modifier = Modifier.height(20.dp))
                HealthQuestionItem("Perokok", state.smoker) { viewModel.updateSmoker(it) }
                Spacer(modifier = Modifier.height(20.dp))
                HealthQuestionItem("Hipertensi", state.hypertension) { viewModel.updateHypertension(it) }

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Button(
                    onClick = {
                        val result = viewModel.calculateRisk()
                        // Menggunakan rute yang diperbaiki di Navigation.kt (riskScore/heartAge)
                        navController.navigate("cvd_risk_result/${result.first}/${result.second}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFCC7CF0)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Hitung Risiko Total",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HealthQuestionItem(
    question: String,
    selected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Column {
        Text(
            question,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF374151),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 24.dp)
            ) {
                RadioButton(
                    selected = selected,
                    onClick = { onSelectionChange(true) },
                    colors = RadioButtonDefaults.colors(selectedColor = PinkMain)
                )
                Text(
                    "Ya",
                    fontSize = 14.sp,
                    color = Color(0xFF374151)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = !selected,
                    onClick = { onSelectionChange(false) },
                    colors = RadioButtonDefaults.colors(selectedColor = PinkMain)
                )
                Text(
                    "Tidak",
                    fontSize = 14.sp,
                    color = Color(0xFF374151)
                )
            }
        }
    }
}