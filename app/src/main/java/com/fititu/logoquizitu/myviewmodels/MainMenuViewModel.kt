package com.fititu.logoquizitu.myviewmodels

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.fititu.logoquizitu.AddLogoFragment
import com.fititu.logoquizitu.FragmentConstants
import com.fititu.logoquizitu.GalleryFragment
import com.fititu.logoquizitu.MainMenuFragment
import com.fititu.logoquizitu.Model.SortBy
import com.fititu.logoquizitu.MyLogosFragment
import com.fititu.logoquizitu.SelectModeFragment
import com.fititu.logoquizitu.SettingsFragment
import com.fititu.logoquizitu.StatisticsFragment
import com.fititu.logoquizitu.View.IMainMenuView

class MainMenuViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext

    fun navigateTo(view: IMainMenuView, fragmentTo: String) {
        var fragment: Fragment? = null
        fragment = when (fragmentTo) {
            "toAdd" -> {
                AddLogoFragment()
            }
            FragmentConstants.TO_SELECT_MODE -> {
                SelectModeFragment()
            }
            FragmentConstants.TO_SETTINGS -> {
                SettingsFragment()
            }
            FragmentConstants.TO_GALLERY -> {
                GalleryFragment(null, null, null, SortBy.LEVEL)
            }
            FragmentConstants.TO_STATISTICS -> {
                StatisticsFragment()
            }
            FragmentConstants.TO_MY_LOGOS -> {
                MyLogosFragment()
            }
            FragmentConstants.TO_MAIN_MENU -> {
                MainMenuFragment()
            }
            else -> {
                null
            }
        }
        view.changeView(fragment = fragment!!)
    }
}