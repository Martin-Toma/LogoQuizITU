package com.fititu.logoquizitu.ViewModels

import android.content.Context
import android.app.Application
import androidx.lifecycle.*
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Dao.GlobalProfileDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.Entity.GlobalProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class GameRandomViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext
    val companyDao: CompanyDao = AppDatabase.getInstance(appContext).companyDao()
    var globalProfileDao: GlobalProfileDao = AppDatabase.getInstance(appContext).globalProfileDao()
    lateinit var globalProfiles: List<GlobalProfileEntity>
    lateinit var globalProfile: GlobalProfileEntity
    lateinit var randomLogo: CompanyEntity
    private val _logoNull = MutableLiveData<Boolean>()
    val logoNull: LiveData<Boolean> get() = _logoNull


    var gameMode: String = ""
        set(value) {
            field = value

        }
    var gameModeParameter: String = ""
        set(value) {
            field = value

        }

    private fun triggerNavigation() {
        _logoNull.value = true
    }

    // Coroutine scope for background tasks
    fun getRandomLogo() {
        val randomLogoFromDb = runBlocking(Dispatchers.IO) {
            when (gameMode) {
                "GameRandom" -> companyDao.getRandomCompany()
                "Categories:" -> companyDao.getRandomCompanyOfCategory(gameModeParameter)
                "Levels:" -> companyDao.getRandomCompanyOfLevel(gameModeParameter.toInt())
                else -> companyDao.getRandomCompany()
            }
        }
        if (randomLogoFromDb != null) {
            randomLogo = randomLogoFromDb
            return
        }
        triggerNavigation()
    }

    fun getGlobalProfile() {
        globalProfiles = runBlocking(Dispatchers.IO) {
            globalProfileDao.get()
        }
        globalProfile = globalProfiles[0]
    }

    fun resetCurrentGameState() {
        runBlocking(Dispatchers.IO) {
            globalProfileDao.update(
                GlobalProfileEntity(
                    globalProfile.id,
                    globalProfile.hintsCount,
                    globalProfile.hintsUsedCount,
                    globalProfile.gameTime,
                    -1,
                    "",
                    "",
                    "",
                    ""
                )
            )
            globalProfiles = globalProfileDao.get()
        }
    }

    fun updateCompanySolved() { //todo should work, needs testing
        randomLogo.solved = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                companyDao.update(randomLogo)
            }
        }
    }

    fun saveStateToDb(
        logoName: String,
        logoColor: String,
        letterstring: String,
        nameColor: String,
        randomId: Int
    ) {
        runBlocking(Dispatchers.IO) {
            globalProfileDao.update(
                GlobalProfileEntity(
                    globalProfile.id,
                    globalProfile.hintsCount,
                    globalProfile.hintsUsedCount,
                    globalProfile.gameTime,
                    randomId, //todo change
                    logoName,
                    logoColor,
                    letterstring,
                    nameColor
                )
            )
            globalProfiles = globalProfileDao.get()
        }
        globalProfile = globalProfiles[0] //update globalProfile
    }

    fun getCompanyById() {
        randomLogo = runBlocking(Dispatchers.IO) {
            companyDao.getCompanyById(globalProfile.currentCompanyId)
        }
    }

}