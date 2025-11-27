package com.example.sehatjantungku.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sehatjantungku.ui.components.BottomNavBar
import com.example.sehatjantungku.ui.theme.PinkLight
import com.example.sehatjantungku.ui.theme.PinkMain
import com.example.sehatjantungku.ui.theme.PurpleLight

@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = "settings"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
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
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Nama User",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Settings Menu
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                SettingsMenuItem(
                    icon = Icons.Default.AccountCircle,
                    title = "Akun Tersimpan",
                    subtitle = "Kelola informasi akun Anda",
                    onClick = { }
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsMenuItem(
                    icon = Icons.Default.Language,
                    title = "Bahasa",
                    subtitle = "Indonesia",
                    onClick = { }
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsMenuItem(
                    icon = Icons.Default.Help,
                    title = "Pusat Bantuan",
                    subtitle = "FAQ dan dukungan",
                    onClick = { }
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                // Logout Button
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PinkMain,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Arrow",
                tint = Color.Gray
            )
        }
    }
}
