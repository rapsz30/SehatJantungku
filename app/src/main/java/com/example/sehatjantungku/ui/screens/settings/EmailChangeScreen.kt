package com.example.sehatjantungku.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailChangeScreen(navController: NavController) {
    var currentEmail by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ganti Email") },
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
                .background(Color.White)
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                text = "Email Saat Ini",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = currentEmail,
                onValueChange = { currentEmail = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Masukkan email saat ini") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6FB1),
                    focusedLabelColor = Color(0xFFFF6FB1)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Email Baru",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newEmail,
                onValueChange = { newEmail = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Masukkan email baru") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6FB1),
                    focusedLabelColor = Color(0xFFFF6FB1)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Konfirmasi Email Baru",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmEmail,
                onValueChange = { confirmEmail = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Konfirmasi email baru") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6FB1),
                    focusedLabelColor = Color(0xFFFF6FB1)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // TODO: Implement email change logic
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF6FB1),
                                    Color(0xFFCC7CF0)
                                )
                            )
                        ),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = "Simpan Perubahan",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
