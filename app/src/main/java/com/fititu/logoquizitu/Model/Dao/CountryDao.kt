package com.fititu.logoquizitu.Model.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fititu.logoquizitu.Model.Entity.CountryEntity
import com.fititu.logoquizitu.Model.Entity.Relation.CountryWithCompanies

@Dao
interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(countryEntity: CountryEntity)

    @Delete
    suspend fun delete(countryEntity: CountryEntity)

    @Transaction
    @Query("SELECT * FROM CountryEntity")
    suspend fun getCountriesWithCompanies() : List<CountryWithCompanies>

    @Query("SELECT name FROM CountryEntity")
    suspend fun getCountryNames() : List<String>
}