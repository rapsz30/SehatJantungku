package com.example.sehatjantungku.ui.screens.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    navController: NavController,
    articleId: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Featured Image
            Image(
                painter = rememberAsyncImagePainter("https://via.placeholder.com/400x250"),
                contentDescription = "Article Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(20.dp)) {
                // Title
                Text(
                    text = "Cara Menjaga Kesehatan Jantung di Usia Muda",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Author and Date
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dr. Ahmad Rahman",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "• 15 Feb 2025",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "• 5 min read",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Content
                Text(
                    text = "Kesehatan jantung adalah aspek penting yang sering diabaikan oleh generasi muda. Padahal, menjaga kesehatan jantung sejak dini dapat mencegah berbagai penyakit kardiovaskular di masa depan.",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "1. Pola Makan Sehat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Konsumsi makanan yang kaya akan serat, seperti buah-buahan, sayuran, dan biji-bijian. Kurangi asupan garam, gula, dan lemak jenuh.",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "2. Olahraga Teratur",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Lakukan aktivitas fisik minimal 30 menit setiap hari. Olahraga kardio seperti berjalan, berlari, atau bersepeda sangat baik untuk jantung.",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.6f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tags
                Row {
                    AssistChip(
                        onClick = { },
                        label = { Text("Kesehatan") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFFFFEEF7),
                            labelColor = Color(0xFFFF6FB1)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AssistChip(
                        onClick = { },
                        label = { Text("Jantung") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFFFFEEF7),
                            labelColor = Color(0xFFFF6FB1)
                        )
                    )
                }
            }
        }
    }
}
