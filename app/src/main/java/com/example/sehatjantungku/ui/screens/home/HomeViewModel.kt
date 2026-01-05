package com.example.sehatjantungku.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.sehatjantungku.data.model.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val firestore = FirebaseFirestore.getInstance()

    private val realtimeDb = FirebaseDatabase.getInstance("https://sehatjantungku-d8e98-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference("articles")

    private val _userName = MutableStateFlow("Sobat Sehat")
    val userName: StateFlow<String> = _userName

    private val _topArticles = MutableStateFlow<List<Article>>(emptyList())
    val topArticles: StateFlow<List<Article>> = _topArticles

    init {
        fetchUserName()
        fetchTopArticles()
    }

    private fun fetchUserName() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: currentUser.displayName ?: "Sobat Sehat"
                        _userName.value = name
                    }
                }
                .addOnFailureListener {
                    Log.e("HomeViewModel", "Gagal ambil nama user: ${it.message}")
                }
        }
    }

    private fun fetchTopArticles() {
        val targetIds = listOf("1", "2", "3", "4")

        realtimeDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Article>()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        try {
                            val id = data.child("id").getValue(String::class.java) ?: ""

                            if (id in targetIds) {
                                val article = Article(
                                    id = id,
                                    title = data.child("title").getValue(String::class.java) ?: "",
                                    author = data.child("author").getValue(String::class.java) ?: "",
                                    date = data.child("date").getValue(String::class.java) ?: "",
                                    readTime = data.child("readTime").getValue(String::class.java) ?: "",
                                    imageUrl = data.child("imageUrl").getValue(String::class.java) ?: "",
                                    description = data.child("description").getValue(String::class.java) ?: "",
                                    content = data.child("content").getValue(String::class.java) ?: ""
                                )
                                list.add(article)
                            }
                        } catch (e: Exception) {
                            Log.e("HomeViewModel", "Gagal parse artikel: ${e.message}")
                        }
                    }
                }
                _topArticles.value = list.sortedBy { it.id }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeViewModel", "Database Error: ${error.message}")
            }
        })
    }
}