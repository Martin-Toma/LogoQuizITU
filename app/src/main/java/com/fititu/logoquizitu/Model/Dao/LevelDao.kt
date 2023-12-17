package com.fititu.logoquizitu.Model.Dao
// Author: Ondřej Vrána (xvrana32)

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fititu.logoquizitu.Model.Entity.LevelEntity
import com.fititu.logoquizitu.Model.Entity.Relation.LevelWithCompanies

@Dao
interface LevelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(levelEntity: LevelEntity)

    @Delete
    suspend fun delete(levelEntity: LevelEntity)

    @Transaction
    @Query("SELECT * FROM LevelEntity")
    suspend fun getLevelsWithCompanies() : List<LevelWithCompanies>

    @Query("SELECT id FROM LevelEntity")
    suspend fun getLevelIds() : List<Int>
}