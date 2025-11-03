package com.example.nutricustomapp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.nutricustomapp.ui.components.GamerButton
import com.example.nutricustomapp.ui.theme.GrayMedium
import com.example.nutricustomapp.ui.theme.InputBorderNormal
import com.example.nutricustomapp.ui.theme.PrimaryPink
import com.example.nutricustomapp.utils.Screen
import com.example.nutricustomapp.utils.getLoggedInEmail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPValidationScreen(navController: NavController, expectedCode: String) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val email = getLoggedInEmail(context)

    var otp1 by remember { mutableStateOf("") }
    var otp2 by remember { mutableStateOf("") }
    var otp3 by remember { mutableStateOf("") }
    var otp4 by remember { mutableStateOf("") }

    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }

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
            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Enter the code sent to you by\nEmail or Mobile number",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // OTP Fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OTPTextField(
                    value = otp1,
                    onValueChange = {
                        if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                            otp1 = it
                            if (it.length == 1) focusRequester2.requestFocus()
                        }
                    }
                )

                OTPTextField(
                    value = otp2,
                    onValueChange = {
                        if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                            otp2 = it
                            if (it.length == 1) focusRequester3.requestFocus()
                        }
                    },
                    focusRequester = focusRequester2
                )

                OTPTextField(
                    value = otp3,
                    onValueChange = {
                        if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                            otp3 = it
                            if (it.length == 1) focusRequester4.requestFocus()
                        }
                    },
                    focusRequester = focusRequester3
                )

                OTPTextField(
                    value = otp4,
                    onValueChange = {
                        if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                            otp4 = it
                        }
                    },
                    focusRequester = focusRequester4
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Verify Button
            GamerButton(
                text = "Verify",
                onClick = {
                    val enteredCode = "$otp1$otp2$otp3$otp4"

                    if (enteredCode.length != 4) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please enter complete code!")
                        }
                        return@GamerButton
                    }

                    if (enteredCode == expectedCode) {
                        navController.navigate(Screen.ResetPassword.createRoute(email)) {
                            popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Wrong code!")
                        }
                        otp1 = ""
                        otp2 = ""
                        otp3 = ""
                        otp4 = ""
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Didn't receive code
            Text(
                text = "Didn't receive a verification code?",
                color = GrayMedium,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Resend Code
            TextButton(onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("Coming soon :)")
                }
            }) {
                Text("Resend code", color = PrimaryPink, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPTextField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .width(60.dp)
            .height(60.dp)
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .border(1.dp, InputBorderNormal, RoundedCornerShape(8.dp)),
        textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
        )
    )
}