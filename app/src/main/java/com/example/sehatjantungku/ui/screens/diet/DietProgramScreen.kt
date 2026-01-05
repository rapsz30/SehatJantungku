package com.example.sehatjantungku.ui.screens.diet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    val bgGray = Color(0xFFF9FAFB)

    val state by viewModel.state.collectAsState()
    val cvdAvailable by viewModel.cvdDataAvailable.collectAsState()

    val dietProgress by viewModel.dietProgress.collectAsState()
    val isLoadingProgress by viewModel.isLoadingProgress.collectAsState()

    var hasCheckedStatus by remember { mutableStateOf(false) }

    // 1. Cek status diet saat layar dibuka
    LaunchedEffect(Unit) {
        viewModel.loadUserDietProgress()
    }

    // 2. Pantau hasil pengecekan (Redirect jika ada diet aktif)
    LaunchedEffect(dietProgress, isLoadingProgress) {
        if (!isLoadingProgress) {
            if (dietProgress != null && !dietProgress!!.isCompleted) {
                val activeDietId = dietProgress!!.dietId
                navController.navigate("diet_start/$activeDietId") {
                    popUpTo("diet_program") { inclusive = true }
                }
            } else {
                hasCheckedStatus = true
            }
        }
    }

    var showInfo by remember { mutableStateOf(false) }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            icon = { Icon(Icons.Default.Info, null, tint = pinkMain) },
            title = { Text("Metode Personalisasi", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Sistem kami menggunakan metode SAW (Simple Additive Weighting) untuk merekomendasikan diet terbaik.",
                        fontSize = 14.sp
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    Column {
                        Text("Faktor Penentu Utama:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        val points = listOf(
                            "🫀 Skor Risiko Jantung (CVD Risk)",
                            "🩸 Tekanan Darah & Kolesterol",
                            "🥗 Preferensi Makanan & Gaya Hidup"
                        )
                        points.forEach { point ->
                            Text(
                                text = "• $point",
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        "❤️ Jawaban yang jujur sangat penting untuk keamanan jantung Anda.",
                        fontSize = 13.sp,
                        color = pinkMain,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfo = false }) {
                    Text("Mengerti", color = pinkMain, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personalisasi Diet", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showInfo = true }) {
                        Icon(Icons.Default.Info, "Info", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(
                    Brush.horizontalGradient(colors = listOf(Color(0xFFFF8CCF), Color(0xFFCC7CF0)))
                )
            )
        }
    ) { padding ->
        if (isLoadingProgress || !hasCheckedStatus) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = pinkMain)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(bgGray)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Text(
                        "Lengkapi data berikut agar kami bisa memilihkan program diet yang paling efektif untuk jantung Anda.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }

                // Question 1 - Blood Pressure
                item {
                    DietQuestionCard(
                        number = "1",
                        question = "Bagaimana kondisi tekanan darah kamu saat ini?",
                        icon = Icons.Default.MonitorHeart,
                        activeColor = pinkMain
                    ) {
                        val options = listOf("Normal / terkontrol", "Kadang tinggi", "Sering tinggi / Hipertensi", "Tidak tahu")
                        options.forEach { option ->
                            SelectionOption(
                                text = option,
                                selected = state.bloodPressure == option,
                                activeColor = pinkMain,
                                onClick = { viewModel.updateBloodPressure(option) }
                            )
                        }
                    }
                }

                // Question 2 - Cholesterol
                item {
                    DietQuestionCard(
                        number = "2",
                        question = "Bagaimana kondisi kolesterol kamu?",
                        icon = Icons.Default.HealthAndSafety,
                        activeColor = pinkMain
                    ) {
                        val options = listOf("Normal", "Agak tinggi", "Tinggi / Hiperkolesterolemia", "Tidak tahu")
                        options.forEach { option ->
                            SelectionOption(
                                text = option,
                                selected = state.cholesterol == option,
                                activeColor = pinkMain,
                                onClick = { viewModel.updateCholesterol(option) }
                            )
                        }
                    }
                }

                // Question 3 - Health Conditions
                item {
                    DietQuestionCard(
                        number = "3",
                        question = "Apakah kamu memiliki kondisi kesehatan berikut? (Boleh pilih lebih dari satu)",
                        icon = Icons.Default.CheckCircle,
                        activeColor = pinkMain
                    ) {
                        val conditions = listOf("Tekanan darah tinggi", "Kolesterol tinggi", "Diabetes", "Asam urat", "Tidak ada")
                        conditions.forEach { condition ->
                            val isSelected = state.healthConditions.contains(condition)
                            SelectionOption(
                                text = condition,
                                selected = isSelected,
                                activeColor = pinkMain,
                                onClick = { viewModel.toggleHealthCondition(condition) },
                                isCheckbox = true
                            )
                        }
                    }
                }

                // Question 4 - Food Preference
                item {
                    DietQuestionCard(
                        number = "4",
                        question = "Makanan seperti apa yang paling nyaman buat kamu jalani?",
                        icon = Icons.Default.Restaurant,
                        activeColor = pinkMain
                    ) {
                        val options = listOf("Nabati dominan", "Hewani dominan", "Seimbang", "Praktis / Instan")
                        options.forEach { option ->
                            SelectionOption(
                                text = option,
                                selected = state.foodPreference == option,
                                activeColor = pinkMain,
                                onClick = { viewModel.updateFoodPreference(option) }
                            )
                        }
                    }
                }

                // Question 5 - Activity Level
                item {
                    DietQuestionCard(
                        number = "5",
                        question = "Seberapa aktif kamu bergerak?",
                        icon = Icons.Default.SportsGymnastics,
                        activeColor = pinkMain
                    ) {
                        val options = listOf("Jarang bergerak (Sedenter)", "Kadang aktif (Ringan)", "Cukup aktif (2-3x/minggu)", "Sangat aktif (Atlet/Rutin)")
                        options.forEach { option ->
                            SelectionOption(
                                text = option,
                                selected = state.activityLevel == option,
                                activeColor = pinkMain,
                                onClick = { viewModel.updateActivityLevel(option) }
                            )
                        }
                    }
                }

                // Question 6 - Commitment
                item {
                    DietQuestionCard(
                        number = "6",
                        question = "Seberapa yakin kamu bisa konsisten?",
                        icon = Icons.Default.SelfImprovement,
                        activeColor = pinkMain
                    ) {
                        val options = listOf("Sulit", "Lumayan", "Cukup yakin", "Sangat yakin")
                        options.forEach { option ->
                            SelectionOption(
                                text = option,
                                selected = state.commitment == option,
                                activeColor = pinkMain,
                                onClick = { viewModel.updateCommitment(option) }
                            )
                        }
                    }
                }

                // Question 7 - CVD Risk Consideration
                item {
                    DietCVDSelectionCard(
                        cvdAvailable = cvdAvailable,
                        useCVDData = state.useCvdScore,
                        riskCategory = state.cvdRiskCategory,
                        onSelectionChange = { viewModel.updateUseCVDData(it) }
                    )
                }

                item {
                    Button(
                        onClick = {
                            val result = viewModel.calculateBestDiet()
                            navController.navigate("diet_result/${result.bestDietId}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = pinkMain),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        enabled = state.bloodPressure.isNotEmpty() && state.cholesterol.isNotEmpty() &&
                                state.foodPreference.isNotEmpty()
                    ) {
                        Text("Analisis & Dapatkan Rekomendasi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}


@Composable
fun DietQuestionCard(
    number: String,
    question: String,
    icon: ImageVector,
    activeColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(activeColor.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(number, fontWeight = FontWeight.Bold, color = activeColor)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun SelectionOption(
    text: String,
    selected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    isCheckbox: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) activeColor.copy(alpha = 0.1f) else Color.White,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) activeColor else Color(0xFFE5E7EB)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCheckbox) {
                Icon(
                    if (selected) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (selected) activeColor else Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                RadioButton(
                    selected = selected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = activeColor),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) activeColor else Color(0xFF374151)
            )
        }
    }
}

@Composable
fun DietCVDSelectionCard(
    cvdAvailable: Boolean,
    useCVDData: Boolean,
    riskCategory: String,
    onSelectionChange: (Boolean) -> Unit
) {
    val pinkMain = Color(0xFFFF6FB1)
    val blueMain = Color(0xFF3B82F6)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "7. Pertimbangkan Skor Risiko Jantung?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (cvdAvailable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(blueMain.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null, tint = blueMain, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Data ditemukan: Risiko $riskCategory",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = blueMain
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF4F4), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Belum ada riwayat tes CVD.",
                        fontSize = 12.sp,
                        color = Color.Red.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = cvdAvailable) {
                            if(cvdAvailable) onSelectionChange(true)
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (useCVDData && cvdAvailable) blueMain.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, if (useCVDData && cvdAvailable) blueMain else Color(0xFFE5E7EB))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .alpha(if (cvdAvailable) 1f else 0.4f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (useCVDData && cvdAvailable) {
                            Icon(Icons.Default.CheckCircle, null, tint = blueMain, modifier = Modifier.size(24.dp))
                        } else {
                            Spacer(modifier = Modifier.size(24.dp))
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Gunakan Data",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (cvdAvailable) blueMain else Color.Gray
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectionChange(false) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (!useCVDData) pinkMain.copy(alpha = 0.1f) else Color.White,
                    border = BorderStroke(1.dp, if (!useCVDData) pinkMain else Color(0xFFE5E7EB))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!useCVDData) {
                            Icon(Icons.Default.CheckCircle, null, tint = pinkMain, modifier = Modifier.size(24.dp))
                        } else {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Saran Umum", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if(!useCVDData) pinkMain else Color.Gray)
                    }
                }
            }
        }
    }
}