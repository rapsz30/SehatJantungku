package com.example.sehatjantungku.ui.screens.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Program Diet Personal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
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
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Question 1
            QuestionCard(
                question = "Apa tujuan Anda saat ini?",
                options = listOf("Menurunkan berat badan", "Menjaga berat badan", "Menambah berat badan"),
                selectedOption = state.goal,
                onOptionSelected = { viewModel.updateGoal(it) }
            )

            // Question 2
            QuestionCard(
                question = "Seberapa ketat Anda ingin mengurangi garam?",
                options = listOf("Sangat ketat", "Cukup ketat", "Tidak terlalu ketat"),
                selectedOption = state.saltReduction,
                onOptionSelected = { viewModel.updateSaltReduction(it) }
            )

            // Question 3
            QuestionCard(
                question = "Apakah Anda ingin mengurangi makanan berlemak/berminyak?",
                options = listOf("Ya, sangat ingin", "Ingin sedikit saja", "Tidak terlalu peduli"),
                selectedOption = state.fatReduction,
                onOptionSelected = { viewModel.updateFatReduction(it) }
            )

            // Question 4
            QuestionCard(
                question = "Anda lebih suka makanan kaya sayur & buah?",
                options = listOf("Ya", "Biasa saja", "Tidak terlalu"),
                selectedOption = state.vegetablePreference,
                onOptionSelected = { viewModel.updateVegetablePreference(it) }
            )

            // Question 5
            QuestionCard(
                question = "Apakah Anda ingin membatasi konsumsi telur, daging merah, dan santan?",
                options = listOf("Ya, batasi banyak", "Batasi sedikit", "Tidak masalah"),
                selectedOption = state.meatRestriction,
                onOptionSelected = { viewModel.updateMeatRestriction(it) }
            )

            // Question 6
            QuestionCard(
                question = "Apakah Anda nyaman makan buah & sayur setiap hari?",
                options = listOf("Sangat nyaman", "Kadang-kadang", "Tidak terlalu suka"),
                selectedOption = state.dailyVegetable,
                onOptionSelected = { viewModel.updateDailyVegetable(it) }
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pinkMain),
                enabled = state.goal.isNotEmpty() && state.saltReduction.isNotEmpty()
            ) {
                Text("Submit", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun QuestionCard(
    question: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                question,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(12.dp))

            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedOption == option,
                            onClick = { onOptionSelected(option) }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOption == option,
                        onClick = { onOptionSelected(option) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFFFF6FB1)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(option, fontSize = 14.sp)
                }
            }
        }
    }
}
