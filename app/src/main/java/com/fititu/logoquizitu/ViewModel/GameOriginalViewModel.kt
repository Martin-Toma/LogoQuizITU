package com.fititu.logoquizitu.ViewModel
import androidx.lifecycle.ViewModel
import com.fititu.logoquizitu.Model.LogoEntity

class GameOriginalViewModel : ViewModel (){
    var logoEntity: LogoEntity? = null
    fun setLogo(logoEntity: LogoEntity?){
        this.logoEntity = logoEntity
    }
}