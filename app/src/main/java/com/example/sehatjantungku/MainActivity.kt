package com.example.sehatjantungku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sehatjantungku.navigation.SehatJantungkuNavigation
import com.example.sehatjantungku.ui.theme.SehatJantungkuTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    /*Kelompok:
     * Anggota: Afifuddin Mahfud         (23523076)
     *          Ahmad Khatami Rafsanjani (23523095)
     *          Azhartama Zuhal Budiazka (23523026)
     *          Mohamad Rafi Hendryansah (23523064)
     *
     * Email: miawmiaw@email.com
     * Password: miawmiaw
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) "home" else "login"

        setContent {
            SehatJantungkuTheme {
                SehatJantungkuNavigation(startDestination = startDestination)
            }
        }
    }
}