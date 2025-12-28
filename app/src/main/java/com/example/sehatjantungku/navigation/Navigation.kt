package com.example.sehatjantungku.navigation

import androidx.compose.runtime.Composable
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
import com.example.sehatjantungku.ui.screens.diet.DietProgramScreen
import com.example.sehatjantungku.ui.screens.diet.DietResultScreen
import com.example.sehatjantungku.ui.screens.diet.DietStartScreen
import com.example.sehatjantungku.ui.screens.diet.DietCompletionScreen
import com.example.sehatjantungku.ui.screens.content.ArticleDetailScreen

// --- Import Settings Sub-Menu ---
// Perbaikan: Menggunakan nama Singular (AccountSettingScreen)
import com.example.sehatjantungku.ui.screens.settings.AccountSettingScreen
import com.example.sehatjantungku.ui.screens.settings.PasswordChangeScreen
import com.example.sehatjantungku.ui.screens.settings.LanguageScreen
import com.example.sehatjantungku.ui.screens.settings.HelpCenterScreen

@Composable
fun SehatJantungkuNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // --- AUTH ROUTES ---
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }

        // --- MAIN ROUTES ---
        composable("home") {
            HomeScreen(navController)
        }
        composable("content") {
            ContentScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }
        composable("notifications") {
            NotificationsScreen(navController)
        }

        // --- FITUR: CHATBOT ---
        composable("chatbot") {
            ChatbotScreen(navController)
        }

        // --- FITUR: CVD RISK ---
        composable("cvd_risk") {
            CVDRiskScreen(navController)
        }
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

        // --- FITUR: DIET PROGRAM ---
        composable("diet_program") {
            DietProgramScreen(navController)
        }
        composable(
            route = "diet_result/{bestDiet}/{scores}",
            arguments = listOf(
                navArgument("bestDiet") { type = NavType.StringType },
                navArgument("scores") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bestDiet = backStackEntry.arguments?.getString("bestDiet") ?: ""
            val scoresString = backStackEntry.arguments?.getString("scores") ?: "0,0,0,0,0"
            // Konversi string "1,2,3" menjadi List<Int>
            val scores = scoresString.split(",").map { it.toIntOrNull() ?: 0 }
            DietResultScreen(navController, bestDiet, scores)
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
        composable("settings/account") {
            // Menggunakan nama fungsi yang benar (Singular)
            AccountSettingScreen(navController)
        }

        // Route "settings/email" SUDAH DIHAPUS

        composable("settings/password") {
            PasswordChangeScreen(navController)
        }
        composable("settings/language") {
            LanguageScreen(navController)
        }
        composable("settings/help") {
            HelpCenterScreen(navController)
        }
    }
}