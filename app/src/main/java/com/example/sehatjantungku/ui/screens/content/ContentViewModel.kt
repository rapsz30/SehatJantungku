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
    // PENTING: Menggunakan URL spesifik region sesuai pesan log error Anda
    private val database = FirebaseDatabase
        .getInstance("https://sehatjantungku-d8e98-default-rtdb.asia-southeast1.firebasedatabase.app")
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
                    Log.d("FirebaseData", "Koneksi Berhasil! Ditemukan ${snapshot.childrenCount} artikel")
                    for (data in snapshot.children) {
                        try {
                            // Menggunakan mapping manual agar lebih aman terhadap perbedaan tipe data
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
                            Log.e("FirebaseData", "Gagal mapping item ${data.key}: ${e.message}")
                        }
                    }
                } else {
                    Log.e("FirebaseData", "Snapshot tidak ditemukan di path 'articles'")
                }

                _articles.value = list
                _isLoading.value = false // Berhenti loading setelah data diproses
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseData", "Koneksi gagal: ${error.message}")
                _isLoading.value = false
            }
        })
    }
}