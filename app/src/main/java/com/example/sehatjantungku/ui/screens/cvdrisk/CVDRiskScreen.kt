package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val pinkMain = PinkMain

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("Panduan Pengisian") },
            text = {
                Text(
                    """
                    1. Isi semua data dengan benar.
                    2. Hitung BMI terlebih dahulu sebelum melanjutkan.
                    3. Pastikan semua pertanyaan dijawab.
                    4. Tekan tombol Hitung Risiko Total untuk melihat hasil.
                    """.trimIndent()
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Mengerti", color = pinkMain)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CVD Risk Predictor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.Default.Info, "Info")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = pinkMain,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Data Pribadi Section
            item {
                FormSectionTitle("Data Pribadi")

                CustomCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Jenis Kelamin", fontWeight = FontWeight.Medium, color = Color(0xFF333333))
                        Spacer(modifier = Modifier.height(12.dp))

                        // --- TATA LETAK JENIS KELAMIN (Vertikal) ---
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            GenderRadioButton(
                                label = "Pria",
                                selected = state.gender == "Pria",
                                onClick = { viewModel.updateGender("Pria") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            GenderRadioButton(
                                label = "Wanita",
                                selected = state.gender == "Wanita",
                                onClick = { viewModel.updateGender("Wanita") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        // --- AKHIR TATA LETAK JENIS KELAMIN ---

                        Spacer(modifier = Modifier.height(20.dp))

                        CustomOutlinedTextField(
                            value = state.age,
                            onValueChange = { viewModel.updateAge(it) },
                            label = "Umur (tahun)",
                            pinkMain = pinkMain
                        )
                    }
                }
            }

            // Data Fisik Section
            item {
                FormSectionTitle("Data Fisik")

                CustomCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            CustomOutlinedTextField(
                                value = state.height,
                                onValueChange = { viewModel.updateHeight(it) },
                                label = "Tinggi (m)",
                                modifier = Modifier.weight(1f),
                                pinkMain = pinkMain
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            CustomOutlinedTextField(
                                value = state.weight,
                                onValueChange = { viewModel.updateWeight(it) },
                                label = "Berat (kg)",
                                modifier = Modifier.weight(1f),
                                pinkMain = pinkMain
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.calculateBMI() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = pinkMain),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Hitung BMI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        CustomOutlinedTextField(
                            value = state.bmi,
                            onValueChange = { },
                            label = "BMI",
                            enabled = false,
                            pinkMain = pinkMain
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CustomOutlinedTextField(
                            value = state.bloodPressure,
                            onValueChange = { viewModel.updateBloodPressure(it) },
                            label = "Tekanan Darah Sistolik (mmHg)",
                            pinkMain = pinkMain
                        )
                    }
                }
            }

            // Data Kesehatan Section
            item {
                FormSectionTitle("Riwayat Kesehatan")

                CustomCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // --- TATA LETAK RIWAYAT KESEHATAN (Vertikal) ---
                        HealthQuestionItem(
                            question = "Apakah Anda memiliki Riwayat Diabetes?",
                            selected = state.diabetes,
                            onSelectionChange = { viewModel.updateDiabetes(it) },
                            pinkMain = pinkMain
                        )
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                        HealthQuestionItem(
                            question = "Apakah Anda seorang Perokok?",
                            selected = state.smoker,
                            onSelectionChange = { viewModel.updateSmoker(it) },
                            pinkMain = pinkMain
                        )
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                        HealthQuestionItem(
                            question = "Apakah Anda memiliki Riwayat Hipertensi?",
                            selected = state.hypertension,
                            onSelectionChange = { viewModel.updateHypertension(it) },
                            pinkMain = pinkMain
                        )
                        // --- AKHIR TATA LETAK RIWAYAT KESEHATAN ---
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        val (riskScore, heartAge) = viewModel.calculateRisk()
                        // Navigasi menggunakan format rute: cvd_risk_result/{heartAge}/{riskScore}
                        navController.navigate("cvd_risk_result/$heartAge/$riskScore")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = pinkMain),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hitung Risiko Total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// --- COMPOSE HELPER FUNCTIONS UNTUK TAMPILAN MODERN ---

@Composable
fun FormSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1F2937),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun CustomCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        content()
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    pinkMain: Color,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = true,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = pinkMain,
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedLabelColor = pinkMain,
            disabledTextColor = Color(0xFF333333),
            disabledBorderColor = Color(0xFFE5E7EB),
            disabledLabelColor = Color.Gray,
            disabledContainerColor = Color(0xFFFAFAFA)
        )
    )
}

@Composable
fun GenderRadioButton(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val pinkMain = PinkMain
    Card(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) pinkMain.copy(alpha = 0.1f) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            if (selected) pinkMain else Color(0xFFE5E7EB)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = pinkMain,
                    unselectedColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                label,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) pinkMain else Color.Black
            )
        }
    }
}


@Composable
fun HealthQuestionItem(
    question: String,
    selected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    pinkMain: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(question, fontWeight = FontWeight.Medium, color = Color(0xFF333333))
        Spacer(modifier = Modifier.height(12.dp))

        // Menggunakan Column untuk Vertikal
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HealthOptionCard(
                label = "Ya",
                isSelected = selected,
                onClick = { onSelectionChange(true) },
                pinkMain = pinkMain,
                modifier = Modifier.fillMaxWidth() // Mengambil lebar penuh
            )
            HealthOptionCard(
                label = "Tidak",
                isSelected = !selected,
                onClick = { onSelectionChange(false) },
                pinkMain = pinkMain,
                modifier = Modifier.fillMaxWidth() // Mengambil lebar penuh
            )
        }
    }
}

@Composable
private fun HealthOptionCard(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    pinkMain: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) pinkMain.copy(alpha = 0.1f) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            if (isSelected) pinkMain else Color(0xFFE5E7EB)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) pinkMain else Color.Black
            )
        }
    }
}