package com.fititu.logoquizitu.Model.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GlobalProfileEntity(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val hintsCount: Int,
    val hintsUsedCount: Int,
    val gameTime: Long // Duration in seconds
)
