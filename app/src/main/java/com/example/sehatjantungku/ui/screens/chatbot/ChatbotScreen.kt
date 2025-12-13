// ChatbotScreen.kt (Kode Lengkap)
package com.example.sehatjantungku.ui.screens.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel // <-- Tambahkan import ini!

// HAPUS data class ChatMessage dari file ini, karena sudah ada di ChatbotViewModel.kt!
// Jika Anda ingin mempertahankan ChatMessage di file ini, hapus dari ViewModel,
// tapi praktik terbaik adalah menyimpannya bersama logika state di ViewModel.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(
    navController: NavController,
    // Menggunakan ViewModel untuk mengelola state dan API
    viewModel: ChatbotViewModel = viewModel()
) {
    // Ambil state pesan dari ViewModel (akan otomatis terupdate)
    val messages by viewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    val pinkMain = Color(0xFFFF6FB1)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Chatbot", fontWeight = FontWeight.Bold)
                        Text(
                            "Asisten Kesehatan Jantung",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                // Gunakan reverseLayout agar pesan terbaru muncul di bawah (seperti chat)
                reverseLayout = true
            ) {
                // Tampilkan pesan dari ViewModel (menggunakan key agar performa bagus)
                items(messages.reversed(), key = { it.message + it.isUser }) { message ->
                    ChatBubble(message)
                }
            }

            // Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tanyakan tentang kesehatan jantung...") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = pinkMain,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            // Panggil fungsi pengirim pesan di ViewModel
                            viewModel.sendMessage(inputText)
                            inputText = "" // Reset input
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(pinkMain, RoundedCornerShape(24.dp)),
                    // Disable tombol jika input kosong
                    enabled = inputText.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (message.isUser) Color(0xFFFF6FB1) else Color.White,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.message,
                color = if (message.isUser) Color.White else Color.Black,
                fontSize = 14.sp
            )
        }
    }
}