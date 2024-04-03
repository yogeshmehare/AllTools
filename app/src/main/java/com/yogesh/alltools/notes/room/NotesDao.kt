package com.yogesh.alltools.notes.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note: Note)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun findNoteById(noteId: Int): Note

    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<Note>>

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes WHERE type = :type")
    fun getImpNotes(type:String): LiveData<List<Note>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAllNotes(noteList: List<Note>)
}