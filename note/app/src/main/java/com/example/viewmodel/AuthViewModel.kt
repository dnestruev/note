package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.NoteRepository
import com.example.data.SecurityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.security.MessageDigest

sealed class AuthState {
    object Loading : AuthState()
    object SetupRequired : AuthState()
    object Locked : AuthState()
    object UnlockedNormal : AuthState()
    object UnlockedFake : AuthState()
    object Wiped : AuthState() // Triggered self-destruct
}

class AuthViewModel(
    private val securityRepo: SecurityRepository,
    private val noteRepo: NoteRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkInitialState()
    }

    private fun checkInitialState() {
        viewModelScope.launch {
            val pinHash = securityRepo.pinHashFlow.first()
            if (pinHash.isNullOrEmpty()) {
                _authState.value = AuthState.SetupRequired
            } else {
                _authState.value = AuthState.Locked
            }
        }
    }

    fun setupPin(pin: String) {
        viewModelScope.launch {
            val hash = hashPin(pin)
            securityRepo.setPin(hash)
            _authState.value = AuthState.UnlockedNormal
        }
    }
    
    fun setupFakePin(pin: String) {
        viewModelScope.launch {
             val hash = hashPin(pin)
             securityRepo.setFakePin(hash)
        }
    }

    fun authenticate(pin: String) {
        viewModelScope.launch {
            val hash = hashPin(pin)
            val realHash = securityRepo.pinHashFlow.first()
            val fakeHash = securityRepo.fakePinHashFlow.first()

            if (hash == realHash) {
                securityRepo.resetFailedAttempts()
                _authState.value = AuthState.UnlockedNormal
            } else if (hash == fakeHash && !fakeHash.isNullOrEmpty()) {
                securityRepo.resetFailedAttempts()
                _authState.value = AuthState.UnlockedFake
            } else {
                handleFailedAttempt()
            }
        }
    }
    
    fun authenticateBiometricSuccess() {
        viewModelScope.launch {
            securityRepo.resetFailedAttempts()
            _authState.value = AuthState.UnlockedNormal
        }
    }

    private suspend fun handleFailedAttempt() {
        val attempts = securityRepo.incrementFailedAttempts()
        val threshold = securityRepo.wipeThresholdFlow.first()
        if (attempts >= threshold) {
            triggerSelfDestruct()
        }
    }

    private suspend fun triggerSelfDestruct() {
        noteRepo.selfDestruct()
        securityRepo.selfDestruct()
        _authState.value = AuthState.Wiped
    }

    fun lock() {
        viewModelScope.launch {
            val pinHash = securityRepo.pinHashFlow.first()
            if (!pinHash.isNullOrEmpty()) {
                _authState.value = AuthState.Locked
            } else {
                _authState.value = AuthState.SetupRequired
            }
        }
    }

    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        fun provideFactory(
            securityRepository: SecurityRepository,
            noteRepository: NoteRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(securityRepository, noteRepository) as T
            }
        }
    }
}
