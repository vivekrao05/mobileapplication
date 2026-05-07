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
import com.bookstore.app.model.AuthorRequest
import com.bookstore.app.model.AuthorResponse
import com.bookstore.app.network.ApiService
import com.bookstore.app.viewmodel.AuthorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorsScreen(navController: NavController, api: ApiService) {
    val viewModel: AuthorViewModel = viewModel()

    val authors by viewModel.authors.observeAsState(emptyList<AuthorResponse>())
    val loading by viewModel.loading.observeAsState(false)

    LaunchedEffect(Unit) { viewModel.fetchAuthors(api) }

    var showDialog   by remember { mutableStateOf(false) }
    var editTarget   by remember { mutableStateOf<AuthorResponse?>(null) }
    var deleteTarget by remember { mutableStateOf<AuthorResponse?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Authors") },
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
                Icon(Icons.Default.Add, contentDescription = "Add Author")
            }
        }
    ) { padding ->
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            authors.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No authors yet. Tap + to add one.")
            }
            else -> LazyColumn(contentPadding = padding) {
                items(authors) { author ->
                    AuthorCard(
                        author = author,
                        onEdit = { editTarget = author; showDialog = true },
                        onDelete = { deleteTarget = author }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AuthorFormDialog(
            author = editTarget,
            onDismiss = { showDialog = false; editTarget = null },
            onSave = { request ->
                if (editTarget != null) {
                    viewModel.updateAuthor(api, editTarget!!.id, request)
                } else {
                    viewModel.createAuthor(api, request)
                }
                showDialog = false
                editTarget = null
            }
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete Author") },
            text = { Text("Delete \"${target.name}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAuthor(api, target.id)
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
private fun AuthorCard(
    author: AuthorResponse,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(author.name, style = MaterialTheme.typography.titleMedium)
                if (!author.biography.isNullOrEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        author.biography!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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
private fun AuthorFormDialog(
    author: AuthorResponse?,
    onDismiss: () -> Unit,
    onSave: (AuthorRequest) -> Unit
) {
    val isEdit = author != null

    var name by remember { mutableStateOf(author?.name ?: "") }
    var bio  by remember { mutableStateOf(author?.biography ?: "") }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Edit Author" else "Add Author") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text("Name *") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nameError) {
                    Text(
                        "Name is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Biography (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.trim().isEmpty()) {
                    nameError = true
                    return@TextButton
                }
                onSave(AuthorRequest(name.trim(), bio.trim().ifEmpty { null }))
            }) {
                Text(if (isEdit) "Update" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}