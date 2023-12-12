package com.fititu.logoquizitu.Model.Entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false) val name : String,
    val imgCategory: String,
)
{
    // Ignored fields
    @Ignore val imgCategoryBitmap: Bitmap? = null
}
