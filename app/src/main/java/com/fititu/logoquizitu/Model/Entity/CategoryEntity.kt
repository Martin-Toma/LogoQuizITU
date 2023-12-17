package com.fititu.logoquizitu.Model.Entity
// Author: Ondřej Vrána (xvrana32)

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false) val name : String,
    val imgCategory: Int,
)
{
    // Ignored fields
    @Ignore val imgCategoryBitmap: Bitmap? = null
}
