package com.fititu.logoquizitu

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.ViewModels.RandomNameViewModel
import kotlinx.coroutines.*


class RandomNameFragment : Fragment() {
    private var logoNamesButtons = mutableListOf<Button>()
    private lateinit var randomLogoImageView: ImageView
    private lateinit var viewModel: RandomNameViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RandomNameViewModel::class.java]
        randomLogoImageView = view.findViewById(R.id.randomLogoImage)
        viewModel.getRandomLogos()
        setImage()
        val logoNamesGridLayout: GridLayout = view.findViewById(R.id.LogoNamesGridLayout)
        generateLogoNameButtons(logoNamesGridLayout)
        updateHomeButton()
    }

    private fun setImage() {
        lifecycleScope.launch {
            var pathUI: String?
            withContext(Dispatchers.Main) {
                val path = viewModel.randomCompanyNames[0].imgOriginal
                pathUI = path
            }
            Glide.with(requireContext())
                .load(pathUI)
                .into(randomLogoImageView)
        }
    }

    private fun generateLogoNameButtons(gridLayout: GridLayout) {
        gridLayout.rowCount = 4
        gridLayout.columnCount = 1
        viewModel.setCorrectName()
        viewModel.shuffleCompanyNames()
        for (i in 0..3) {
            val button = Button(requireContext())
            val params = GridLayout.LayoutParams()
            button.text = viewModel.randomCompanyNames[i].companyName
            button.setBackgroundColor(Color.WHITE)
            button.setOnClickListener {
                onButtonClicked(button)
            }
            button.setTextColor(Color.BLACK)
            button.setPadding(30, 10, 30, 10)
            params.rowSpec = GridLayout.spec(i)
            params.columnSpec = GridLayout.spec(0)
            params.setMargins(10, 10, 10, 10)
            button.layoutParams = params
            params.setGravity(Gravity.CENTER_HORIZONTAL) // Center horizontally within the cell
            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_white)
            gridLayout.addView(button)
            logoNamesButtons.add(button)

        }
    }

    private fun onButtonClicked(button: Button) {
        if (button.text == viewModel.correctName) {
            for (currentButton in logoNamesButtons) {
                if (currentButton.text == viewModel.correctName) {
                    currentButton.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_green)
                }
            }
        } else {
            for (currentButton in logoNamesButtons) {
                if (currentButton.text == viewModel.correctName)
                    currentButton.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_green)
                else
                    currentButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)
            }
        }
        changeFragmentAfterDelay()
    }

    private fun changeFragmentAfterDelay() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = RandomNameFragment()
            fragmentTransaction.replace(R.id.mainMenuFragmentContainer, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun updateHomeButton() {
        val homeButton = view?.findViewById<Button>(R.id.HomeButton)
        homeButton?.text = "HOME"
        homeButton?.setOnClickListener {
            returnToMainMenu()
        }
    }

    private fun returnToMainMenu() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val newFragment = MainMenuFragment()
        fragmentTransaction.replace(R.id.mainMenuFragmentContainer, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_random_name, container, false)
    }

}