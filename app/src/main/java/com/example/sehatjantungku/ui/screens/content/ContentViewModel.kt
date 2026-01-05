package com.example.sehatjantungku.ui.screens.content

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.sehatjantungku.data.model.Article
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ContentViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance("https://sehatjantungku-d8e98-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference("articles")

    private val _articles = mutableStateOf<List<Article>>(emptyList())
    val articles: State<List<Article>> = _articles

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        fetchArticlesFromFirebase()
    }

    private fun fetchArticlesFromFirebase() {
        _isLoading.value = true
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Article>()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        try {
                            val article = Article(
                                id = data.child("id").getValue(String::class.java) ?: "",
                                title = data.child("title").getValue(String::class.java) ?: "",
                                author = data.child("author").getValue(String::class.java) ?: "",
                                date = data.child("date").getValue(String::class.java) ?: "",
                                readTime = data.child("readTime").getValue(String::class.java) ?: "",
                                imageUrl = data.child("imageUrl").getValue(String::class.java) ?: "",
                                description = data.child("description").getValue(String::class.java) ?: "",
                                content = data.child("content").getValue(String::class.java) ?: ""
                            )
                            list.add(article)
                        } catch (e: Exception) {
                            Log.e("FirebaseData", "Gagal memproses item ${data.key}: ${e.message}")
                        }
                    }
                }
                _articles.value = list
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseData", "Database Error: ${error.message}")
                _isLoading.value = false
            }
        })
    }
}