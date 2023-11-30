package com.fititu.logoquizitu.Model

import androidx.fragment.app.Fragment
import com.fititu.logoquizitu.*

class MainMenuModel(): IMainMenuModel {
    override fun nextFragment(where: String): Fragment? {
        var fragment: Fragment? = null
        when (where){
            "toModeMenu" -> {
                 fragment = ModeMenuFragment()
            }
            "toMyLogos" -> {
                fragment = MyLogosFragment()
            }
            "toAdd" -> {
                fragment = AddLogoFragment()
            }
            "toGameRandom" -> {
                fragment = GameRandomFragment()
            }
            "toRandomName" -> {
                fragment = RandomNameFragment()
            }
        }
        return fragment
    }
}