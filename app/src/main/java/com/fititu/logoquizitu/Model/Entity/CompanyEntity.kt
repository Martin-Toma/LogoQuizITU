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
    // for images from users
    var imgAltered : String,
    var imgOriginal : String,
    // for images from resources
    var imgAlteredRsc: Int,
    var imgOriginalRsc : Int,

    var solved : Boolean,
    var companyName: String,
    var companyDescription : String,

    var foundationDate : Date,

    val userCreated : Boolean,
    @Embedded var gameState : GameState?,

    // FKs
    var countryOfOriginName : String?,
    var categoryName : String?,
    var levelId : Int?,
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



