package com.fititu.logoquizitu.ViewModels

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.fititu.logoquizitu.FragmentConstants
import com.fititu.logoquizitu.GameRandom
import com.fititu.logoquizitu.RandomNameFragment
import com.fititu.logoquizitu.SelectCategoryFragment
import com.fititu.logoquizitu.SelectLevelFragment
import com.fititu.logoquizitu.SelectLogoGameFragment
import com.fititu.logoquizitu.View.ISelectModeView

class SelectModeViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext

    fun navigateTo(view: ISelectModeView, fragmentTo: String) {
        var fragment: Fragment? = null
        fragment = when (fragmentTo) {
            FragmentConstants.TO_RANDOM ->{
                val gameRandomFragment = GameRandom()
                val bundle = Bundle().apply {
                    putString("GameMode", "GameRandom")
                    putString("GameModeParameter", "")
                }
                gameRandomFragment.arguments = bundle
                gameRandomFragment
            }
            FragmentConstants.TO_LEVEL ->{
                SelectLevelFragment()
            }
            FragmentConstants.TO_SELECT ->{
                SelectLogoGameFragment()
            }
            FragmentConstants.TO_NAME ->{
                RandomNameFragment()
            }
            FragmentConstants.TO_CATEGORY ->{
                SelectCategoryFragment()
            }
            FragmentConstants.TO_PLAY_MY_LOGOS ->{
                TODO()
            }
            else -> {
                null
            }
        }
        view.changeView(fragment = fragment!!)
    }
}