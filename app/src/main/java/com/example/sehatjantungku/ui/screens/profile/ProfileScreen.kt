package com.example.sehatjantungku.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sehatjantungku.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val pinkMain = Color(0xFFFF6FB1)
    val purpleLight = Color(0xFFCC7CF0)

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    val user = viewModel.userData

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = pinkMain)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F8F8)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(colors = listOf(pinkMain, purpleLight)),
                            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                        .padding(bottom = 40.dp, top = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = pinkMain)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = user?.name ?: "Loading...", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = user?.email ?: "", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).offset(y = (-20).dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "Informasi Pribadi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileItem(Icons.Default.Person, "Nama Lengkap", user?.name ?: "-")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        ProfileItem(Icons.Default.Email, "Email", user?.email ?: "-")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        ProfileItem(Icons.Default.Phone, "Nomor Telepon", user?.phone ?: "-")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        ProfileItem(Icons.Default.Cake, "Tanggal Lahir", user?.birthDate ?: "-")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFFFF6FB1), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}