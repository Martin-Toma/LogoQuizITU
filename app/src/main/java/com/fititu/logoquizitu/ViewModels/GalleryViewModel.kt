package com.fititu.logoquizitu.ViewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.SortBy
import kotlinx.coroutines.runBlocking

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext
    lateinit var companies: List<CompanyEntity>
    private lateinit var companyDao: CompanyDao

    var countryOfOrigin : String? = null
    var category : String? = null
    var level : Int? = null
    var sortBy : SortBy = SortBy.LEVEL

    fun setFilters(_countryOfOrigin : String?, _category : String?, _level : Int?, _sortBy : SortBy)
    {
        countryOfOrigin = _countryOfOrigin
        category = _category
        level = _level
        sortBy = _sortBy
    }

    fun getCompanies() {
        companyDao = AppDatabase.getInstance(appContext).companyDao()
        runBlocking {
            if (countryOfOrigin == null){
                if (category == null) {
                    if (level == null){
                        companies = companyDao.getAll()
                    }
                    else{
                        companies = companyDao.getCompaniesOfLevel(level!!)
                    }
                }
                else{
                    if (level == null){
                        companies = companyDao.getCompaniesOfCategory(category!!)
                    }
                    else{
                        companies = companyDao.getCompaniesOfLevelAndCategory(level!!, category!!)
                    }
                }
            }
            else{
                if (category == null) {
                    if (level == null){
                        companies = companyDao.getCompaniesOfCountry(countryOfOrigin!!)
                    }
                    else{
                        companies = companyDao.getCompaniesOfLevelAndCountry(level!!,
                            countryOfOrigin!!
                        )
                    }
                }
                else{
                    if (level == null){
                        companies = companyDao.getCompaniesOfCategoryAndCountry(countryOfOrigin!!,
                            category!!
                        )
                    }
                    else{
                        companies = companyDao.getCompaniesAllFilters(level!!,
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
//    fun navigateTo(view: ISelectModeView, fragmentTo: String) {
//        var fragment: Fragment? = null
//        fragment = when (fragmentTo) {
//            FragmentConstants.TO_RANDOM -> {
//                GameRandom()
//            }
//
//            FragmentConstants.TO_LEVEL -> {
//                SelectLevelFragment()
//            }
//
//            FragmentConstants.TO_SELECT -> {
//                TODO()
//            }
//
//            FragmentConstants.TO_NAME -> {
//                RandomNameFragment()
//            }
//
//            FragmentConstants.TO_CATEGORY -> {
//                SelectCategoryFragment()
//            }
//
//            FragmentConstants.TO_PLAY_MY_LOGOS -> {
//                TODO()
//            }
//
//            else -> {
//                null
//            }
//        }
//        view.changeView(fragment = fragment!!)
//    }
}