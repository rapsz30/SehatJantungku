package com.example.sehatjantungku.ui.screens.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sehatjantungku.data.model.Article
import com.example.sehatjantungku.ui.theme.PinkMain
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    navController: NavController,
    articleId: String
) {
    var article by remember { mutableStateOf<Article?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(articleId) {
        val db = FirebaseDatabase.getInstance("https://sehatjantungku-d8e98-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("articles").child(articleId)

        db.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                article = Article(
                    id = snapshot.child("id").getValue(String::class.java) ?: "",
                    title = snapshot.child("title").getValue(String::class.java) ?: "",
                    author = snapshot.child("author").getValue(String::class.java) ?: "",
                    date = snapshot.child("date").getValue(String::class.java) ?: "",
                    readTime = snapshot.child("readTime").getValue(String::class.java) ?: "",
                    imageUrl = snapshot.child("imageUrl").getValue(String::class.java) ?: "",
                    description = snapshot.child("description").getValue(String::class.java) ?: "",
                    content = snapshot.child("content").getValue(String::class.java) ?: ""
                )
            }
            isLoading = false
        }.addOnFailureListener {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Artikel", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PinkMain)
            }
        } else if (article != null) {
            val item = article!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // GAMBAR HEADER
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp), // Tinggi gambar pas
                    contentScale = ContentScale.Crop
                )

                // KONTEN TEKS
                Column(modifier = Modifier.padding(24.dp)) {
                    Surface(
                        color = PinkMain.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Kesehatan Jantung",
                            color = PinkMain,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Judul
                    Text(
                        text = item.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info Penulis & Tanggal
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MetaInfo(Icons.Default.Person, item.author)
                        Spacer(modifier = Modifier.width(16.dp))
                        MetaInfo(Icons.Default.AccessTime, item.date)
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = 20.dp),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    // Isi Artikel
                    Text(
                        text = item.content.replace("\\n", "\n"),
                        fontSize = 15.sp, // Ukuran font nyaman
                        lineHeight = 24.sp, // Jarak antar baris lega
                        color = Color(0xFF333333),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Artikel tidak ditemukan", color = Color.Gray)
            }
        }
    }
}

// Komponen Kecil
@Composable
fun MetaInfo(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}