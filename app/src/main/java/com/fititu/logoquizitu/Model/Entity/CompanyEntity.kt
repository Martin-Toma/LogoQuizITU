package com.fititu.logoquizitu.Model.Entity

import android.graphics.Bitmap
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class CompanyEntity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val imgAltered : String,
    val imgOriginal : String,
    val solved : Boolean,
    val companyName: String,
    val companyDescription : String,

    val foundationDate : Date,

    val userCreated : Boolean,
    @Embedded val gameState : GameState?,

    // FKs
    val countryOfOriginName : String?,
    val categoryName : String?,
    val levelId : Int?,
)
{
    // Ignored fields
    @Ignore val imgAlteredBitmap: Bitmap? = null
    @Ignore val imgOriginalBitmap: Bitmap? = null
}

data class GameState(
    val myGuessStr : String,
    val optionCharsOrig : String
)



