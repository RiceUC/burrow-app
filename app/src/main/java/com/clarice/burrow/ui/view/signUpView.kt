@file:OptIn(ExperimentalMaterial3Api::class)

package com.clarice.burrow.ui.view

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clarice.burrow.R
import com.clarice.burrow.ui.viewmodel.AuthViewModel
import java.util.*

@Composable
fun SignUpView(
    onBack: () -> Unit = {},
    onSignIn: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember { AuthViewModel(context) }
    val registerState = viewModel.registerState

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf("Female") }

    // Initialize gender in view model
    LaunchedEffect(Unit) {
        viewModel.updateRegisterGender(gender)
    }

    // Date picker
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, y, m, d ->
            dob = String.format("%04d-%02d-%02d", y, m + 1, d)
            viewModel.updateRegisterBirthdate(dob)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Handle successful registration
    LaunchedEffect(registerState.isSuccess) {
        if (registerState.isSuccess) {
            onSignUpSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.welcomebg),
                contentScale = ContentScale.Crop
            )
    ) {

        // Back button
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Transparent, RoundedCornerShape(50))
                    .border(2.dp, Color.White, RoundedCornerShape(50))
                    .padding(8.dp)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Center content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Create your account",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(40.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        viewModel.updateRegisterName(it)
                    },
                    placeholder = { Text("Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = outlinedFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !registerState.isLoading
                )

                Spacer(Modifier.height(20.dp))

                // Username
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        viewModel.updateRegisterUsername(it)
                    },
                    placeholder = { Text("Username") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = outlinedFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !registerState.isLoading
                )

                Spacer(Modifier.height(20.dp))

                // Date of Birth
                OutlinedTextField(
                    value = if (dob.isNotBlank()) formatDateForDisplay(dob) else "",
                    onValueChange = { },
                    placeholder = {
                        Text(
                            text = "Date of birth",
                            color = Color(0xFF2E235C).copy(alpha = 0.4f)
                        )
                    },
                    singleLine = true,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { datePicker.show() },
                            enabled = !registerState.isLoading
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = outlinedFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { if (!registerState.isLoading) datePicker.show() },
                    enabled = false // Always disabled for clicking through
                )

                Spacer(Modifier.height(20.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        viewModel.updateRegisterPassword(it)
                    },
                    placeholder = { Text("Password") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = outlinedFieldColors(),
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !registerState.isLoading
                )

                Spacer(Modifier.height(20.dp))

                // Gender
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gender", color = Color.White, fontSize = 15.sp)

                    Spacer(Modifier.width(24.dp))

                    RadioOption(
                        text = "Female",
                        selected = gender,
                        enabled = !registerState.isLoading,
                        onSelect = {
                            gender = it
                            viewModel.updateRegisterGender(it)
                        }
                    )

                    Spacer(Modifier.width(16.dp))

                    RadioOption(
                        text = "Male",
                        selected = gender,
                        enabled = !registerState.isLoading,
                        onSelect = {
                            gender = it
                            viewModel.updateRegisterGender(it)
                        }
                    )
                }

                // Error Message
                if (registerState.error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFCDD2)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = registerState.error,
                            color = Color(0xFFB71C1C),
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.height(35.dp))

                // Sign Up button
                Button(
                    onClick = {
                        viewModel.register { onSignUpSuccess() }
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(),
                    enabled = !registerState.isLoading &&
                            name.isNotBlank() &&
                            username.isNotBlank() &&
                            password.isNotBlank()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF6A8BFF).copy(
                                            alpha = if (registerState.isLoading ||
                                                name.isBlank() ||
                                                username.isBlank() ||
                                                password.isBlank()) 0.5f else 1f
                                        ),
                                        Color(0xFF9BB2FF).copy(
                                            alpha = if (registerState.isLoading ||
                                                name.isBlank() ||
                                                username.isBlank() ||
                                                password.isBlank()) 0.5f else 1f
                                        )
                                    )
                                ),
                                shape = RoundedCornerShape(50.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (registerState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "SIGN UP",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(25.dp))

                TextButton(
                    onClick = onSignIn,
                    enabled = !registerState.isLoading
                ) {
                    Text(
                        text = "ALREADY HAVE AN ACCOUNT? SIGN IN",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun outlinedFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFF2E235C),
        unfocusedTextColor = Color(0xFF2E235C),
        unfocusedBorderColor = Color.Transparent,
        focusedBorderColor = Color(0xFFB0C0FF),
        unfocusedContainerColor = Color(0xFFF1F3F9),
        focusedContainerColor = Color(0xFFF1F3F9),
        disabledContainerColor = Color(0xFFF1F3F9),
        disabledBorderColor = Color.Transparent,
        focusedPlaceholderColor = Color(0xFF2E235C).copy(alpha = 0.4f),
        unfocusedPlaceholderColor = Color(0xFF2E235C).copy(alpha = 0.4f)
    )

@Composable
private fun RadioOption(
    text: String,
    selected: String,
    enabled: Boolean = true,
    onSelect: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(enabled = enabled) {
            if (enabled) onSelect(text)
        }
    ) {
        RadioButton(
            selected = selected == text,
            onClick = { if (enabled) onSelect(text) },
            enabled = enabled
        )
        Text(
            text,
            color = Color.White.copy(alpha = if (enabled) 1f else 0.5f)
        )
    }
}

private fun formatDateForDisplay(isoDate: String): String {
    return try {
        val parts = isoDate.split("-")
        if (parts.size == 3) {
            "${parts[2]}/${parts[1]}/${parts[0]}" // DD/MM/YYYY
        } else {
            isoDate
        }
    } catch (e: Exception) {
        isoDate
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpPreview() {
    SignUpView()
}
