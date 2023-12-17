package com.fititu.logoquizitu.Model

import androidx.fragment.app.Fragment
import com.fititu.logoquizitu.*

class MainMenuModel(): IMainMenuModel {
    override fun nextFragment(where: String): Fragment? {
        var fragment: Fragment? = null
        when (where){
//            "toModeMenu" -> {
//                 fragment = ModeMenuFragment()
//            }
//            "toMyLogos" -> {
//                fragment = MyLogosFragment()
//            }
            "toAdd" -> {
                fragment = AddLogoFragment()
            }
//            "toGameRandom" -> {
//                fragment = GameRandom()
//            }
//            "toRandomName" -> {
//                fragment = RandomNameFragment()
//            }
//            "toCreateLogo" -> {
//                fragment = CreateLogoFragment()
//            }
            // main menu navigation
            FragmentConstants.TO_SELECT_MODE -> {
                fragment = SelectModeFragment()
            }
            FragmentConstants.TO_SETTINGS -> {
                fragment = SettingsFragment()
            }
            FragmentConstants.TO_GALLERY -> {
                fragment = GalleryFragment(null, null, null, SortBy.LEVEL)
            }
            FragmentConstants.TO_STATISTICS -> {
                fragment = StatisticsFragment()
            }
            FragmentConstants.TO_MY_LOGOS -> {
                fragment = MyLogosFragment()
            }
            FragmentConstants.TO_MAIN_MENU -> {
                fragment = MainMenuFragment()
            }

            // select mode navigation
            FragmentConstants.TO_RANDOM ->{
                fragment = GameRandom()
            }
            FragmentConstants.TO_LEVEL ->{
                fragment = SelectLevelFragment()
            }
            FragmentConstants.TO_SELECT ->{
                fragment = TODO()
            }
            FragmentConstants.TO_NAME ->{
                fragment = RandomNameFragment()
            }
            FragmentConstants.TO_CATEGORY ->{
                fragment = SelectCategoryFragment()
            }
            FragmentConstants.TO_MY_LOGOS ->{
                fragment = TODO()
            }
        }
        return fragment
    }
    override fun nextFragmentWithParam(where: String, id: Int): Fragment? {
        var fragment: Fragment? = null
        when (where){
            "toAdd" -> {
                fragment = AddLogoFragment.newInstance(id)
            }
        }
        return fragment
    }
}