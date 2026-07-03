package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.GhostNotesNavigation
import com.example.ui.theme.GhostNotesTheme
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val appContainer = (application as GhostNotesApplication).container
        
        setContent {
            GhostNotesTheme {
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModel.provideFactory(
                        appContainer.securityRepository,
                        appContainer.noteRepository
                    )
                )
                val notesViewModel: NotesViewModel = viewModel(
                    factory = NotesViewModel.provideFactory(
                        appContainer.noteRepository
                    )
                )
                GhostNotesNavigation(
                    authViewModel = authViewModel,
                    notesViewModel = notesViewModel
                )
            }
        }
    }
}
