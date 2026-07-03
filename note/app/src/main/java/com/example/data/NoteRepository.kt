package com.example.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    fun getNotes(showHidden: Boolean): Flow<List<Note>> = noteDao.getNotes(showHidden)
    
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)

    suspend fun insert(note: Note) = noteDao.insertNote(note)

    suspend fun update(note: Note) = noteDao.updateNote(note)

    suspend fun deleteById(id: Int) = noteDao.deleteNoteById(id)

    suspend fun deleteExpired(currentTime: Long) = noteDao.deleteExpiredNotes(currentTime)
    
    suspend fun selfDestruct() = noteDao.deleteAllNotes()
}
