package com.fititu.logoquizitu.Model

import androidx.fragment.app.Fragment
import com.fititu.logoquizitu.AddLogoFragment
import com.fititu.logoquizitu.GameRandom
import com.fititu.logoquizitu.ModeMenuFragment
import com.fititu.logoquizitu.MyLogosFragment

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
                fragment = GameRandom()
            }
        }
        return fragment
    }
}