package com.fititu.logoquizitu.Controller

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.fititu.logoquizitu.ModeMenuFragment
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.MainMenuModel
import com.fititu.logoquizitu.R
import com.fititu.logoquizitu.View.IMainMenuView

class MainMenuController(
    private val menuView: IMainMenuView
) : IMainMenuController {

    override fun onClickButton(where: String) {
        val mainMenuModel = MainMenuModel()
        val newFragment = mainMenuModel.nextFragment(where)
        menuView.changeView(fragment = newFragment!!)
    }
    override fun editDbFragment(where: String, entityId: Int){
        val mainMenuModel = MainMenuModel()
        val newFragment = mainMenuModel.nextFragmentWithParam(where, entityId)
        menuView.changeViewWithParam(fragment = newFragment!!);
    }
}