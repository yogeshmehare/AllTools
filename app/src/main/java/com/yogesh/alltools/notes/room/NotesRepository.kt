package com.yogesh.alltools.notes.room

import androidx.lifecycle.LiveData
import com.yogesh.alltools.notes.NotesConstant.Important
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesRepository(private val notesDao: NotesDao)  {
    val allNotes : LiveData<List<Note>> = notesDao.getAllNotes()
    val impNotes : LiveData<List<Note>> = notesDao.getImpNotes(Important)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun addNote(newNote: Note) {
        coroutineScope.launch(Dispatchers.IO) {
            notesDao.addNote(newNote)
        }
    }

    fun addAllNotes(noteList: List<Note>) {
        coroutineScope.launch(Dispatchers.IO) {
           notesDao.addAllNotes(noteList)
        }
    }

    fun updateNote(newNote: Note) {
        coroutineScope.launch(Dispatchers.IO) {
            notesDao.updateNote(newNote)
        }
    }
}
