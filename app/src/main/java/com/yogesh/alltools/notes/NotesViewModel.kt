package com.yogesh.alltools.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yogesh.alltools.notes.NotesConstant.All
import com.yogesh.alltools.notes.NotesConstant.Important
import com.yogesh.alltools.notes.room.Note
import com.yogesh.alltools.notes.room.NotesDao
import com.yogesh.alltools.notes.room.NotesRepository
import com.yogesh.alltools.notes.room.NotesRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application = application) {
    var selectedItems: MutableList<Note> = mutableListOf()
    var notes : LiveData<List<Note>> = MutableLiveData()
    var impNotes : LiveData<List<Note>>
    private val tempNotes = listOf(Note(0, "hi", "whatsUp", All),
    Note(1, "hello", "jaguar", All),
    Note(2, "kia", "mercedes", Important),
    Note(3, "bmw", "audi", Important),
    Note(4, "bugati", "lambo", Important),
    Note(5, "mustang", "ferrari", Important))
    private val notesDao : NotesDao
    private var notesRepository : NotesRepository
    init {
        notesDao = NotesRoomDatabase.getInstance(application.applicationContext).noteDao()
        notesRepository = NotesRepository(notesDao)
        addAllNotes(tempNotes)
        notes = notesRepository.allNotes
        impNotes = notesRepository.impNotes
    }

    private fun addAllNotes(noteList: List<Note>) {
        notesRepository.addAllNotes(noteList)
    }

    fun addNote(note: Note) {
        notesRepository.addNote(note)
    }

    fun updateNote(note: Note) {
        notesRepository.updateNote(note)
    }

    fun getImpNotes() {
        impNotes = notesDao.getImpNotes(Important) as MutableLiveData<List<Note>>
    }

    fun deleteSelectedItems() {
        viewModelScope.launch (Dispatchers.IO) {
            selectedItems.map {
                notesDao.deleteNote(it)
            }
            selectedItems = mutableListOf()
        }
    }

}