package com.example.sehatjantungku.ui.screens.cvdrisk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
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
    
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("Panduan Pengisian") },
            text = {
                Text(
                    """
                    1. Isi semua data dengan benar
                    2. Hitung BMI terlebih dahulu sebelum melanjutkan
                    3. Pastikan semua pertanyaan dijawab
                    4. Tekan tombol Hitung Risiko Total untuk melihat hasil
                    """.trimIndent()
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Mengerti")
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
                    containerColor = PinkMain,
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
                .padding(16.dp)
        ) {
            // Data Pribadi Section
            item {
                Text(
                    text = "Data Pribadi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Jenis Kelamin", fontWeight = FontWeight.Medium)
                        Row {
                            RadioButton(
                                selected = state.gender == "Pria",
                                onClick = { viewModel.updateGender("Pria") }
                            )
                            Text("Pria", modifier = Modifier.align(Alignment.CenterVertically))
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(
                                selected = state.gender == "Wanita",
                                onClick = { viewModel.updateGender("Wanita") }
                            )
                            Text("Wanita", modifier = Modifier.align(Alignment.CenterVertically))
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = state.age,
                            onValueChange = { viewModel.updateAge(it) },
                            label = { Text("Umur (tahun)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Data Fisik Section
            item {
                Text(
                    text = "Data Fisik",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = state.height,
                                onValueChange = { viewModel.updateHeight(it) },
                                label = { Text("Tinggi (m)") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = state.weight,
                                onValueChange = { viewModel.updateWeight(it) },
                                label = { Text("Berat (kg)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Button(
                            onClick = { viewModel.calculateBMI() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PinkMain)
                        ) {
                            Text("Hitung BMI")
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = state.bmi,
                            onValueChange = { },
                            label = { Text("BMI") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledBorderColor = Color.Gray,
                                disabledLabelColor = Color.Gray
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = state.bloodPressure,
                            onValueChange = { viewModel.updateBloodPressure(it) },
                            label = { Text("Tekanan Darah (mmHg)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Data Kesehatan Section
            item {
                Text(
                    text = "Data Kesehatan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        HealthQuestionItem("Diabetes", state.diabetes) { viewModel.updateDiabetes(it) }
                        Spacer(modifier = Modifier.height(16.dp))
                        HealthQuestionItem("Perokok", state.smoker) { viewModel.updateSmoker(it) }
                        Spacer(modifier = Modifier.height(16.dp))
                        HealthQuestionItem("Hipertensi", state.hypertension) { viewModel.updateHypertension(it) }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Submit Button
            item {
                Button(
                    onClick = {
                        val result = viewModel.calculateRisk()
                        navController.navigate("cvd_risk_result/${result.first}/${result.second}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinkMain),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hitung Risiko Total", fontSize = 16.sp)
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
        Text(question, fontWeight = FontWeight.Medium)
        Row {
            RadioButton(
                selected = selected,
                onClick = { onSelectionChange(true) }
            )
            Text("Ya", modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = !selected,
                onClick = { onSelectionChange(false) }
            )
            Text("Tidak", modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}
