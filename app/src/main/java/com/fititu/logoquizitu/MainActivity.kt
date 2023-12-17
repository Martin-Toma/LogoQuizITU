package com.fititu.logoquizitu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.DbConstants
import com.fititu.logoquizitu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentManager : FragmentManager // manages the fragment
    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)  // fetch the fragment
        setContentView(binding.root)

        switchFragment(MainMenuFragment())

//        this.deleteDatabase(DbConstants.DB_NAME)
        AppDatabase.initDb(this, lifecycleScope)
    }

    private fun switchFragment(fragment: Fragment){
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.mainMenuFragmentContainer, fragment).commit()
    }
}