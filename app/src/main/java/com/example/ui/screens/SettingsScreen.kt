package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SECURITY PROTOCOLS") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
            Text("These settings are highly sensitive. Please proceed with caution.", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Self-Destruct Threshold", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            Text("Wipe all data after 5 failed attempts (Default)", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Cloud Backup (GitHub)", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            Text("Status: Not Configured (E2E Encryption Ready)", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            Button(onClick = { /* Todo */ }, modifier = Modifier.padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Text("Configure GitHub Token")
            }
        }
    }
}
