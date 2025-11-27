package com.example.sehatjantungku.ui.screens.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Content",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Artikel & Video Edukasi Kesehatan",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Cari artikel atau video...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkMain,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tab Switcher
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PinkMain,
                modifier = Modifier.padding(horizontal = 16.dp)
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
            
            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (selectedTab == 0) {
                    items(4) { index ->
                        ArticleCard(index)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    items(4) { index ->
                        VideoCard(index)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCard(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    text = "Deskripsi singkat tentang artikel kesehatan jantung ini",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun VideoCard(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            }
        }
    }
}
