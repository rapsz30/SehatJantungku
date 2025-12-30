package com.example.sehatjantungku.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sehatjantungku.data.model.Article
import com.example.sehatjantungku.ui.components.BottomNavBar
import com.example.sehatjantungku.ui.theme.PinkLight
import com.example.sehatjantungku.ui.theme.PinkMain
import com.example.sehatjantungku.ui.theme.PurpleLight

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    // Collect Data
    val userName by viewModel.userName.collectAsState()
    val topArticles by viewModel.topArticles.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = "home"
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 20.dp),
            // [PERBAIKAN 2] Jarak diperkecil dari 20.dp menjadi 12.dp agar tidak kejauhan
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- 1. HEADER USER ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Profile Picture Placeholder
                        Surface(
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { navController.navigate("profile") },
                            shape = CircleShape,
                            color = PinkLight.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = "Profile", tint = PinkMain)
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text("Selamat Datang,", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = userName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    // [PERBAIKAN 1] Notifikasi Icon Bersih (Tanpa Background Kotak/Segi-8)
                    IconButton(
                        onClick = { navController.navigate("notifications") }
                    ) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = "Notifikasi",
                            tint = PinkMain,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // --- 2. HERO BANNER ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(160.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PinkMain, PurpleLight)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            "Heart",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "SehatJantungku",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Because every heartbeat matters",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // --- 3. MENU FITUR ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp), // Sedikit padding vertical
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FeatureItem(
                        icon = Icons.Default.MonitorHeart,
                        label = "Cek Risiko",
                        onClick = { navController.navigate("cvd_risk") }
                    )
                    FeatureItem(
                        icon = Icons.Default.RestaurantMenu,
                        label = "Diet Plan",
                        onClick = { navController.navigate("diet_program") }
                    )
                    FeatureItem(
                        icon = Icons.Default.SmartToy,
                        label = "Chatbot AI",
                        onClick = { navController.navigate("chatbot") }
                    )
                }
            }

            // --- 4. HEADER ARTIKEL ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Artikel Pilihan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Lihat Semua",
                        fontSize = 12.sp,
                        color = PinkMain,
                        modifier = Modifier.clickable { navController.navigate("content") }
                    )
                }
            }

            // --- 5. LIST ARTIKEL ---
            if (topArticles.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PinkMain, modifier = Modifier.size(30.dp))
                    }
                }
            } else {
                items(topArticles) { article ->
                    // Padding horizontal saja, vertikal diatur oleh Arrangement parent
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        ArticleItem(
                            article = article,
                            onClick = {
                                navController.navigate("article_detail/${article.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- KOMPONEN PENDUKUNG ---

@Composable
fun FeatureItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(80.dp)
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = PinkMain,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ArticleItem(article: Article, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(80.dp)
        ) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = article.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.description,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}