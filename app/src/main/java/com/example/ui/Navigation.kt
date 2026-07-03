package com.example.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.NotesViewModel
import com.example.ui.screens.*

@Composable
fun GhostNotesNavigation(
    authViewModel: AuthViewModel,
    notesViewModel: NotesViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(
                viewModel = authViewModel,
                onAuthenticatedNormal = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onAuthenticatedFake = {
                    navController.navigate("fake_home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        
        composable("home") {
            NoteListScreen(
                viewModel = notesViewModel,
                onAddNote = { navController.navigate("add_note") },
                onNoteClick = { noteId -> navController.navigate("note_detail/$noteId") },
                onSettings = { navController.navigate("settings") },
                onLock = {
                    authViewModel.lock()
                    navController.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        
        composable("fake_home") {
            FakeHomeScreen(
                onLock = {
                    authViewModel.lock()
                    navController.navigate("auth") {
                        popUpTo("fake_home") { inclusive = true }
                    }
                }
            )
        }
        
        composable("add_note") {
            AddNoteScreen(
                viewModel = notesViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("note_detail/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            NoteDetailScreen(
                noteId = noteId,
                viewModel = notesViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
