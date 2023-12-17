package com.fititu.logoquizitu.ViewModels

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.fititu.logoquizitu.FragmentConstants
import com.fititu.logoquizitu.GalleryFragment
import com.fititu.logoquizitu.GameRandom
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CategoryDao
import com.fititu.logoquizitu.Model.Dao.LevelDao
import com.fititu.logoquizitu.Model.Entity.Relation.CategoryWithCompanies
import com.fititu.logoquizitu.Model.Entity.Relation.LevelWithCompanies
import com.fititu.logoquizitu.Model.SortBy
import com.fititu.logoquizitu.RandomNameFragment
import com.fititu.logoquizitu.SelectCategoryFragment
import com.fititu.logoquizitu.SelectLevelFragment
import com.fititu.logoquizitu.View.ICategoryView
import com.fititu.logoquizitu.View.ILevelView
import com.fititu.logoquizitu.View.ISelectModeView
import kotlinx.coroutines.runBlocking

class SelectCategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext
    lateinit var categories: List<CategoryWithCompanies>
    private lateinit var categoryDao: CategoryDao

    fun getCategories() {
        categoryDao = AppDatabase.getInstance(appContext).categoryDao()
        runBlocking {
            categories = categoryDao.getCategoriesWithCompanies()
        }
    }

    fun navigateToCategory(view: ICategoryView, categoryName: String) {
        val fragment = GalleryFragment(null, categoryName, null, SortBy.LEVEL, true)
        view.navigateToCategory(fragment = fragment)
    }
}