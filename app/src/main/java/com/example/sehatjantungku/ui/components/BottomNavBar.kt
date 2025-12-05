package com.example.sehatjantungku.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.sehatjantungku.ui.theme.PinkMain

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == "home") Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PinkMain,
                selectedTextColor = PinkMain,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = PinkMain.copy(alpha = 0.1f)
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == "content") Icons.Filled.Article else Icons.Outlined.Article,
                    contentDescription = "Content"
                )
            },
            label = { Text("Content") },
            selected = currentRoute == "content",
            onClick = { navController.navigate("content") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PinkMain,
                selectedTextColor = PinkMain,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = PinkMain.copy(alpha = 0.1f)
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == "settings") Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings") },
            selected = currentRoute == "settings",
            onClick = { navController.navigate("settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PinkMain,
                selectedTextColor = PinkMain,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = PinkMain.copy(alpha = 0.1f)
            )
        )
    }
}
