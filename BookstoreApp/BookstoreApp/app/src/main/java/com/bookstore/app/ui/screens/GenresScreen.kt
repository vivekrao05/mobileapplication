package com.bookstore.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bookstore.app.model.GenreRequest
import com.bookstore.app.model.GenreResponse
import com.bookstore.app.network.ApiService
import com.bookstore.app.viewmodel.GenreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenresScreen(navController: NavController, api: ApiService) {
    val viewModel: GenreViewModel = viewModel()

    val genres by viewModel.genres.observeAsState(emptyList<GenreResponse>())
    val loading by viewModel.loading.observeAsState(false)

    LaunchedEffect(Unit) { viewModel.fetchGenres(api) }

    var showDialog   by remember { mutableStateOf(false) }
    var editTarget   by remember { mutableStateOf<GenreResponse?>(null) }
    var deleteTarget by remember { mutableStateOf<GenreResponse?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Genres") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editTarget = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Genre")
            }
        }
    ) { padding ->
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            genres.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No genres yet. Tap + to add one.")
            }
            else -> LazyColumn(contentPadding = padding) {
                items(genres) { genre ->
                    GenreCard(
                        genre = genre,
                        onEdit = { editTarget = genre; showDialog = true },
                        onDelete = { deleteTarget = genre }
                    )
                }
            }
        }
    }

    if (showDialog) {
        GenreFormDialog(
            genre = editTarget,
            onDismiss = { showDialog = false; editTarget = null },
            onSave = { request ->
                if (editTarget != null) {
                    viewModel.updateGenre(api, editTarget!!.id, request)
                } else {
                    viewModel.createGenre(api, request)
                }
                showDialog = false
                editTarget = null
            }
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete Genre") },
            text = { Text("Delete \"${target.name}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteGenre(api, target.id)
                    deleteTarget = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun GenreCard(
    genre: GenreResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                genre.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun GenreFormDialog(
    genre: GenreResponse?,
    onDismiss: () -> Unit,
    onSave: (GenreRequest) -> Unit
) {
    val isEdit = genre != null

    var name by remember { mutableStateOf(genre?.name ?: "") }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Edit Genre" else "Add Genre") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text("Genre Name *") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nameError) {
                    Text(
                        "Genre name is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.trim().isEmpty()) {
                    nameError = true
                    return@TextButton
                }
                onSave(GenreRequest(name.trim()))
            }) {
                Text(if (isEdit) "Update" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}