package com.fititu.logoquizitu.myviewmodels

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.fititu.logoquizitu.FragmentConstants
import com.fititu.logoquizitu.GameRandom
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.LevelDao
import com.fititu.logoquizitu.Model.Entity.Relation.LevelWithCompanies
import com.fititu.logoquizitu.RandomNameFragment
import com.fititu.logoquizitu.SelectCategoryFragment
import com.fititu.logoquizitu.SelectLevelFragment
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