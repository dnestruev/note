package com.example

import android.content.Context
import com.example.data.AppDatabase
import com.example.data.NoteRepository
import com.example.data.SecurityRepository

class AppContainer(private val context: Context) {
    val database by lazy { AppDatabase.getDatabase(context) }
    val noteRepository by lazy { NoteRepository(database.noteDao()) }
    val securityRepository by lazy { SecurityRepository(context) }
}
