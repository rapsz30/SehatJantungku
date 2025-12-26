package com.example.sehatjantungku.ui.screens.content

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.sehatjantungku.data.model.Article
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ContentViewModel : ViewModel() {
    // Pastikan path ini sesuai dengan folder tempat Anda import JSON tadi
    private val database = FirebaseDatabase.getInstance().getReference("articles")

    private val _articles = mutableStateOf<List<Article>>(emptyList())
    val articles: State<List<Article>> = _articles

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        fetchArticlesFromFirebase()
    }

    private fun fetchArticlesFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Article>()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val article = data.getValue(Article::class.java)
                        article?.let { list.add(it) }
                    }
                }
                _articles.value = list
                // PENTING: Menghentikan loading setelah data didapat (meskipun kosong)
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                // Berhenti loading jika koneksi dibatalkan/error
                _isLoading.value = false
            }
        })
    }
}