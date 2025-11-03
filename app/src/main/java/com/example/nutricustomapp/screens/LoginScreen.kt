package com.example.nutricustomapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.nutricustomapp.R
import com.example.nutricustomapp.database.AppDatabase
import com.example.nutricustomapp.ui.components.GamerButton
import com.example.nutricustomapp.ui.components.GamerTextField
import com.example.nutricustomapp.ui.theme.PrimaryPink
import com.example.nutricustomapp.utils.Screen
import com.example.nutricustomapp.utils.Validator
import com.example.nutricustomapp.utils.saveUserSession

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getInstance(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    // Real-time validation
    LaunchedEffect(email) {
        emailError = email.isNotEmpty() && !Validator.isValidEmail(email)
    }

    LaunchedEffect(password) {
        passwordError = password.isNotEmpty() && !Validator.isValidPassword(password)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            GamerTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                isError = emailError,
                errorMessage = "Must not be empty!"
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(12.dp))

            // Remember Me & Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryPink)
                    )
                    Text("Remember Me")
                }

                TextButton(onClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }) {
                    Text("Forgot password?", color = PrimaryPink)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            GamerButton(
                text = "Login",
                onClick = {
                    val isEmailValid = Validator.isValidEmail(email)
                    val isPasswordValid = Validator.isValidPassword(password)

                    emailError = !isEmailValid
                    passwordError = !isPasswordValid

                    if (isEmailValid && isPasswordValid) {
                        scope.launch {
                            try {
                                val user = withContext(Dispatchers.IO) {
                                    database.userDAO().login(email, password)
                                }

                                if (user != null) {
                                    // Save session
                                    saveUserSession(context, user.email, user.fullName)

                                    snackbarHostState.showSnackbar("Login Successful!")

                                    // Navigate to News Screen instead of Home
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                } else {
                                    snackbarHostState.showSnackbar("Invalid email or password!")
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

            Spacer(modifier = Modifier.height(16.dp))

            // OR
            Text(
                text = "OR",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Social Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Coming soon :)")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF1877F2)
                    )
                ) {
                    Text("Facebook")
                }

                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Coming soon :)")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("Google", color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Now
            TextButton(onClick = {
                navController.navigate(Screen.SignUp.route)
            }) {
                Text("Don't have an account? Register Now", color = PrimaryPink)
            }
        }
    }
}