package com.fititu.logoquizitu

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.LogoEntity
import com.fititu.logoquizitu.Model.LogoEntityDao
import com.fititu.logoquizitu.ViewModel.GameOriginalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min
import kotlin.random.Random

private val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
/**
 * A simple [Fragment] subclass.
 * Use the [GameRandomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameRandomFragment : Fragment() {
    private lateinit var gameOriginalViewModel: GameOriginalViewModel
    private lateinit var logoEntityDao: LogoEntityDao
    private lateinit var randomLogo: LogoEntity
    private var letters = mutableListOf<Letter>()
    private var letterButtons = mutableListOf<Button>()
    private var lettercount : Int = 8
    private var logoNameButtons = mutableListOf<Button>()
    private lateinit var currentLogoNameButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        logoEntityDao = AppDatabase.getInstance(requireContext()).logoEntityDao()
        return inflater.inflate(R.layout.fragment_game_random,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameOriginalViewModel = ViewModelProvider(requireActivity())[GameOriginalViewModel::class.java]
        getRandomLogo()
        val randomLogoImageView: ImageView = view.findViewById(R.id.logoImageView)
        // Load the random logo into the ImageView
        // Observe changes in the logoEntity LiveData
        gameOriginalViewModel.logoEntity?.let { logoEntity ->
            // Use an image loading library like Glide or Picasso to load the image
            // Replace "requireContext()" with "this" if not in a fragment
            //delay(500)
            Glide.with(randomLogoImageView.context)
                .load(logoEntity.imagePath)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(randomLogoImageView)
        }
        val logoNameGridLayout: GridLayout = view.findViewById(R.id.LogoNameGridLayout)
        calculateLetterCount("abcd")//todo change to logo name when it actually works
        //var columns = min( (randomLogo.name.length + 1) / 2,8)todo uncomment
        var columns = min((10+1)/2,8)
        if(lettercount <=8) {
            logoNameGridLayout.rowCount = 1
            logoNameGridLayout.columnCount = 5 //todo uncomment this randomLogo.name.length + 1
        }
        else{
            logoNameGridLayout.rowCount = 2
            logoNameGridLayout.columnCount = columns
        }
        addLogoLetterButtons(logoNameGridLayout)
        // create buttons for each letter in the logo name

        val lettersGridLayout: GridLayout = view.findViewById(R.id.lettersGridLayout)
        columns = min((lettercount+1)/2,8)
        lettersGridLayout.rowCount = 2
        lettersGridLayout.columnCount = columns
        // Add random letter buttons
        addRandomLetterButtons(lettersGridLayout)
    }
    fun getRandomLogo(){
        lifecycleScope.launch {
            randomLogo = withContext(Dispatchers.IO){
                logoEntityDao.getRandomPhotoPost()!!
            }
            withContext(Dispatchers.Main){
                gameOriginalViewModel.setLogo(randomLogo)
            }
        }
    }
    fun addLogoLetterButtons(gridLayout: GridLayout){
        for(i in 0 until 2){ //randomLogo.Name.Length
            val button = Button(requireContext())
            button.setOnClickListener {
                // Handle button click
                onLogoLetterButtonClick(button)
            }
            button.text = ""
            // Customize button appearance if needed
            button.setBackgroundColor(Color.WHITE)
            // Add button to GridLayout
            val params = GridLayout.LayoutParams()
            if(gridLayout.rowCount == 1)
                params.rowSpec = GridLayout.spec(0)
            else
                params.rowSpec = GridLayout.spec(i / gridLayout.columnCount)
            params.columnSpec = GridLayout.spec(i % gridLayout.columnCount)
            params.width = 100
            params.setMargins(10, 10, 10, 10)
            button.layoutParams = params
            button.isEnabled = false
            gridLayout.addView(button)

            logoNameButtons.add(button)
        }
        //add last button that is special
        val button = Button(requireContext())
        button.text = "X"
        button.setBackgroundColor(Color.RED)
        // Add button to GridLayout
        val params = GridLayout.LayoutParams()
        params.rowSpec = GridLayout.spec(gridLayout.rowCount-1)
        params.columnSpec = GridLayout.spec(gridLayout.columnCount-1)
        params.width = 150
        //button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)
        params.setMargins(50, 10, 10, 10)
        button.setOnClickListener {
            // Handle button click
            resetLogoLetters()
        }
        button.layoutParams = params
        gridLayout.addView(button)
        currentLogoNameButton = logoNameButtons[0]
    }
     fun addRandomLetterButtons(gridLayout: GridLayout){
        // Create and add buttons
        for (i in 0 until lettercount) { // Ensure there are at most 8 buttons
            //todo ensure letters from logo name are always there
            val button = Button(requireContext())
            button.text = letters[i].letter.toString()
            button.setOnClickListener {
                // Handle button click
                onLetterButtonClick(button,letters[i])
            }

            // Customize button appearance if needed
            button.setBackgroundColor(letters[i].bgColor)
            // Add button to GridLayout
            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(i / gridLayout.columnCount)
            params.columnSpec = GridLayout.spec(i % gridLayout.columnCount)
            params.width = 100
            params.setMargins(10, 10, 10, 10)
            button.layoutParams = params

            gridLayout.addView(button)
            letterButtons.add(button)
        }
    }
    private fun onLetterButtonClick(button: Button,letter: Letter) {
        // Handle the click event for the letter button
        // You can implement your logic here
        letter.bgColor = Color.DKGRAY
        button.setBackgroundColor(letter.bgColor)
        button.isEnabled = false
        currentLogoNameButton.text = letter.letter.toString()
        currentLogoNameButton.setBackgroundColor(Color.YELLOW)
        currentLogoNameButton.isEnabled = true
        if(logoNameButtons.indexOf(currentLogoNameButton)== logoNameButtons.size-1)//last letter
            checkLogoName()
        else {
                jumpToFreeLetter()
        }
    }

    private fun jumpToFreeLetter() {
        for (text in logoNameButtons){
            if(text.text == ""){
                currentLogoNameButton = text
                break
            }
        }
    }

    fun checkLogoName(){
        var logoName = "abcd"
        val logoNameString = logoNameButtons.joinToString("") { it.text.toString() }
        if(logoName == logoNameString){
            // Assuming you are inside a Fragment
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            // Replace the current fragment with the new one
            val newFragment = GameRandomFragment()
            fragmentTransaction.replace(R.id.mainMenuFragmentContainer, newFragment)
            fragmentTransaction.addToBackStack(null) // Optional: Add to back stack for back navigation
            fragmentTransaction.commit()//navigate to next fragment
            return
        }
        resetLogoLetters()
        //todo check letter positions, if a letter is on a correct spot, make it green and disabled. if the letter is part of the name, but on wrong position, change bgcolor to yellow
    }
    private fun onLogoLetterButtonClick(button: Button){
        val letter = button.text.toString()
        var letterToFind = letters.find { it.letter == letter[0]  && it.bgColor == Color.DKGRAY}
        var buttonToChange = letterButtons[letters.indexOf(letterToFind)]
        //check if color is DKGRAY
        //find a button with the same letter and gray background
        buttonToChange?.let {
            it.setBackgroundColor(Color.WHITE)
            letters[letterButtons.indexOf(it)].bgColor = Color.WHITE
        }
        button.text = ""
        button.setBackgroundColor(Color.WHITE)
        buttonToChange.isEnabled = true
        button .isEnabled = false
        jumpToFreeLetter()

    }
    private fun calculateLetterCount(logName: String){
        val uniqueLetters = logName.toSet()
        var lettersToAdd = uniqueLetters.size + 4
        if (lettersToAdd < 8)
            lettersToAdd = 8
        if (lettersToAdd > 16)
            lettersToAdd = 16
        lettercount = lettersToAdd
        for (letter in uniqueLetters) {
            letters.add(Letter(letter, Color.WHITE))
        }
        for (i in 0 until (lettersToAdd - uniqueLetters.size)) {
            val random = Random.Default
            val randomLetter = alphabet[random.nextInt(0, alphabet.length)]
            letters.add(Letter(randomLetter, Color.WHITE))
        }
        letters.shuffle()
    }
    private fun resetLogoLetters(){
        for (letter in letters){
            letter.bgColor = Color.WHITE
        }
        for(logobutton in logoNameButtons){
            logobutton.text = ""
            logobutton.setBackgroundColor(Color.WHITE)
            logobutton.isEnabled = false
        }
        for(letterButton in letterButtons){
            letterButton.isEnabled = true
            letterButton.setBackgroundColor(Color.WHITE)
        }
        jumpToFreeLetter()
    }
}
data class Letter(var letter: Char, var bgColor: Int )

