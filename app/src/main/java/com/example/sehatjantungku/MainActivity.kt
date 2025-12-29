package com.example.sehatjantungku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sehatjantungku.navigation.SehatJantungkuNavigation
import com.example.sehatjantungku.ui.theme.SehatJantungkuTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ---CEK STATUS LOGIN ---
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) "home" else "login"

        setContent {
            SehatJantungkuTheme {
                // Kirim startDestination ke navigasi
                SehatJantungkuNavigation(startDestination = startDestination)
            }
        }
    }
}