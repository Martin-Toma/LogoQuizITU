package com.fititu.logoquizitu.Model.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.LogoEntity

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

    @Query("SELECT * FROM CompanyEntity")
    fun getAllCompanies() : List<CompanyEntity>

    @Query("SELECT * FROM CompanyEntity WHERE levelId = :level")
    suspend fun getCompaniesOfLevel(level : Int) : List<CompanyEntity>

    @Query("SELECT * FROM CompanyEntity WHERE levelId = :level ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomCompanyOfLevel(level : Int) : CompanyEntity

    @Query("SELECT * FROM CompanyEntity WHERE categoryName = :categoryName")
    suspend fun getCompaniesOfCategory(categoryName: String) : List<CompanyEntity>

    @Query("SELECT * FROM CompanyEntity WHERE categoryName = :categoryName ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomCompanyOfCategory(categoryName: String) : CompanyEntity
    @Query("SELECT * FROM CompanyEntity WHERE countryOfOriginName = :countryName")
    suspend fun getCompaniesOfCountry(countryName : String) : List<CompanyEntity>

    @Query("SELECT * FROM CompanyEntity/* WHERE solved=0 */ORDER BY RANDOM() LIMIT 1") //todo uncomment solved=0, this excludes solved companies
    suspend fun getRandomCompany() : CompanyEntity?

    @Query("SELECT * FROM CompanyEntity ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomCompanies(limit:Int) : List<CompanyEntity>

    @Query("SELECT * FROM CompanyEntity WHERE id = :id")
    suspend fun getCompanyById(id : Int) : CompanyEntity
    // for more special filters do set operations on lists returned by these
    // for sorting do sort on list returned by these

    @Insert
    suspend fun insert(photoPost: CompanyEntity)

    @Query("SELECT * FROM CompanyEntity")
    fun getAllPhotoPostsC(): List<CompanyEntity>

    @Query("SELECT * FROM CompanyEntity ORDER BY RANDOM() LIMIT 8")
    fun getRandomLogos(): List<CompanyEntity>
}