package com.fititu.logoquizitu.ViewModels
// Author: Ondřej Vrána (xvrana32)

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.fititu.logoquizitu.GameRandom
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CategoryDao
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Dao.CountryDao
import com.fititu.logoquizitu.Model.Dao.LevelDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.SortBy
import com.fititu.logoquizitu.View.IGalleryView
import kotlinx.coroutines.runBlocking

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext
    lateinit var companies: List<CompanyEntity>
    private lateinit var companyDao: CompanyDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var levelDao: LevelDao
    private lateinit var countryDao: CountryDao

    lateinit var categoryNames: List<String>
    lateinit var countryNames: List<String>
    lateinit var levels: List<String>

    var countryOfOrigin : String? = null
        set(value) {
            field = value
            // TODO save to globalProfile
        }
    var category : String? = null
        set(value) {
            field = value
            // TODO save to globalProfile
        }
    var level : String? = null
        set(value) {
            field = value
            // TODO save to globalProfile
        }
    var sortBy : SortBy = SortBy.LEVEL
        set(value) {
            field = value
            // TODO save to globalProfile
        }

    fun getFilterLists(){
        categoryDao = AppDatabase.getInstance(appContext).categoryDao()
        levelDao = AppDatabase.getInstance(appContext).levelDao()
        countryDao = AppDatabase.getInstance(appContext).countryDao()
        runBlocking {
            val tmpCategoryNames = categoryDao.getCategoryNames().toMutableList()
            tmpCategoryNames.add(0, "Any")
            categoryNames = tmpCategoryNames


            val tmpCountryNames = countryDao.getCountryNames().toMutableList()
            tmpCountryNames.add(0, "Any")
            countryNames = tmpCountryNames

            val tmpLevels = levelDao.getLevelIds()
            val stringLevels : MutableList<String> = mutableListOf()
            stringLevels.add("Any")
            tmpLevels.forEach {
                stringLevels.add(it.toString())
            }
            levels = stringLevels
        }
    }

    fun setFilters(_countryOfOrigin : String?, _category : String?, _level : String?, _sortBy : SortBy)
    {
        countryOfOrigin = _countryOfOrigin
        category = _category
        level = _level
        sortBy = _sortBy
    }

    fun getCompanies() {
        companyDao = AppDatabase.getInstance(appContext).companyDao()
        runBlocking {
            if (countryOfOrigin == null || countryOfOrigin == "Any"){
                if (category == null || category == "Any") {
                    if (level == null || level == "Any"){
                        companies = companyDao.getAll()
                    }
                    else{
                        companies = companyDao.getCompaniesOfLevel(level!!.toInt())
                    }
                }
                else{
                    if (level == null|| level == "Any"){
                        companies = companyDao.getCompaniesOfCategory(category!!)
                    }
                    else{
                        companies = companyDao.getCompaniesOfLevelAndCategory(level!!.toInt(), category!!)
                    }
                }
            }
            else{
                if (category == null || category == "Any") {
                    if (level == null|| level == "Any"){
                        companies = companyDao.getCompaniesOfCountry(countryOfOrigin!!)
                    }
                    else{
                        companies = companyDao.getCompaniesOfLevelAndCountry(level!!.toInt(),
                            countryOfOrigin!!
                        )
                    }
                }
                else{
                    if (level == null|| level == "Any"){
                        companies = companyDao.getCompaniesOfCategoryAndCountry(countryOfOrigin!!,
                            category!!
                        )
                    }
                    else{
                        companies = companyDao.getCompaniesAllFilters(level!!.toInt(),
                            countryOfOrigin!!, category!!
                        )
                    }
                }
            }

            companies = when (sortBy){
                SortBy.ALPHABETICALLY -> {
                    companies.sortedBy { it.companyName }
                }

                SortBy.LEVEL -> {
                    companies.sortedBy { it.levelId }
                }

                SortBy.AGE -> {
                    companies.sortedBy { it.foundationDate.time }
                }
            }
        }
    }

    // remake to open new game
    fun navigateTo(view: IGalleryView) {
        val gameRandomFragment = GameRandom()
        val bundle = Bundle().apply {
            if (level != null || level != "any"){
                putString("GameMode", "Levels:")
                putString("GameModeParameter", level)
            }
            else if (category != null || category != "any"){
                putString("GameMode", "Levels:")
                putString("GameModeParameter", category)
            }
            else{
                putString("GameMode", "GameRandom")
                putString("GameModeParameter", "")
            }
        }
        gameRandomFragment.arguments = bundle

        view.navigateTo(fragment = gameRandomFragment)
    }
}