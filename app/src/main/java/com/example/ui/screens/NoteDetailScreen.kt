package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.Note
import com.example.viewmodel.NotesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Int?,
    viewModel: NotesViewModel,
    onBack: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val note = notes.find { it.id == noteId }

    LaunchedEffect(note) {
        if (note != null) {
            viewModel.viewNote(note) // Handle burn after reading
        }
    }

    if (note == null) {
        // Note not found or just deleted (burn after reading)
        LaunchedEffect(Unit) {
            onBack()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(note.title.ifEmpty { "Untitled" }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteNote(note)
                        onBack()
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (note.isBurnAfterReading) {
                Text(
                    text = "WARNING: This note will self-destruct when you leave this screen.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
