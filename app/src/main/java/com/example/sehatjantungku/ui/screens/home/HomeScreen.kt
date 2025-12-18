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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sehatjantungku.ui.components.BottomNavBar
import com.example.sehatjantungku.ui.theme.PinkLight
import com.example.sehatjantungku.ui.theme.PinkMain
import com.example.sehatjantungku.ui.theme.PurpleLight

// Update: Menambahkan ID untuk navigasi
data class Article(
    val id: String,
    val title: String,
    val description: String
)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    // Update: Daftar artikel dengan ID unik
    val articles = listOf(
        Article(
            "1",
            "Tips Menjaga Kesehatan Jantung",
            "Pelajari cara-cara sederhana untuk menjaga kesehatan jantung Anda setiap hari"
        ),
        Article(
            "2",
            "Makanan Sehat untuk Jantung",
            "Daftar makanan yang baik untuk kesehatan jantung dan pembuluh darah"
        ),
        Article(
            "3",
            "Olahraga Ringan untuk Jantung",
            "Jenis-jenis olahraga yang aman dan bermanfaat untuk kesehatan jantung"
        ),
        Article(
            "4",
            "Mengenali Gejala Penyakit Jantung",
            "Waspadai tanda-tanda awal penyakit jantung yang sering diabaikan"
        )
    )

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
            // User Welcome Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PinkLight)
                                .clickable { navController.navigate("profile") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Selamat datang kembali",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Nama User",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = PinkMain
                        )
                    }
                }
            }

            // Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(PinkMain, PurpleLight)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Heart",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Welcome to SehatJantungku",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Because every heartbeat matters",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Feature Menu
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    FeatureItem(
                        icon = Icons.Default.Favorite,
                        label = "CVD Risk Predictor",
                        onClick = { navController.navigate("cvd_risk") },
                        modifier = Modifier.weight(1f)
                    )
                    FeatureItem(
                        icon = Icons.Default.Restaurant,
                        label = "Personalized Diet Program",
                        onClick = { navController.navigate("diet_program") },
                        modifier = Modifier.weight(1f)
                    )
                    FeatureItem(
                        icon = Icons.Default.Chat,
                        label = "Chatbot",
                        onClick = { navController.navigate("chatbot") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Top Article Section
            item {
                Text(
                    text = "Top Article",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Update: Menghubungkan klik artikel ke rute detail
            items(articles) { article ->
                ArticleItem(
                    article = article,
                    onClick = {
                        navController.navigate("article/${article.id}")
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun FeatureItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PinkMain, PinkLight)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun ArticleItem(
    article: Article,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp, 90.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = article.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
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