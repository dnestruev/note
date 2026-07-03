package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AuthState
import com.example.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthenticatedNormal: () -> Unit,
    onAuthenticatedFake: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.UnlockedNormal -> onAuthenticatedNormal()
            is AuthState.UnlockedFake -> onAuthenticatedFake()
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when (authState) {
            is AuthState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            is AuthState.SetupRequired -> SetupScreen(viewModel)
            is AuthState.Locked -> LockScreen(viewModel)
            is AuthState.Wiped -> WipedScreen()
            else -> {}
        }
    }
}

@Composable
fun SetupScreen(viewModel: AuthViewModel) {
    var step by remember { mutableStateOf(1) }
    var primaryPin by remember { mutableStateOf("") }
    var fakePin by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Icon(Icons.Filled.Lock, contentDescription = "Setup", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (step == 1) "GHOST PROTOCOL" else "DECOY PROTOCOL",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (step == 1) "Set your primary access PIN." else "Set a fake PIN to show an empty vault.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        PinPad(
            pin = if (step == 1) primaryPin else fakePin,
            onPinChange = { newPin ->
                if (step == 1) primaryPin = newPin else fakePin = newPin
            },
            onSubmit = {
                if (step == 1 && primaryPin.length >= 4) {
                    viewModel.setupPin(primaryPin)
                    step = 2
                } else if (step == 2 && fakePin.length >= 4) {
                    viewModel.setupFakePin(fakePin)
                    viewModel.authenticate(primaryPin) // Authenticate with real PIN to enter
                }
            }
        )
    }
}

@Composable
fun LockScreen(viewModel: AuthViewModel) {
    var pin by remember { mutableStateOf("") }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Icon(Icons.Filled.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(32.dp))
        
        // Pin display dots
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            for (i in 0 until 4) { // Assuming 4 digit pin for UI, though logic allows any length
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (i < pin.length) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        PinPad(
            pin = pin,
            onPinChange = { pin = it },
            onSubmit = {
                if (pin.length >= 4) {
                    viewModel.authenticate(pin)
                    pin = ""
                }
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Biometric button (simplified, normally requires Activity context)
        IconButton(onClick = { /* Biometric prompt would go here */ }) {
            Icon(Icons.Filled.Fingerprint, contentDescription = "Biometric", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
fun WipedScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = "PROTOCOL ZERO",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Data has been purged due to multiple failed access attempts.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun PinPad(
    pin: String,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val buttons = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "C", "0", "OK"
    )

    Column(modifier = Modifier.fillMaxWidth(0.8f)) {
        for (i in 0 until 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (j in 0 until 3) {
                    val index = i * 3 + j
                    val item = buttons[index]
                    PinButton(text = item) {
                        when (item) {
                            "C" -> onPinChange("")
                            "OK" -> onSubmit()
                            else -> if (pin.length < 8) onPinChange(pin + item)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PinButton(text: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
