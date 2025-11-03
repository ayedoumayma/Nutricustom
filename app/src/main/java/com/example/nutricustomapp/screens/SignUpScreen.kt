package com.example.nutricustomapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
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
import com.example.nutricustomapp.ui.components.GamerDatePickerField
import com.example.nutricustomapp.ui.theme.GrayMedium
import com.example.nutricustomapp.utils.Validator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getInstance(context) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var dateOfBirthError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    // Real-time validation
    LaunchedEffect(firstName) {
        firstNameError = firstName.isNotEmpty() && firstName.length < 2
    }

    LaunchedEffect(lastName) {
        lastNameError = lastName.isNotEmpty() && lastName.length < 2
    }

    LaunchedEffect(dateOfBirth) {
        dateOfBirthError = dateOfBirth.isNotEmpty() && !Validator.isValidDate(dateOfBirth)
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

            // First Name Field
            GamerTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                icon = Icons.Default.Person,
                isError = firstNameError,
                errorMessage = "Must be at least 2 characters!"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Last Name Field
            GamerTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                icon = Icons.Default.Person,
                isError = lastNameError,
                errorMessage = "Must be at least 2 characters!"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date of Birth Field with DatePicker
            GamerDatePickerField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = "Date of Birth",
                icon = Icons.Default.CalendarToday,
                isError = dateOfBirthError,
                errorMessage = "Please select your date of birth!"
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
                errorMessage = "Invalid email format!"
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
                errorMessage = "Password must be at least 6 characters!"
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
                errorMessage = "Passwords must match!"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            GamerButton(
                text = "Submit",
                onClick = {
                    val isFirstNameValid = firstName.length >= 2
                    val isLastNameValid = lastName.length >= 2
                    val isDateOfBirthValid = dateOfBirth.isNotEmpty()
                    val isEmailValid = Validator.isValidEmail(email)
                    val isPasswordValid = Validator.isValidPassword(password)
                    val isConfirmPasswordValid = Validator.doPasswordsMatch(password, confirmPassword)

                    firstNameError = !isFirstNameValid
                    lastNameError = !isLastNameValid
                    dateOfBirthError = !isDateOfBirthValid
                    emailError = !isEmailValid
                    passwordError = !isPasswordValid
                    confirmPasswordError = !isConfirmPasswordValid

                    if (isFirstNameValid && isLastNameValid && isDateOfBirthValid &&
                        isEmailValid && isPasswordValid && isConfirmPasswordValid) {
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
                                                firstName = firstName,
                                                lastName = lastName,
                                                dateOfBirth = dateOfBirth,
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
                            snackbarHostState.showSnackbar("Please fix all errors!")
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