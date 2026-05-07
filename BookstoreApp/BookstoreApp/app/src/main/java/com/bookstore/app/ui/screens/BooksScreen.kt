package com.bookstore.app.ui.screens

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bookstore.app.model.AuthorResponse
import com.bookstore.app.model.BookRequest
import com.bookstore.app.model.BookResponse
import com.bookstore.app.model.GenreResponse
import com.bookstore.app.network.ApiService
import com.bookstore.app.util.TokenManager
import com.bookstore.app.viewmodel.AuthorViewModel
import com.bookstore.app.viewmodel.BookViewModel
import com.bookstore.app.viewmodel.GenreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScreen(
    navController: NavController,
    api: ApiService,
    tokenManager: TokenManager
) {
    val bookVM: BookViewModel = viewModel()
    val authorVM: AuthorViewModel = viewModel()
    val genreVM: GenreViewModel = viewModel()

    val books by bookVM.books.observeAsState(emptyList<BookResponse>())
    val authors by authorVM.authors.observeAsState(emptyList<AuthorResponse>())
    val genres by genreVM.genres.observeAsState(emptyList<GenreResponse>())
    val loading by bookVM.loading.observeAsState(false)

    LaunchedEffect(Unit) {
        bookVM.fetchBooks(api)
        authorVM.fetchAuthors(api)
        genreVM.fetchGenres(api)
    }

    var showDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<BookResponse?>(null) }
    var deleteTarget by remember { mutableStateOf<BookResponse?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Books")
                        if (books.isNotEmpty()) {
                            Spacer(Modifier.width(8.dp))
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(books.size.toString())
                            }
                        }
                    }
                }, actions = {
                    // Authors button with badge
                    BadgedBox(
                        badge = {
                            if (authors.isNotEmpty()) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(authors.size.toString())
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        TextButton(onClick = { navController.navigate("authors") }) {
                            Text("Authors")
                        }
                    }

                    // Genres button with badge
                    BadgedBox(
                        badge = {
                            if (genres.isNotEmpty()) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(genres.size.toString())
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        TextButton(onClick = { navController.navigate("genres") }) {
                            Text("Genres")
                        }
                    }

                    // Logout
                    IconButton(onClick = {
                        tokenManager.clearAll()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editTarget = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
            }
        }
    ) { padding ->

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            books.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No books yet. Tap + to add one.")
            }

            else -> LazyColumn(contentPadding = padding) {
                items(books) { book ->
                    BookCard(
                        book = book,
                        onEdit = { editTarget = book; showDialog = true },
                        onDelete = { deleteTarget = book }
                    )
                }
            }
        }
    }

    if (showDialog) {
        BookFormDialog(
            book = editTarget,
            authors = authors,
            genres = genres,
            onDismiss = { showDialog = false; editTarget = null },
            onSave = { request ->
                if (editTarget != null) {
                    bookVM.updateBook(api, editTarget!!.id, request)
                } else {
                    bookVM.createBook(api, request)
                }
                showDialog = false
                editTarget = null
            }
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete Book") },
            text = { Text("Delete \"${target.title}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    bookVM.deleteBook(api, target.id)
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
private fun BookCard(
    book: BookResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    book.author?.name ?: "No Author",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    book.genre?.name ?: "No Genre",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                book.publishedYear?.let {
                    Text(
                        "Year: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!book.isbn.isNullOrEmpty()) {
                    Text(
                        "ISBN: ${book.isbn}",
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
private fun BookFormDialog(
    book: BookResponse?,
    authors: List<AuthorResponse>,
    genres: List<GenreResponse>,
    onDismiss: () -> Unit,
    onSave: (BookRequest) -> Unit
) {
    val isEdit = book != null

    var title by remember { mutableStateOf(book?.title ?: "") }
    var isbn by remember { mutableStateOf(book?.isbn ?: "") }
    var year by remember { mutableStateOf(book?.publishedYear?.toString() ?: "") }
    var authorId by remember { mutableStateOf(book?.author?.id) }
    var genreId by remember { mutableStateOf(book?.genre?.id) }
    var titleError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Edit Book" else "Add Book") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    label = { Text("Title *") },
                    isError = titleError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (titleError) {
                    Text(
                        "Title is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = isbn,
                    onValueChange = { isbn = it },
                    label = { Text("ISBN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it.filter { c -> c.isDigit() } },
                    label = { Text("Published Year") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                DropdownSelector(
                    label = "Author *",
                    options = authors,
                    selectedId = authorId,
                    optionLabel = { it.name },
                    optionId = { it.id },
                    onSelect = { authorId = it }
                )

                Spacer(Modifier.height(8.dp))

                DropdownSelector(
                    label = "Genre *",
                    options = genres,
                    selectedId = genreId,
                    optionLabel = { it.name },
                    optionId = { it.id },
                    onSelect = { genreId = it }
                )


            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.trim().isEmpty()) {
                    titleError = true
                    return@TextButton
                }
                val request = BookRequest(
                    title.trim(),
                    isbn.trim().ifEmpty { null },
                    year.toIntOrNull(),
                    authorId,
                    genreId
                )
                onSave(request)
            }) {
                Text(if (isEdit) "Update" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownSelector(
    label: String,
    options: List<T>,
    selectedId: Long?,
    optionLabel: (T) -> String,
    optionId: (T) -> Long,
    onSelect: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedItem = options.find { optionId(it) == selectedId }
    val displayText = selectedItem?.let { optionLabel(it) } ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelect(optionId(option))
                        expanded = false
                    }
                )
            }
        }
    }
}