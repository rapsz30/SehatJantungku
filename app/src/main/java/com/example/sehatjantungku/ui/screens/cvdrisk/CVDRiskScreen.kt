package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVDRiskScreen(
    navController: NavController,
    viewModel: CVDRiskViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showInfoDialog by remember { mutableStateOf(false) }

    // Warna tema
    val pinkMain = Color(0xFFFF6FB1)
    val bgGray = Color(0xFFF9FAFB)
    val textDark = Color(0xFF1F2937)

    // Dialog Info
    if (showInfoDialog) {
        Dialog(onDismissRequest = { showInfoDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = pinkMain)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Panduan Pengisian", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoItem("1. Data Pribadi", "Pilih jenis kelamin dan masukkan umur (tahun).")
                    InfoItem("2. Data Fisik", "Isi tinggi (meter, cth: 1.70) dan berat (kg). Klik 'Hitung BMI'.")
                    InfoItem("3. Data Kesehatan", "Masukkan tekanan darah sistolik (angka atas) dan kondisi kesehatan.")
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showInfoDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = pinkMain),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Mengerti", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("CVD Risk Predictor", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Analisis risiko jantung", fontSize = 12.sp, color = Color.White.copy(0.9f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.Default.Info, "Info", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(
                    Brush.horizontalGradient(colors = listOf(Color(0xFFFF8CCF), Color(0xFFCC7CF0)))
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGray)
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- Section 1: Data Pribadi ---
            item {
                SectionCard(title = "Data Pribadi", icon = Icons.Default.Person, pinkMain = pinkMain) {
                    Text("Jenis Kelamin", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SelectionCard(
                            text = "Pria",
                            selected = state.gender == "Pria",
                            onClick = { viewModel.updateGender("Pria") },
                            modifier = Modifier.weight(1f),
                            activeColor = pinkMain
                        )
                        SelectionCard(
                            text = "Wanita",
                            selected = state.gender == "Wanita",
                            onClick = { viewModel.updateGender("Wanita") },
                            modifier = Modifier.weight(1f),
                            activeColor = pinkMain
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomInputField(
                        label = "Umur",
                        value = state.age,
                        onValueChange = { viewModel.updateAge(it) },
                        suffix = "Tahun",
                        activeColor = pinkMain
                    )
                }
            }

            // --- Section 2: Data Fisik ---
            item {
                SectionCard(title = "Data Fisik & BMI", icon = Icons.Default.MonitorWeight, pinkMain = pinkMain) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            CustomInputField(
                                label = "Tinggi",
                                value = state.height,
                                onValueChange = { viewModel.updateHeight(it) },
                                suffix = "m",
                                placeholder = "1.70",
                                activeColor = pinkMain
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            CustomInputField(
                                label = "Berat",
                                value = state.weight,
                                onValueChange = { viewModel.updateWeight(it) },
                                suffix = "kg",
                                activeColor = pinkMain
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.calculateBMI() },
                            colors = ButtonDefaults.buttonColors(containerColor = pinkMain),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Icon(Icons.Default.Calculate, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hitung")
                        }

                        // Hasil BMI
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .background(
                                    if (state.bmi.isNotEmpty()) Color(0xFFEFF6FF) else Color(0xFFF3F4F6),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    if (state.bmi.isNotEmpty()) Color(0xFF3B82F6) else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (state.bmi.isNotEmpty()) "BMI: ${state.bmi}" else "Hasil BMI",
                                fontWeight = FontWeight.Bold,
                                color = if (state.bmi.isNotEmpty()) Color(0xFF1E40AF) else Color.Gray
                            )
                        }
                    }
                }
            }

            // --- Section 3: Data Kesehatan ---
            item {
                SectionCard(title = "Kondisi Kesehatan", icon = Icons.Default.CheckCircle, pinkMain = pinkMain) {
                    CustomInputField(
                        label = "Tekanan Darah Sistolik",
                        value = state.bloodPressure,
                        onValueChange = { viewModel.updateBloodPressure(it) },
                        suffix = "mmHg",
                        placeholder = "120",
                        activeColor = pinkMain
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    YesNoQuestion("Apakah Anda menderita Diabetes?", state.diabetes, pinkMain) { viewModel.updateDiabetes(it) }
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))
                    YesNoQuestion("Apakah Anda Merokok?", state.smoker, pinkMain) { viewModel.updateSmoker(it) }
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))
                    YesNoQuestion("Apakah ada Riwayat Hipertensi?", state.hypertension, pinkMain) { viewModel.updateHypertension(it) }
                }
            }

            // --- Tombol Submit ---
            item {
                Button(
                    onClick = {
                        val result = viewModel.calculateRisk()
                        navController.navigate("cvd_risk_result/${result.first}/${result.second}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC7CF0)), // Purple Theme
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Text("Hitung Risiko Total", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// --- Helper Components untuk UI yang Rapi ---

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    pinkMain: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(pinkMain.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = pinkMain, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            }
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String? = null,
    placeholder: String = "",
    activeColor: Color
) {
    Column {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4B5563))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = activeColor,
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF9FAFB)
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, // KEYBOARD ANGKA
                imeAction = ImeAction.Next
            ),
            trailingIcon = if (suffix != null) {
                { Text(suffix, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(end = 8.dp)) }
            } else null
        )
    }
}

@Composable
fun SelectionCard(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color
) {
    Surface(
        modifier = modifier
            .height(50.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) activeColor.copy(alpha = 0.1f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) activeColor else Color(0xFFE5E7EB)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            if (selected) {
                Icon(Icons.Default.CheckCircle, null, tint = activeColor, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) activeColor else Color.Black
            )
        }
    }
}

@Composable
fun YesNoQuestion(
    question: String,
    stateValue: Boolean, // True = Ya, False = Tidak (Default false)
    activeColor: Color,
    onValueChange: (Boolean) -> Unit
) {
    Column {
        Text(question, fontSize = 14.sp, color = Color(0xFF374151), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SelectionCard(
                text = "Ya",
                selected = stateValue,
                onClick = { onValueChange(true) },
                modifier = Modifier.weight(1f),
                activeColor = activeColor
            )
            SelectionCard(
                text = "Tidak",
                selected = !stateValue,
                onClick = { onValueChange(false) },
                modifier = Modifier.weight(1f),
                activeColor = activeColor
            )
        }
    }
}

@Composable
fun InfoItem(title: String, desc: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(desc, fontSize = 13.sp, color = Color.Gray)
    }
}