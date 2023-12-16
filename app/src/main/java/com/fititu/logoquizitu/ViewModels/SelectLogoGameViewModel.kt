package com.fititu.logoquizitu.ViewModels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectLogoGameViewModel (application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val appContext: Context = application.applicationContext
    val logoEntityDao : CompanyDao = AppDatabase.getInstance(appContext).companyDao()

    private val _randomLogos = MutableLiveData<List<CompanyEntity>>()
    val randomLogos: LiveData<List<CompanyEntity>> get() = _randomLogos

    fun initGame() {
        viewModelScope.launch(Dispatchers.IO) {
            val logos = logoEntityDao.getRandomLogos()
            _randomLogos.postValue(logos)
        }
    }


}