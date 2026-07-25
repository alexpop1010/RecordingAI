package com.tapp.recordingai.model.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FolderDao {
    @Insert
    suspend fun insert(folder: Folder): Long

    @Update
    suspend fun update(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Query("SELECT * FROM folders ORDER BY name COLLATE NOCASE ASC")
    suspend fun getAll(): List<Folder>

    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun getById(id: Int): Folder?
}
