package com.fititu.logoquizitu.Controller

import com.fititu.logoquizitu.Model.Entity.CompanyEntity

interface IMainMenuController {
    fun onClickButton(where:String)

    fun editDbFragment(where: String, entityId: Int)
}