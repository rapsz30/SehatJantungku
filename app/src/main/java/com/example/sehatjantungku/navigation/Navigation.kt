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
import com.example.sehatjantungku.ui.screens.cvdrisk.CVDRiskResultScreen
import com.example.sehatjantungku.ui.screens.diet.DietProgramScreen
import com.example.sehatjantungku.ui.screens.diet.DietResultScreen
import com.example.sehatjantungku.ui.screens.diet.DietStartScreen
import com.example.sehatjantungku.ui.screens.diet.DietCompletionScreen
import com.example.sehatjantungku.ui.screens.content.ArticleDetailScreen
import com.example.sehatjantungku.ui.screens.content.VideoDetailScreen
import com.example.sehatjantungku.ui.screens.settings.AccountSettingsScreen
import com.example.sehatjantungku.ui.screens.settings.EmailChangeScreen
import com.example.sehatjantungku.ui.screens.settings.PasswordChangeScreen
import com.example.sehatjantungku.ui.screens.settings.LanguageScreen
import com.example.sehatjantungku.ui.screens.settings.HelpCenterScreen
import com.example.sehatjantungku.ui.screens.chatbot.ChatbotScreen

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
            route = "cvd_risk_result/{heartAge}/{riskScore}", // Fixed parameter order to match function signature
            arguments = listOf(
                navArgument("heartAge") { type = NavType.IntType },
                navArgument("riskScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val heartAge = backStackEntry.arguments?.getInt("heartAge") ?: 0
            val riskScore = backStackEntry.arguments?.getInt("riskScore") ?: 0
            CVDRiskResultScreen(navController, heartAge, riskScore)
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
            route = "article/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ArticleDetailScreen(navController, id)
        }
        composable(
            route = "video/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            VideoDetailScreen(navController, id)
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
