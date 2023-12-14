package com.fititu.logoquizitu.Model.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fititu.logoquizitu.Model.Entity.GlobalProfileEntity

@Dao
interface GlobalProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(globalProfileEntity: GlobalProfileEntity)

    @Update
    fun update(globalProfileEntity: GlobalProfileEntity)

    @Delete
    suspend fun delete(globalProfileEntity: GlobalProfileEntity)

    @Transaction
    @Query("SELECT * FROM GlobalProfileEntity LIMIT 1") // there should be only one profile
    suspend fun get() : List<GlobalProfileEntity>

}