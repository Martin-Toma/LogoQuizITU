package com.fititu.logoquizitu.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logo_entity")
data class LogoEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imagePath: String,
    val name: String,
    val description: String
)