package com.fititu.logoquizitu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.GlobalProfileDao
import com.fititu.logoquizitu.Model.DbConstants
import com.fititu.logoquizitu.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentManager : FragmentManager // manages the fragment
    private lateinit var binding : ActivityMainBinding
    private var timeStart : Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)  // fetch the fragment
        setContentView(binding.root)

        switchFragment(MainMenuFragment())

//        this.deleteDatabase(DbConstants.DB_NAME)
        AppDatabase.initDb(this, lifecycleScope)
    }

    override fun onPause() {
        val timePaused = System.currentTimeMillis()
        val timePassed = timePaused - timeStart

        val profileDao = AppDatabase.getInstance(this).globalProfileDao()
        lifecycleScope.launch {
            val profile = profileDao.get()[0]
            profile.gameTime += timePassed.toDuration(DurationUnit.MILLISECONDS).toLong(DurationUnit.SECONDS)
            profileDao.update(profile)
        }
        timeStart = timePaused
        super.onPause()
    }
    private fun switchFragment(fragment: Fragment){
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.mainMenuFragmentContainer, fragment).commit()
    }
}