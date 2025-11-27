package com.example.sehatjantungku.ui.screens.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Article(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)

class HomeViewModel : ViewModel() {
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()
    
    init {
        loadArticles()
    }
    
    private fun loadArticles() {
        _articles.value = listOf(
            Article(
                id = 1,
                title = "Tips Menjaga Kesehatan Jantung",
                description = "Pelajari cara-cara sederhana untuk menjaga kesehatan jantung Anda setiap hari",
                imageUrl = ""
            ),
            Article(
                id = 2,
                title = "Makanan Sehat untuk Jantung",
                description = "Daftar makanan yang baik untuk kesehatan jantung dan pembuluh darah",
                imageUrl = ""
            ),
            Article(
                id = 3,
                title = "Olahraga Ringan untuk Jantung",
                description = "Jenis-jenis olahraga yang aman dan bermanfaat untuk kesehatan jantung",
                imageUrl = ""
            ),
            Article(
                id = 4,
                title = "Mengenali Gejala Penyakit Jantung",
                description = "Waspadai tanda-tanda awal penyakit jantung yang sering diabaikan",
                imageUrl = ""
            )
        )
    }
}
