package com.example.sehatjantungku.ui.screens.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sehatjantungku.ui.components.BottomNavBar
import com.example.sehatjantungku.ui.theme.PinkMain

@Composable
fun ContentScreen(
    navController: NavController,
    viewModel: ContentViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = "content"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cari artikel atau video...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkMain,
                    cursorColor = PinkMain
                )
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PinkMain,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PinkMain
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Artikel") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Video") }
                )
            }

            // List Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedTab == 0) {
                    // List Artikel (Mengarah ke ArticleDetailScreen)
                    items(10) { index ->
                        ArticleCard(
                            index = index,
                            onClick = {
                                // Sesuai Navigation.kt: route = "article/{id}"
                                navController.navigate("article/${index + 1}")
                            }
                        )
                    }
                } else {
                    // List Video (Mengarah ke VideoDetailScreen)
                    items(5) { index ->
                        VideoCard(
                            index = index,
                            onClick = {
                                // Sesuai Navigation.kt: route = "video/{id}"
                                navController.navigate("video/${index + 1}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCard(index: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // Navigasi saat diklik
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp, 90.dp)
                    .background(Color.LightGray, RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Artikel Kesehatan Jantung ${index + 1}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Deskripsi singkat tentang artikel kesehatan jantung ini agar pengguna tertarik membaca.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun VideoCard(index: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // Navigasi saat diklik
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Video Edukasi ${index + 1}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Durasi: 05:20",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}