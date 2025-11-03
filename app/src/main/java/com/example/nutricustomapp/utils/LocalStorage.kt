package com.example.nutricustomapp.utils

import android.content.Context

private const val PREFS_NAME = "gamer_app_prefs"
private const val KEY_EMAIL = "user_email"
private const val KEY_USERNAME = "user_username"
private const val KEY_IS_LOGGED_IN = "is_logged_in"

fun getLoggedInEmail(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_EMAIL, "") ?: ""
}
fun saveEmail(context: Context, email: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_EMAIL, email).apply()
}

fun getEmail(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_EMAIL, "") ?: ""
}

fun saveUsername(context: Context, username: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_USERNAME, username).apply()
}

fun getUsername(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_USERNAME, "") ?: ""
}

fun saveLoginState(context: Context, isLoggedIn: Boolean) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
}

fun isLoggedIn(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
}

fun clearUserData(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().clear().apply()
}

fun saveUserSession(context: Context, email: String, username: String) {
    saveEmail(context, email)
    saveUsername(context, username)
    saveLoginState(context, true)
}