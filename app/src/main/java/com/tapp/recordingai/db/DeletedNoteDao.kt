package com.tapp.recordingai.db

import androidx.room.*

@Dao
interface DeletedNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: DeletedNote)

    @Delete
    suspend fun delete(note: DeletedNote)

    @Query("SELECT * FROM deleted_notes ORDER BY deletedAt DESC")
    suspend fun getAll(): List<DeletedNote>

    @Query("DELETE FROM deleted_notes WHERE deletedAt < :timeLimit")
    suspend fun deleteOlderThan(timeLimit: Long)
}
