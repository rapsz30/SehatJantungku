package com.example.sehatjantungku.ui.screens.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun VideoDetailScreen(
    navController: NavController,
    videoId: String
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
            // Video Player (Placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    modifier = Modifier.size(64.dp),
                    tint = Color.White
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // Title
                Text(
                    text = "Olahraga Ringan untuk Kesehatan Jantung",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stats
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "12.5K views",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• 2 hari lalu",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton(Icons.Default.ThumbUp, "125")
                    ActionButton(Icons.Default.Share, "Share")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Divider(color = Color.LightGray)

                Spacer(modifier = Modifier.height(16.dp))

                // Author Info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF8CCF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Dr. Sarah Wijaya",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Dokter Spesialis Jantung",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Deskripsi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Video ini menjelaskan berbagai jenis olahraga ringan yang dapat dilakukan untuk menjaga kesehatan jantung. Cocok untuk pemula dan dapat dilakukan di rumah tanpa alat khusus.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight.times(1.5f),
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tags
                Row {
                    AssistChip(
                        onClick = { },
                        label = { Text("Olahraga") },
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

@Composable
private fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = { }) {
            Icon(icon, contentDescription = label)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
