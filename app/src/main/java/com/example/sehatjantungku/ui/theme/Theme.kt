package com.example.sehatjantungku.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- 1. DEFINISI WARNA (Harus hanya ada di satu tempat di package ini) ---
val PinkLight = Color(0xFFFF8CCF)
val PinkMain = Color(0xFFFF6FB1) // Ini adalah warna utama (Primary)
val PurpleLight = Color(0xFFCC7CF0)
val Gray = Color(0xFF555555)
val LightGray = Color(0xFFE5E5E5)
// -----------------------------------------------------------------------

private val LightColorScheme = lightColorScheme(
    // WARNA UTAMA
    primary = PinkMain,
    onPrimary = Color.White,

    // WARNA SEKUNDER (digunakan sebagai aksen)
    secondary = PurpleLight,
    onSecondary = Color.White,

    // WARNA TERSIER
    tertiary = PinkLight,
    onTertiary = Color.White,

    // WARNA BACKGROUND & SURFACE
    background = Color.White,
    surface = Color.White,

    // WARNA TEKS (onBackground & onSurface)
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun SehatJantungkuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Karena Anda tidak mendefinisikan skema warna gelap (Dark Theme),
    // kita gunakan LightColorScheme saja.
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Mengambil Typography dari file Type.kt
        content = content
    )
}