package com.bookstore.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bookstore.app.network.ApiService
import com.bookstore.app.util.TokenManager
import com.bookstore.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    api: ApiService,
    tokenManager: TokenManager
) {
    val viewModel: AuthViewModel = viewModel<AuthViewModel>()

    val token   by viewModel.token.observeAsState("")
    val error   by viewModel.error.observeAsState("")
    val loading by viewModel.loading.observeAsState(false)

    // When token arrives, save it and navigate to books
    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            tokenManager.saveToken(token!!)
            navController.navigate("books") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📚 Bookstore", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Collection Manager",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(40.dp))

        if (!error.isNullOrEmpty()) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(api, username, password) },
            enabled = !loading && username.isNotEmpty() && password.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Signing in..." else "Sign In")
        }
    }
}