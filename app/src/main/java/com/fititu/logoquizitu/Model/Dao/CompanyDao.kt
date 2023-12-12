package com.fititu.logoquizitu.Model.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fititu.logoquizitu.Model.Entity.CompanyEntity

@Dao
interface CompanyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(companyEntity: CompanyEntity)

    @Update
    suspend fun update(companyEntity: CompanyEntity)

    @Delete
    suspend fun delete(companyEntity: CompanyEntity)

    @Query("SELECT * FROM CompanyEntity")
    suspend fun getAll() : List<CompanyEntity>

    @Query("SELECT * FROM CompanyEntity WHERE levelId = :level")
    suspend fun getCompaniesOfLevel(level : Int) : List<CompanyEntity>

    @Query("SELECT * FROM CompanyEntity WHERE categoryName = :categoryName")
    suspend fun getCompaniesOfCategory(categoryName: String) : List<CompanyEntity>

    @Query("SELECT * FROM CompanyEntity WHERE countryOfOriginName = :countryName")
    suspend fun getCompaniesOfCountry(countryName : String) : List<CompanyEntity>

    // for more special filters do set operations on lists returned by these
    // for sorting do sort on list returned by these
}