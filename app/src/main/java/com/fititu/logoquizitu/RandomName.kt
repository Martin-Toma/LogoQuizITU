package com.fititu.logoquizitu

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import kotlinx.coroutines.*
import java.io.File


class RandomNameFragment : Fragment() {
    private var LogoNamesButtons = mutableListOf<Button>()
    private lateinit var RandomCompanyNames: List<CompanyEntity>
    private lateinit var CompanyDao: CompanyDao
    private lateinit var randomLogoImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CompanyDao = AppDatabase.getInstance(requireContext()).companyDao()
        randomLogoImageView = view.findViewById(R.id.randomLogoImage)
        getRandomLogos()
        val logoNamesGridLayout: GridLayout = view.findViewById(R.id.LogoNamesGridLayout)
        GenerateLogoNameButtons(logoNamesGridLayout)

    }

    private fun getRandomLogos(){
        RandomCompanyNames = runBlocking(Dispatchers.IO){
            CompanyDao.getRandomCompanies()
        }
        lifecycleScope.launch {
            var path_UI: String?
            withContext(Dispatchers.Main) {
                val path = RandomCompanyNames[0].imgOriginal//randomLogo2.imagePath
                path_UI = path

                Log.d("Image Loading", "other ${Uri.parse(path)}")
            }
            Glide.with(requireContext())
                .load(path_UI)
                .into(randomLogoImageView)
        }
    }
    private fun GenerateLogoNameButtons(gridLayout: GridLayout/*logo entities*/){
        gridLayout.rowCount = 4
        gridLayout.columnCount = 1

        for(i in 0..3){
            //todo generate 4 buttons with the 4 logo names
            val button = Button(requireContext())
            val params = GridLayout.LayoutParams()

            button.text = RandomCompanyNames[i].companyName
            button.setBackgroundColor(Color.WHITE)
            button.setOnClickListener {
                onButtonClicked(button)
            }
            button.setTextColor(Color.BLACK)
            button.setPadding(30, 10, 30, 10)
            params.rowSpec =GridLayout.spec(i)
            params.columnSpec = GridLayout.spec(0)
            params.setMargins(10, 10, 10, 10)
            button.layoutParams = params
            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_white)
            gridLayout.addView(button)
            LogoNamesButtons.add(button)

        }
    }
    private fun onButtonClicked(button: Button){
        if(button.text == RandomCompanyNames[0].companyName){
            for (button in LogoNamesButtons){
                if(button.text == RandomCompanyNames[0].companyName){
                    button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_green)
                }
            }
        }
        else{
            for (button in LogoNamesButtons){
                if(button.text == RandomCompanyNames[0].companyName){
                    button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_green)
                }
                else{
                    button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)

                }
            }
        }
        changeFragmentAfterDelay()

    }
    private fun changeFragmentAfterDelay(){
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            // Replace the current fragment with the new one
            val newFragment = RandomNameFragment()
            fragmentTransaction.replace(R.id.mainMenuFragmentContainer, newFragment)
            fragmentTransaction.addToBackStack(null) // Optional: Add to back stack for back navigation
            fragmentTransaction.commit()//navigate to next fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_random_name, container, false)
    }

}