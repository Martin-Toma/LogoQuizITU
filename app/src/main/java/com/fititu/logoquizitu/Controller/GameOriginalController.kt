package com.fititu.logoquizitu.Controller

import com.fititu.logoquizitu.GameRandomFragment
import com.fititu.logoquizitu.Model.LogoEntity

class GameOriginalController(private val logoEntity: LogoEntity, private val gameRandomFragment: GameRandomFragment) {
    init {
        displayLogo()
    }
    private fun displayLogo(){
        //gameRandomFragment.displayLogo(logoEntity.imagePath)
    }
}