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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    // Collect Data dari ViewModel
    val userName by viewModel.userName.collectAsState()
    val topArticles by viewModel.topArticles.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = "home"
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // --- BAGIAN USER NAME ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PinkLight)
                                .clickable { navController.navigate("profile") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, "Profile", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Selamat datang kembali", fontSize = 12.sp, color = Color.Gray)
                            // TEXT DINAMIS DARI DATABASE
                            Text(
                                text = userName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(Icons.Default.Notifications, "Notifications", tint = PinkMain)
                    }
                }
            }

            // Hero Banner (Tetap sama)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(PinkMain, PurpleLight))),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FavoriteBorder, "Heart", tint = Color.White, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Welcome to SehatJantungku", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Because every heartbeat matters", color = Color.White.copy(0.9f), fontSize = 14.sp)
                    }
                }
            }

            // Feature Menu (Tetap sama)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp, 24.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    FeatureItem(Icons.Default.Favorite, "CVD Risk Predictor", { navController.navigate("cvd_risk") }, Modifier.weight(1f))
                    FeatureItem(Icons.Default.Restaurant, "Diet Program", { navController.navigate("diet_program") }, Modifier.weight(1f))
                    FeatureItem(Icons.Default.Chat, "Chatbot", { navController.navigate("chatbot") }, Modifier.weight(1f))
                }
            }

            // Top Article Header
            item {
                Text(
                    text = "Top Article",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // --- BAGIAN LIST ARTIKEL ---
            items(topArticles) { article ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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

// Komponen FeatureItem (Tetap sama)
@Composable
fun FeatureItem(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.clickable(onClick = onClick)) {
        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Brush.radialGradient(listOf(PinkMain, PinkLight))), contentAlignment = Alignment.Center) {
            Icon(icon, label, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = Color.Black, textAlign = TextAlign.Center, lineHeight = 14.sp)
    }
}

// --- ITEM ARTIKEL YANG DIPERBARUI (Bentuk Card seperti ContentScreen) ---
@Composable
fun ArticleItem(article: Article, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(100.dp)
        ) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = article.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
            }
        }
    }
}