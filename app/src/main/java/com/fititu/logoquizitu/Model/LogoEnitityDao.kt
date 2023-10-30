package com.fititu.logoquizitu.Model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LogoEntityDao {
    @Insert
    suspend fun insert(photoPost: LogoEntity)

    @Query("SELECT * FROM logo_entity")
    fun getAllPhotoPosts(): List<LogoEntity>
}