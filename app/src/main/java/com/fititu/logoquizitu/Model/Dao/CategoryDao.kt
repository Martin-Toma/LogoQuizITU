package com.fititu.logoquizitu.Model.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fititu.logoquizitu.Model.Entity.CategoryEntity
import com.fititu.logoquizitu.Model.Entity.Relation.CategoryWithCompanies

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(categoryEntity: CategoryEntity)

    @Delete
    suspend fun delete(categoryEntity: CategoryEntity)

    @Transaction
    @Query("SELECT * FROM CategoryEntity")
    suspend fun getCategoriesWithCompanies() : List<CategoryWithCompanies>

    @Query("SELECT name FROM CategoryEntity")
    suspend fun getCategoryNames() : List<String>
}