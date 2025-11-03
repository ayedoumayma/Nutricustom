package com.example.nutricustomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nutricustomapp.screens.*
import com.example.nutricustomapp.ui.theme.NutricustomAppTheme
import com.example.nutricustomapp.ui.theme.PrimaryPink
import com.example.nutricustomapp.utils.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutricustomAppTheme {
                GamerAppNavigation()
            }
        }
    }
}

@Composable
fun GamerAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(navController = navController)
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }

        composable(
            route = Screen.OTPValidation.route,
            arguments = listOf(navArgument("code") { type = NavType.StringType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("code") ?: "1234"
            OTPValidationScreen(navController = navController, expectedCode = code)
        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(navController = navController, email = email)
        }

        // Main screens with bottom navigation
        composable(Screen.Home.route) {
            MainScreenWithBottomNav(navController = navController, initialTab = 0)
        }

        composable(Screen.Store.route) {
            MainScreenWithBottomNav(navController = navController, initialTab = 1)
        }

        composable(Screen.Profile.route) {
            MainScreenWithBottomNav(navController = navController, initialTab = 2)
        }

    }
}

@Composable
fun MainScreenWithBottomNav(
    navController: androidx.navigation.NavController,
    initialTab: Int = 0
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryPink,
                        selectedTextColor = PrimaryPink,
                        indicatorColor = PrimaryPink.copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Camera, contentDescription = "Scan") },
                    label = { Text("Scan") },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryPink,
                        selectedTextColor = PrimaryPink,
                        indicatorColor = PrimaryPink.copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryPink,
                        selectedTextColor = PrimaryPink,
                        indicatorColor = PrimaryPink.copy(alpha = 0.2f)
                    )
                )
            }
        }
    ) { paddingValues ->
        @Suppress("UNUSED_VARIABLE")
        val padding = paddingValues
        when (selectedTab) {
            0 -> HomeScreen()
            1 -> ScanScreen(snackbarHostState)
            2 -> ProfileScreen(navController = navController) // Pass navController here
        }
    }
}