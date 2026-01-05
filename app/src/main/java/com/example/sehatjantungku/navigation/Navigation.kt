package com.example.sehatjantungku.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

//  Import Screen Auth 
import com.example.sehatjantungku.ui.screens.auth.LoginScreen
import com.example.sehatjantungku.ui.screens.auth.RegisterScreen
import com.example.sehatjantungku.ui.screens.auth.ForgotPasswordScreen

//  Import Screen Utama 
import com.example.sehatjantungku.ui.screens.home.HomeScreen
import com.example.sehatjantungku.ui.screens.content.ContentScreen
import com.example.sehatjantungku.ui.screens.settings.SettingsScreen
import com.example.sehatjantungku.ui.screens.profile.ProfileScreen
import com.example.sehatjantungku.ui.screens.notifications.NotificationsScreen

//  Import Fitur 
import com.example.sehatjantungku.ui.screens.chatbot.ChatbotScreen
import com.example.sehatjantungku.ui.screens.cvdrisk.CVDRiskScreen
import com.example.sehatjantungku.ui.screens.cvdrisk.CVDResultScreen

//  Import Fitur Diet & ViewModel 
import com.example.sehatjantungku.ui.screens.diet.DietProgramScreen
import com.example.sehatjantungku.ui.screens.diet.DietResultScreen
import com.example.sehatjantungku.ui.screens.diet.DietStartScreen
import com.example.sehatjantungku.ui.screens.diet.DietCompletionScreen
import com.example.sehatjantungku.ui.screens.diet.DietProgramViewModel // Import ViewModel Diet
import com.example.sehatjantungku.ui.screens.content.ArticleDetailScreen

//  Import Settings Sub-Menu 
import com.example.sehatjantungku.ui.screens.settings.AccountSettingScreen
import com.example.sehatjantungku.ui.screens.settings.PasswordChangeScreen
import com.example.sehatjantungku.ui.screens.settings.LanguageScreen
import com.example.sehatjantungku.ui.screens.settings.HelpCenterScreen
import com.example.sehatjantungku.ui.screens.settings.EditProfileScreen // PERBAIKAN: Import EditProfileScreen

@Composable
fun SehatJantungkuNavigation(
    startDestination: String = "login"
) {
    val navController = rememberNavController()
    // Inisialisasi ViewModel
    val dietViewModel: DietProgramViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        //  AUTH ROUTES 
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }

        //  MAIN ROUTES 
        composable("home") { HomeScreen(navController) }
        composable("content") { ContentScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("notifications") { NotificationsScreen(navController) }

        //  FITUR: CHATBOT 
        composable("chatbot") { ChatbotScreen(navController) }

        //  FITUR: CVD RISK 
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

        //  FITUR: DIET PROGRAM 

        // 1. Screen Form Personalisasi
        composable("diet_program") {
            // Kita kirim dietViewModel yang sama
            DietProgramScreen(navController, dietViewModel)
        }

        // 2. Screen Hasil Rekomendasi
        composable(
            route = "diet_result/{dietId}",
            arguments = listOf(
                navArgument("dietId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dietId = backStackEntry.arguments?.getString("dietId") ?: "1"
            // Kirim viewModel agar bisa akses data input user untuk Gemini
            DietResultScreen(navController, dietId, dietViewModel)
        }

        // 3. Screen Mulai Diet (Tracker)
        composable(
            route = "diet_start/{dietId}",
            arguments = listOf(navArgument("dietId") { type = NavType.StringType })
        ) { backStackEntry ->
            val dietId = backStackEntry.arguments?.getString("dietId") ?: "1"
            DietStartScreen(navController, dietId, dietViewModel)
        }

        composable("diet_completion") {
            DietCompletionScreen(navController)
        }

        //  ARTIKEL DETAIL 
        composable(
            route = "article_detail/{articleId}",
            arguments = listOf(navArgument("articleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
            ArticleDetailScreen(navController, articleId)
        }

        //  SETTINGS SUB-MENUS 
        composable("settings/account") { AccountSettingScreen(navController) }
        composable("settings/password") { PasswordChangeScreen(navController) }
        composable("settings/language") { LanguageScreen(navController) }
        composable("settings/help") { HelpCenterScreen(navController) }

        // PERBAIKAN: Menambahkan rute edit_profile
        composable("edit_profile") { EditProfileScreen(navController) }
    }
}