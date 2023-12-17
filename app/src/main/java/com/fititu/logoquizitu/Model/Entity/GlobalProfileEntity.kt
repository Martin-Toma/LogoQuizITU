package com.fititu.logoquizitu.Model.Entity
// Author: Ondřej Vrána (xvrana32) and Ján Špaček (xspace39)

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GlobalProfileEntity(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val hintsCount: Int,
    val hintsUsedCount: Int,
    var gameTime: Long, // Duration in seconds

    // ?
    val currentCompanyId : Int,
    val logoLetters : String,
    val logoColors : String,
    val letters : String,
    val letterColors : String,
)
