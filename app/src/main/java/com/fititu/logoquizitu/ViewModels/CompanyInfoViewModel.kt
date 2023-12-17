package com.fititu.logoquizitu.ViewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class CompanyInfoViewModel(application: Application): AndroidViewModel(application) {
    private var companyId: Int = 0
    lateinit var gameMode: String
    private val appContext: Context = application.applicationContext
    private var companyDao : CompanyDao = AppDatabase.getInstance(appContext).companyDao()
    lateinit var company : CompanyEntity
    lateinit var gameRandomMode: String
    fun setParameters(companyId:Int, gameMode:String, gameRandomMode:String){
        this.companyId = companyId
        this.gameMode = gameMode
        this.gameRandomMode = gameRandomMode
    }
    fun retrieveCompany() {
        company = runBlocking(Dispatchers.IO){
            companyDao.getCompanyById(companyId)
        }
    }
}