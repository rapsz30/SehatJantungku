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
    // Referensi ke path "articles" di Firebase Realtime Database
    private val database = FirebaseDatabase.getInstance().getReference("articles")

    // State untuk menyimpan daftar artikel yang akan diamati oleh UI
    private val _articles = mutableStateOf<List<Article>>(emptyList())
    val articles: State<List<Article>> = _articles

    // State untuk memantau proses loading
    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        fetchArticlesFromFirebase()
    }

    private fun fetchArticlesFromFirebase() {
        // Mendengarkan perubahan data secara real-time
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Article>()
                for (data in snapshot.children) {
                    // Mengonversi data snapshot menjadi objek Article
                    val article = data.getValue(Article::class.java)
                    if (article != null) {
                        list.add(article)
                    }
                }
                // Update state dengan data terbaru dari Firebase
                _articles.value = list
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                // Berhenti loading jika terjadi error
                _isLoading.value = false
            }
        })
    }
}