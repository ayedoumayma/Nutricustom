package com.example.nutricustomapp.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getInstance(context) }

    var emailPhone by remember { mutableStateOf("") }
    var emailPhoneError by remember { mutableStateOf(false) }

    // Real-time validation
    LaunchedEffect(emailPhone) {
        emailPhoneError = emailPhone.isNotEmpty() && !Validator.isValidEmailOrPhone(emailPhone)
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
                text = "Forgot Password",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = "Please enter your registered email to reset your password",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email/Phone Field
            GamerTextField(
                value = emailPhone,
                onValueChange = { emailPhone = it },
                label = "Email/Phone",
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                isError = emailPhoneError,
                errorMessage = "Must not be empty!"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            GamerButton(
                text = "Submit",
                onClick = {
                    if (Validator.isValidEmailOrPhone(emailPhone)) {
                        scope.launch {
                            try {
                                // Check if user exists
                                val user = withContext(Dispatchers.IO) {
                                    database.userDAO().getUserByEmail(emailPhone)
                                }

                                if (user != null) {
                                    // Navigate to OTP with code
                                    navController.navigate(Screen.OTPValidation.createRoute("1234"))
                                } else {
                                    snackbarHostState.showSnackbar("Email not found!")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error: ${e.message}")
                            }
                        }
                    } else {
                        emailPhoneError = true
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
                color = GrayMedium,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Send SMS Button
            GamerButton(
                text = "Send SMS",
                onClick = {
                    if (Validator.isValidEmailOrPhone(emailPhone)) {
                        scope.launch {
                            try {
                                val user = withContext(Dispatchers.IO) {
                                    database.userDAO().getUserByEmail(emailPhone)
                                }

                                if (user != null) {
                                    navController.navigate(Screen.OTPValidation.createRoute("6789"))
                                } else {
                                    snackbarHostState.showSnackbar("Email not found!")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error: ${e.message}")
                            }
                        }
                    } else {
                        emailPhoneError = true
                        scope.launch {
                            snackbarHostState.showSnackbar("You Have some errors in your inputs!")
                        }
                    }
                }
            )
        }
    }
}