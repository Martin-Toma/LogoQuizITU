package com.fititu.logoquizitu.myviewmodels

import android.app.Application
import android.content.Context
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.fititu.logoquizitu.FragmentConstants
import com.fititu.logoquizitu.GameRandom
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CategoryDao
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Dao.GlobalProfileDao
import com.fititu.logoquizitu.Model.Entity.GlobalProfileEntity
import com.fititu.logoquizitu.RandomNameFragment
import com.fititu.logoquizitu.SelectCategoryFragment
import com.fititu.logoquizitu.SelectLevelFragment
import com.fititu.logoquizitu.View.ISelectModeView
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext
    private lateinit var profileDao: GlobalProfileDao
    private lateinit var companyDao: CompanyDao

    var duration: Duration? = null
    lateinit var profile: GlobalProfileEntity
    var solvedCount: Int = 0
    var remainingCount: Int = 0

    fun getStatistics() {
        profileDao = AppDatabase.getInstance(appContext).globalProfileDao()
        companyDao = AppDatabase.getInstance(appContext).companyDao()

        runBlocking {
            profile = profileDao.get()[0]
            val companies = companyDao.getAll()
            duration = profile.gameTime.toDuration(DurationUnit.SECONDS)

            companies.forEach {
                if (it.solved) solvedCount++ else remainingCount++
            }
        }
    }
}