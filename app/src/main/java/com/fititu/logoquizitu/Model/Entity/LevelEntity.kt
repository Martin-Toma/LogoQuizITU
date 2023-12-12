package com.fititu.logoquizitu.Model.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LevelEntity(
    @PrimaryKey(autoGenerate = false) val id : Int
)
