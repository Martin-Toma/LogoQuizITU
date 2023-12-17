package com.fititu.logoquizitu.ViewModels
//Author: Ján Špaček (xspace39)

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class RandomNameViewModel(application: Application): AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext
    lateinit var randomCompanyNames: List<CompanyEntity>
    private var companyDao: CompanyDao = AppDatabase.getInstance(appContext).companyDao()
    private lateinit var correctName: String
    fun getRandomLogos() {
        randomCompanyNames = runBlocking(Dispatchers.IO) {
            companyDao.getRandomCompanies(4)
        }
    }
    fun checkAnswer(answer: String): Boolean {
        return answer == correctName
    }
    fun setCorrectName(){
        correctName = randomCompanyNames[0].companyName
    }
    fun shuffleCompanyNames(){
        randomCompanyNames = randomCompanyNames.shuffled()
    }
}