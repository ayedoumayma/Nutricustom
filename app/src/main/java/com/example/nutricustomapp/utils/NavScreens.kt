package com.example.nutricustomapp.utils

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object OTPValidation : Screen("otp_validation/{code}") {
        fun createRoute(code: String) = "otp_validation/$code"
    }
    object ResetPassword : Screen("reset_password/{email}") {
        fun createRoute(email: String) = "reset_password/$email"
    }
    object Home : Screen("home") // This is your main/home screen
    object Store : Screen("store")
    object Profile : Screen("profile")
    // Remove the Home object since you don't have a home screen
}