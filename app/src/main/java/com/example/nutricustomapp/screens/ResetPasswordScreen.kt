package com.example.nutricustomapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.nutricustomapp.database.AppDatabase
import com.example.nutricustomapp.ui.components.GamerButton
import com.example.nutricustomapp.ui.components.GamerTextField
import com.example.nutricustomapp.ui.theme.GrayMedium
import com.example.nutricustomapp.utils.Screen
import com.example.nutricustomapp.utils.Validator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: NavController, email: String) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getInstance(context) }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    // Real-time validation
    LaunchedEffect(password) {
        passwordError = password.isNotEmpty() && !Validator.isValidPassword(password)
        if (confirmPassword.isNotEmpty()) {
            confirmPasswordError = !Validator.doPasswordsMatch(password, confirmPassword)
        }
    }

    LaunchedEffect(confirmPassword) {
        confirmPasswordError = confirmPassword.isNotEmpty() && !Validator.doPasswordsMatch(password, confirmPassword)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Reset Password",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = "Please enter your new password and confirm it.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Password Field
            GamerTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                icon = Icons.Default.Lock,
                isPassword = true,
                isError = passwordError,
                errorMessage = "Must not be empty!"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            GamerTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                icon = Icons.Default.Lock,
                isPassword = true,
                isError = confirmPasswordError,
                errorMessage = "Must be the same password!"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            GamerButton(
                text = "Submit",
                onClick = {
                    val isPasswordValid = Validator.isValidPassword(password)
                    val isConfirmPasswordValid = Validator.doPasswordsMatch(password, confirmPassword)

                    passwordError = !isPasswordValid
                    confirmPasswordError = !isConfirmPasswordValid

                    if (isPasswordValid && isConfirmPasswordValid) {
                        scope.launch {
                            try {
                                // Update password in database
                                withContext(Dispatchers.IO) {
                                    database.userDAO().updatePassword(email, password)
                                }

                                snackbarHostState.showSnackbar("Password Reset Successful!")

                                // Navigate to login
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error: ${e.message}")
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("You Have some errors in your inputs!")
                        }
                    }
                }
            )
        }
    }
}