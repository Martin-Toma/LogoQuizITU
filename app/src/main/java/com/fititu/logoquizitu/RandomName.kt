package com.fititu.logoquizitu

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class RandomNameFragment : Fragment() {
    private var LogoNamesButtons = mutableListOf<Button>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo 1. Access the Room database and fetch a random LogoEntity
        //todo fetch 3 more entities
        //display the 4 entity names in the 4 buttons
        //for now, use mock data

        // Access the Room database and fetch a random LogoEntity
        val logoEntityDao = AppDatabase.getInstance(requireContext()).logoEntityDao()
        val logoNamesGridLayout: GridLayout = view.findViewById(R.id.LogoNamesGridLayout)
        GenerateLogoNameButtons(logoNamesGridLayout)
        //    val randomLogoEntity = logoEntityDao.getRandomPhotoPost()

        // Check if a random logo entity is retrieved
        //   randomLogoEntity?.let {
        // If a logo is found, you can now display the photo
        //     displayLogoPhoto(it.imagePath)
        //  }
    }
    private fun GenerateLogoNameButtons(gridLayout: GridLayout/*logo entities*/){
        val array = arrayOf("Instagram","Google","Meta","Youtube")
        gridLayout.rowCount = 4
        gridLayout.columnCount = 1

        for(i in 0..3){
            //todo generate 4 buttons with the 4 logo names
            val button = Button(requireContext())
            val params = GridLayout.LayoutParams()

            button.text = array[i]
            button.setBackgroundColor(Color.WHITE)
            button.setOnClickListener {
                onButtonClicked(button,array[i])
            }
            button.setTextColor(Color.BLACK)
            params.rowSpec =GridLayout.spec(i)
            params.columnSpec = GridLayout.spec(0)
            params.setMargins(10, 10, 10, 10)
            button.layoutParams = params
            gridLayout.addView(button)
            LogoNamesButtons.add(button)

        }
    }
    private fun onButtonClicked(button: Button,name: String){
        if(name == "Meta"/*randomLogoEntity.name*/){
            for (button in LogoNamesButtons){
                if(button.text == "Meta"){
                    button.setBackgroundColor(Color.GREEN)
                }
            }
        }
        else{
            for (button in LogoNamesButtons){
                if(button.text == "Meta"){
                    button.setBackgroundColor(Color.GREEN)
                }
                else{
                    button.setBackgroundColor(Color.RED)
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
    private fun displayLogoPhoto(imagePath: String) {
        // Load and display the photo using an image loading library (e.g., Glide, Picasso)
        // Here, you can use any library or method of your choice to load and display the image
        // Note: Don't forget to handle permissions for external storage if needed
        // For simplicity, I'm assuming you have an ImageView in your layout with the id "imageView"
        val imageView: ImageView = requireView().findViewById(R.id.imageView)

        // Use an image loading library (e.g., Glide) to load and display the image
        /*Glide.with(this)
            .load(File(imagePath))
            .into(imageView)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_random_name, container, false)
    }

}