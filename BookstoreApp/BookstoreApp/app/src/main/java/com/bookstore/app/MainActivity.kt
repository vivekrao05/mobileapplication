package com.bookstore.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bookstore.app.network.RetrofitClient
import com.bookstore.app.ui.screens.AuthorsScreen
import com.bookstore.app.ui.screens.BooksScreen
import com.bookstore.app.ui.screens.GenresScreen
import com.bookstore.app.ui.screens.LoginScreen
import com.bookstore.app.util.TokenManager

// MainActivity.kt — keep this in Kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)
        val api = RetrofitClient.getApiService(tokenManager)
        val startDest = if (tokenManager.isLoggedIn()) "books" else "login"

        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = startDest) {
                composable("login") { LoginScreen(navController, api, tokenManager) }
                composable("books") { BooksScreen(navController, api, tokenManager) }
                composable("authors") { AuthorsScreen(navController, api) }
                composable("genres") { GenresScreen(navController, api) }
            }
        }
    }
}