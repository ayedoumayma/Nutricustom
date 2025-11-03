package com.example.nutricustomapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.nutricustomapp.models.User
import com.example.nutricustomapp.ui.components.GamerButton
import com.example.nutricustomapp.ui.components.GamerTextField
import com.example.nutricustomapp.ui.theme.GrayMedium
import com.example.nutricustomapp.utils.Screen
import com.example.nutricustomapp.utils.Validator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getInstance(context) }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var fullNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    // Real-time validation
    LaunchedEffect(fullName) {
        fullNameError = fullName.isNotEmpty() && !Validator.isValidFullName(fullName)
    }

    LaunchedEffect(email) {
        emailError = email.isNotEmpty() && !Validator.isValidEmail(email)
    }

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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name Field
            GamerTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "FullName",
                icon = Icons.Default.Person,
                isError = fullNameError,
                errorMessage = "Must be at least 3 characters!"
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    val isFullNameValid = Validator.isValidFullName(fullName)
                    val isEmailValid = Validator.isValidEmail(email)
                    val isPasswordValid = Validator.isValidPassword(password)
                    val isConfirmPasswordValid = Validator.doPasswordsMatch(password, confirmPassword)

                    fullNameError = !isFullNameValid
                    emailError = !isEmailValid
                    passwordError = !isPasswordValid
                    confirmPasswordError = !isConfirmPasswordValid

                    if (isFullNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid) {
                        scope.launch {
                            try {
                                // Check if user already exists
                                val existingUser = withContext(Dispatchers.IO) {
                                    database.userDAO().getUserByEmail(email)
                                }

                                if (existingUser != null) {
                                    snackbarHostState.showSnackbar("Email already registered!")
                                } else {
                                    // Insert new user
                                    withContext(Dispatchers.IO) {
                                        database.userDAO().insert(
                                            User(
                                                fullName = fullName,
                                                email = email,
                                                password = password
                                            )
                                        )
                                    }

                                    snackbarHostState.showSnackbar("Registration Successful!")

                                    // Navigate back to login
                                    navController.popBackStack()
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

            // Terms & Privacy
            TextButton(onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("Coming soon :)")
                }
            }) {
                Text(
                    "By registering you agree to our Terms & Conditions and Privacy Policy",
                    color = GrayMedium,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}