package com.example.sehatjantungku.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel // [PENTING] Tambahkan import ini
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// --- Import Screen Auth ---
import com.example.sehatjantungku.ui.screens.auth.LoginScreen
import com.example.sehatjantungku.ui.screens.auth.RegisterScreen
import com.example.sehatjantungku.ui.screens.auth.ForgotPasswordScreen

// --- Import Screen Utama ---
import com.example.sehatjantungku.ui.screens.home.HomeScreen
import com.example.sehatjantungku.ui.screens.content.ContentScreen
import com.example.sehatjantungku.ui.screens.settings.SettingsScreen
import com.example.sehatjantungku.ui.screens.profile.ProfileScreen
import com.example.sehatjantungku.ui.screens.notifications.NotificationsScreen

// --- Import Fitur ---
import com.example.sehatjantungku.ui.screens.chatbot.ChatbotScreen
import com.example.sehatjantungku.ui.screens.cvdrisk.CVDRiskScreen
import com.example.sehatjantungku.ui.screens.cvdrisk.CVDResultScreen

// --- Import Fitur Diet & ViewModel ---
import com.example.sehatjantungku.ui.screens.diet.DietProgramScreen
import com.example.sehatjantungku.ui.screens.diet.DietResultScreen
import com.example.sehatjantungku.ui.screens.diet.DietStartScreen
import com.example.sehatjantungku.ui.screens.diet.DietCompletionScreen
import com.example.sehatjantungku.ui.screens.diet.DietProgramViewModel // [PENTING] Import ViewModel
import com.example.sehatjantungku.ui.screens.content.ArticleDetailScreen

// --- Import Settings Sub-Menu ---
import com.example.sehatjantungku.ui.screens.settings.AccountSettingScreen
import com.example.sehatjantungku.ui.screens.settings.PasswordChangeScreen
import com.example.sehatjantungku.ui.screens.settings.LanguageScreen
import com.example.sehatjantungku.ui.screens.settings.HelpCenterScreen

@Composable
fun SehatJantungkuNavigation(
    startDestination: String = "login"
) {
    val navController = rememberNavController()

    // [PENTING] Inisialisasi ViewModel di sini agar bisa dishare
    // Ini memastikan Data Input di Form tidak hilang saat pindah ke Result
    val dietViewModel: DietProgramViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // --- AUTH ROUTES ---
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }

        // --- MAIN ROUTES ---
        composable("home") { HomeScreen(navController) }
        composable("content") { ContentScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("notifications") { NotificationsScreen(navController) }

        // --- FITUR: CHATBOT ---
        composable("chatbot") { ChatbotScreen(navController) }

        // --- FITUR: CVD RISK ---
        composable("cvd_risk") { CVDRiskScreen(navController) }
        composable(
            route = "cvd_risk_result/{riskScoresString}/{heartAge}",
            arguments = listOf(
                navArgument("riskScoresString") { type = NavType.StringType },
                navArgument("heartAge") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val riskScoresString = backStackEntry.arguments?.getString("riskScoresString") ?: "0.0,0.0,0.0"
            val heartAge = backStackEntry.arguments?.getInt("heartAge") ?: 0
            CVDResultScreen(navController, heartAge, riskScoresString)
        }

        // --- FITUR: DIET PROGRAM (UPDATED) ---
        composable("diet_program") {
            // Kita kirim dietViewModel yang sudah dibuat di atas
            DietProgramScreen(navController, dietViewModel)
        }

        // Route Result yang Baru (Sesuai kode sebelumnya)
        composable(
            route = "diet_result/{dietId}",
            arguments = listOf(
                navArgument("dietId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dietId = backStackEntry.arguments?.getString("dietId") ?: "1"

            // Di sini kita kirim ViewModel yang SAMA, sehingga data input user masih ada
            // untuk dibaca oleh Gemini AI
            DietResultScreen(navController, dietId, dietViewModel)
        }

        composable(
            route = "diet_start/{dietType}",
            arguments = listOf(navArgument("dietType") { type = NavType.StringType })
        ) { backStackEntry ->
            val dietType = backStackEntry.arguments?.getString("dietType") ?: "Plant-Based"
            DietStartScreen(navController, dietType)
        }
        composable("diet_completion") {
            DietCompletionScreen(navController)
        }

        // --- ARTIKEL DETAIL ---
        composable(
            route = "article_detail/{articleId}",
            arguments = listOf(navArgument("articleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
            ArticleDetailScreen(navController, articleId)
        }

        // --- SETTINGS SUB-MENUS ---
        composable("settings/account") { AccountSettingScreen(navController) }
        composable("settings/password") { PasswordChangeScreen(navController) }
        composable("settings/language") { LanguageScreen(navController) }
        composable("settings/help") { HelpCenterScreen(navController) }
    }
}