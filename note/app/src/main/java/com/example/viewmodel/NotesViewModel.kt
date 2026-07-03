package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Note
import com.example.data.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _showHidden = MutableStateFlow(false)
    val showHidden: StateFlow<Boolean> = _showHidden

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes: StateFlow<List<Note>> = _showHidden
        .flatMapLatest { showHidden ->
            noteRepository.getNotes(showHidden)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Start expiration checker
        viewModelScope.launch {
            while (true) {
                noteRepository.deleteExpired(System.currentTimeMillis())
                delay(60000) // Check every minute
            }
        }
    }

    fun setFakeMode(isFake: Boolean) {
        // If fake mode, we only show non-hidden notes.
        // Actually, if it's fake mode, we show NOTHING, or maybe fake empty notes.
        // For Ghost Notes, fake mode shows "nothing" or only non-hidden notes. 
        // Let's just show an empty list or a very limited list.
        // But to make it simple, let's just use `showHidden = false` for fake, and `showHidden = true` for normal?
        // Wait, if fake mode is on, we don't want them to see even the normal notes if we want to be super secure,
        // or we just show non-hidden notes. Let's just show an empty list for true fake mode, 
        // but for now let's just toggle a fake flag.
    }

    fun addNote(title: String, content: String, ttlMinutes: Int?, isBurnAfterReading: Boolean, isHidden: Boolean) {
        val expiresAt = ttlMinutes?.let { System.currentTimeMillis() + it * 60 * 1000L }
        viewModelScope.launch {
            noteRepository.insert(Note(
                title = title,
                content = content,
                expiresAt = expiresAt,
                isBurnAfterReading = isBurnAfterReading,
                isHidden = isHidden
            ))
        }
    }
    
    fun viewNote(note: Note) {
        if (note.isBurnAfterReading) {
            viewModelScope.launch {
                noteRepository.deleteById(note.id)
            }
        }
    }
    
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteById(note.id)
        }
    }

    companion object {
        fun provideFactory(
            noteRepository: NoteRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotesViewModel(noteRepository) as T
            }
        }
    }
}
