package com.fititu.logoquizitu.myviewmodels

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.fititu.logoquizitu.FragmentConstants
import com.fititu.logoquizitu.GalleryFragment
import com.fititu.logoquizitu.GameRandom
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.LevelDao
import com.fititu.logoquizitu.Model.Entity.Relation.LevelWithCompanies
import com.fititu.logoquizitu.Model.SortBy
import com.fititu.logoquizitu.RandomNameFragment
import com.fititu.logoquizitu.SelectCategoryFragment
import com.fititu.logoquizitu.SelectLevelFragment
import com.fititu.logoquizitu.View.ILevelView
import com.fititu.logoquizitu.View.ISelectModeView
import kotlinx.coroutines.runBlocking

class SelectLevelViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext
    lateinit var levels: List<LevelWithCompanies>
    private lateinit var levelDao: LevelDao

    fun getLevels() {
        levelDao = AppDatabase.getInstance(appContext).levelDao()
        runBlocking {
            levels = levelDao.getLevelsWithCompanies()
        }
    }

    fun navigateToLevel(view: ILevelView, level: Int) {
        val fragment = GalleryFragment(null, null, level, SortBy.LEVEL)
        view.navigateToLevel(fragment = fragment)
    }
}