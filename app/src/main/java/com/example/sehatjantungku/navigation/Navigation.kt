package com.sehatjantungku.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sehatjantungku.ui.screens.content.ContentScreen
import com.example.sehatjantungku.ui.screens.cvdrisk.CVDRiskScreen
import com.example.sehatjantungku.ui.screens.cvdrisk.CVDResultScreen
import com.example.sehatjantungku.ui.screens.diet.DietProgramScreen
import com.example.sehatjantungku.ui.screens.diet.DietResultScreen
import com.example.sehatjantungku.ui.screens.diet.DietStartScreen
import com.example.sehatjantungku.ui.screens.home.HomeScreen
import com.example.sehatjantungku.ui.screens.notifications.NotificationsScreen
import com.example.sehatjantungku.ui.screens.profile.ProfileScreen
import com.example.sehatjantungku.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Content : Screen("content")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")
    object CVDRisk : Screen("cvd_risk")
    object CVDResult : Screen("cvd_result/{heartAge}/{riskScore}")
    object DietProgram : Screen("diet_program")
    object DietResult : Screen("diet_result/{dietType}")
    object DietStart : Screen("diet_start/{dietType}")
}

@Composable
fun SehatJantungkuNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Content.route) {
            ContentScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }

        composable(Screen.CVDRisk.route) {
            CVDRiskScreen(navController = navController)
        }

        composable(
            route = Screen.CVDResult.route,
            arguments = listOf(
                navArgument("heartAge") { type = NavType.IntType },
                navArgument("riskScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val heartAge = backStackEntry.arguments?.getInt("heartAge") ?: 0
            val riskScore = backStackEntry.arguments?.getInt("riskScore") ?: 0
            CVDResultScreen(
                navController = navController,
                heartAge = heartAge,
                riskScore = riskScore
            )
        }

        composable(Screen.DietProgram.route) {
            DietProgramScreen(navController = navController)
        }

        composable(
            route = Screen.DietResult.route,
            arguments = listOf(navArgument("dietType") { type = NavType.StringType })
        ) { backStackEntry ->
            val dietType = backStackEntry.arguments?.getString("dietType") ?: "Plant-Based"
            DietResultScreen(
                navController = navController,
                dietType = dietType
            )
        }

        composable(
            route = Screen.DietStart.route,
            arguments = listOf(navArgument("dietType") { type = NavType.StringType })
        ) { backStackEntry ->
            val dietType = backStackEntry.arguments?.getString("dietType") ?: "Plant-Based"
            DietStartScreen(
                navController = navController,
                dietType = dietType
            )
        }
    }
}
