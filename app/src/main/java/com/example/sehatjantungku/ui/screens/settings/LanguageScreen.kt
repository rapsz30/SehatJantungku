package com.example.sehatjantungku.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(navController: NavController) {
    var selectedLanguage by remember { mutableStateOf("Bahasa Indonesia") }

    val languages = listOf(
        "Bahasa Indonesia",
        "English",
        "中文 (Chinese)",
        "日本語 (Japanese)",
        "한국어 (Korean)",
        "العربية (Arabic)"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bahasa") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            languages.forEach { language ->
                val isAvailable = language == "Bahasa Indonesia"

                LanguageItem(
                    language = language,
                    isSelected = selectedLanguage == language,
                    isAvailable = isAvailable,
                    onClick = {
                        if (isAvailable) {
                            selectedLanguage = language
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun LanguageItem(
    language: String,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit
) {
    val containerColor = when {
        isSelected -> Color(0xFFFFEEF7)
        !isAvailable -> Color(0xFFF5F5F5) // Abu-abu muda
        else -> Color.White
    }

    val contentColor = if (isAvailable) Color.Black else Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAvailable, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isAvailable) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = language,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) Color(0xFFFF6FB1) else contentColor
                )

                if (!isAvailable) {
                    Text(
                        text = "Akan Datang",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFFFF6FB1)
                )
            }
        }
    }
}