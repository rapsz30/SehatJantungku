package com.example.sehatjantungku.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sehatjantungku.ui.screens.auth.LoginScreen
import com.example.sehatjantungku.ui.screens.auth.RegisterScreen
import com.example.sehatjantungku.ui.screens.auth.ForgotPasswordScreen
import com.example.sehatjantungku.ui.screens.home.HomeScreen
import com.example.sehatjantungku.ui.screens.content.ContentScreen
import com.example.sehatjantungku.ui.screens.settings.SettingsScreen
import com.example.sehatjantungku.ui.screens.profile.ProfileScreen
import com.example.sehatjantungku.ui.screens.notifications.NotificationsScreen
import com.example.sehatjantungku.ui.screens.cvdrisk.CVDRiskScreen
import com.example.sehatjantungku.ui.screens.diet.DietProgramScreen
import com.example.sehatjantungku.ui.screens.diet.DietResultScreen
import com.example.sehatjantungku.ui.screens.diet.DietStartScreen
import com.example.sehatjantungku.ui.screens.diet.DietCompletionScreen
import com.example.sehatjantungku.ui.screens.content.ArticleDetailScreen
import com.example.sehatjantungku.ui.screens.settings.AccountSettingsScreen
import com.example.sehatjantungku.ui.screens.settings.EmailChangeScreen
import com.example.sehatjantungku.ui.screens.settings.PasswordChangeScreen
import com.example.sehatjantungku.ui.screens.settings.LanguageScreen
import com.example.sehatjantungku.ui.screens.settings.HelpCenterScreen
import com.example.sehatjantungku.ui.screens.chatbot.ChatbotScreen
import com.example.sehatjantungku.ui.screens.cvdrisk.CVDResultScreen

@Composable
fun SehatJantungkuNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login" // Changed start destination to login
    ) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }

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

        composable("chatbot") {
            ChatbotScreen(navController)
        }

        composable("cvd_risk") {
            CVDRiskScreen(navController)
        }
        composable(
            // Nama parameter di URL disesuaikan dengan isi yang dikirim (riskScoresString)
            route = "cvd_risk_result/{riskScoresString}/{heartAge}",
            arguments = listOf(
                // PERBAIKAN: Mengubah tipe dari IntType menjadi StringType
                navArgument("riskScoresString") { type = NavType.StringType },
                // HeartAge tetap IntType
                navArgument("heartAge") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            // Mengambil argumen pertama sebagai String
            val riskScoresString = backStackEntry.arguments?.getString("riskScoresString") ?: "0.0,0.0,0.0"
            // Mengambil argumen kedua sebagai Int
            val heartAge = backStackEntry.arguments?.getInt("heartAge") ?: 0

            // Perhatikan urutan parameter: CVDResultScreen(navController, heartAge, riskScoresString)
            // CVDResultScreen di file Anda menerima parameter dalam urutan (heartAge: Int, riskScoresString: String)
            CVDResultScreen(navController, heartAge, riskScoresString)
        }

        composable("diet_program") {
            DietProgramScreen(navController)
        }
        composable(
            route = "diet_result/{bestDiet}/{scores}", // Added missing parameters bestDiet and scores
            arguments = listOf(
                navArgument("bestDiet") { type = NavType.StringType },
                navArgument("scores") { type = NavType.StringType } // Pass as comma-separated string
            )
        ) { backStackEntry ->
            val bestDiet = backStackEntry.arguments?.getString("bestDiet") ?: ""
            val scoresString = backStackEntry.arguments?.getString("scores") ?: "0,0,0,0,0"
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

        composable(
            route = "article_detail/{articleId}", // Nama argumen harus konsisten
            arguments = listOf(navArgument("articleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
            ArticleDetailScreen(navController, articleId)
        }
        composable("settings/account") {
            AccountSettingsScreen(navController)
        }
        composable("settings/email") {
            EmailChangeScreen(navController)
        }
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
