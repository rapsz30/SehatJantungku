package com.example.sehatjantungku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sehatjantungku.navigation.SehatJantungkuNavigation
import com.example.sehatjantungku.ui.theme.SehatJantungkuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SehatJantungkuTheme {
                SehatJantungkuNavigation()
            }
        }
    }
}
