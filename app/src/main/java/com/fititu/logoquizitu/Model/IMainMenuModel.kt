package com.fititu.logoquizitu.Model

import androidx.fragment.app.Fragment

interface IMainMenuModel {
    fun nextFragment(where:String):Fragment?
    fun nextFragmentWithParam(where: String, id: Int): Fragment?
}