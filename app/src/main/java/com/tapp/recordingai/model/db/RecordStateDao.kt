package com.tapp.recordingai.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecordStateDao {

    @Query("SELECT * FROM RecordState WHERE id = 0 LIMIT 1")
    suspend fun getText(): RecordState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveText(state: RecordState)

    @Query("DELETE FROM RecordState WHERE id = 0")
    suspend fun clearText()
}