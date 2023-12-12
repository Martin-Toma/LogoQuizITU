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
import kotlin.math.min
import kotlin.random.Random
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.LogoEntity
import com.fititu.logoquizitu.Model.LogoEntityDao
import kotlinx.coroutines.*
import java.util.*

class GameRandom : Fragment() {
    private lateinit var companyDao: CompanyDao
    private lateinit var logoEntityDao: LogoEntityDao
    private lateinit var randomLogo: CompanyEntity
    private lateinit var randomLogo2: LogoEntity
    private var letters = mutableListOf<Letter>()
    private var nameLetters = mutableListOf<Letter>()
    private var letterButtons = mutableListOf<Button>()
    private var lettercount : Int = 8
    private var logoNameButtons = mutableListOf<Button>()
    private lateinit var currentLogoNameButton : Button
    private lateinit var randomLogoImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logoEntityDao = AppDatabase.getInstance(requireContext()).logoEntityDao()
        companyDao = AppDatabase.getInstance(requireContext()).companyDao()
        return inflater.inflate(R.layout.fragment_game_random,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRandomLogo()
        randomLogoImageView = view.findViewById(R.id.logoImageView)

        Glide.with(randomLogoImageView.context)
            .load(randomLogo.imgAltered)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_background)
            .into(randomLogoImageView)
        val logoNameGridLayout: GridLayout = view.findViewById(R.id.LogoNameGridLayout)
        calculateLetterCount(randomLogo.companyName)
        var columns = min( (randomLogo.companyName.length + 1) / 2,8)
        if(lettercount <=12) {
            logoNameGridLayout.rowCount = 1
            logoNameGridLayout.columnCount = randomLogo.companyName.length + 1
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

     fun getRandomLogo() {
         randomLogo = runBlocking(Dispatchers.IO) {
             companyDao.getRandomCompany()
         }
         lifecycleScope.launch {
             randomLogo2 = withContext(Dispatchers.IO) {
                 logoEntityDao.getRandomPhotoPost()!!
             }
             var path_UI : String? = null
             withContext(Dispatchers.Main) {
                 val path = randomLogo2.imagePath
                 path_UI = path

                 Log.d("Image Loading", "other ${Uri.parse(path)}")
             }
             Glide.with(requireContext())
                 .load(path_UI)
                 .into(randomLogoImageView)
             //setImage(path_UI, bitmap)
         }
    }


    fun addLogoLetterButtons(gridLayout: GridLayout){
        for(i in 0 until /*6*/randomLogo.companyName.length){
            val button = Button(requireContext())
            button.setOnClickListener {
                onLogoLetterButtonClick(button)
            }
            button.text = ""
            button.setBackgroundColor(Color.WHITE)
            button.setTextColor(Color.BLACK)
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
            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_letter)
            logoNameButtons.add(button)
            nameLetters.add(Letter(i,null,randomLogo.companyName[i],Color.WHITE,Color.WHITE))
        }
        val resetButton = (view?.findViewById<Button>(R.id.resetButton))!!
        resetButton.text = "X"
        resetButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)
        resetButton.setOnClickListener {
            resetLogoLetters()
        }
        currentLogoNameButton = logoNameButtons[0]
    }
    fun addRandomLetterButtons(gridLayout: GridLayout){
        for (i in 0 until lettercount) {
            val button = Button(requireContext())
            button.text = letters[i].letter.toString()
            button.setTextColor(Color.BLACK)
            button.setOnClickListener {
                onLetterButtonClick(button,letters[i])
            }
            button.setBackgroundColor(letters[i].bgColor)
            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(i / gridLayout.columnCount)
            params.columnSpec = GridLayout.spec(i % gridLayout.columnCount)
            params.width = 100
            params.setMargins(10, 10, 10, 10)
            button.layoutParams = params
            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_letter)
            gridLayout.addView(button)
            letterButtons.add(button)
        }
    }
    private fun onLetterButtonClick(button: Button,letter: Letter) {
        letter.bgColor = Color.DKGRAY
        button.setBackgroundColor(letter.bgColor)
        button.isEnabled = false
        currentLogoNameButton.text = letter.letter.toString()
        currentLogoNameButton.isEnabled = true
        val logoLetter = nameLetters[logoNameButtons.indexOf(currentLogoNameButton)]
        logoLetter.bgColor = Color.YELLOW
        logoLetter.assignedLetter = letter.id
        logoLetter.letter = letter.letter
        val logoNameString = logoNameButtons.joinToString("") { it.text.toString() }
        if(logoNameString.length == randomLogo.companyName.length )
            checkLogoName()
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
        val logoNameString = logoNameButtons.joinToString("") { it.text.toString() }
        if(randomLogo.companyName == logoNameString){
            navigateToNextFragment()
            return
        }
        analyzeLogoName(logoNameString)
        jumpToFreeLetter()
    }

    private fun navigateToNextFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val newFragment = GameRandom()
        fragmentTransaction.replace(R.id.mainMenuFragmentContainer, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()//navigate to next fragment
    }

    private fun onLogoLetterButtonClick(button: Button){
        val logoLetter = nameLetters[logoNameButtons.indexOf(button)] //find corresponding logoLetter
        logoLetter.bgColor = Color.WHITE
        logoLetter.letter = ' '
        val letterToFind= letters[logoLetter.assignedLetter!!]
        logoLetter.assignedLetter = null
        letterToFind.bgColor = letterToFind.defaultColor
        val buttonToChange = letterButtons[letters.indexOf(letterToFind)]
        //find a button with the same letter and gray background
        buttonToChange?.let {
            it.setBackgroundColor(letterToFind.defaultColor)
        }
        button.text = "" //reset the text
        button.setBackgroundColor(Color.WHITE)
        buttonToChange.isEnabled = true
        button .isEnabled = false
        jumpToFreeLetter()

    }
    private fun calculateLetterCount(logName: String){
        var lettersToAdd = logName.length + 4
        if (lettersToAdd < 8)
            lettersToAdd = 8
        if (lettersToAdd > 16)
            lettersToAdd = 16
        lettercount = lettersToAdd
        for (letter in logName) {
            letters.add(Letter(1,null,letter, Color.WHITE, Color.WHITE))
        }
        val existingChars = logName.lowercase(Locale.ROOT).toSet()
        val alphabet = "abcdefghijklmnopqrstuvwxyz".filter { !existingChars.contains(it) }//exclude letters from logo name
        for (i in 0 until (lettersToAdd - logName.length)) {
            val random = Random.Default
            val randomLetter = alphabet[random.nextInt(0, alphabet.length)]
            letters.add(Letter(1,null,randomLetter, Color.WHITE, Color.WHITE))
        }
        letters.shuffle()
        //assign id to each letter
        for (i in 0 until letters.size){
            letters[i].id = i
        }
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
            letterButton.visibility = View.VISIBLE
        }
        jumpToFreeLetter()
    }
    private fun analyzeLogoName(LogoNameString: String){
        val CorrectLogoName = randomLogo.companyName
        var correctLogoNameModified = CorrectLogoName
        var i = 0
        while (i < CorrectLogoName.length){
            if(CorrectLogoName[i] == nameLetters[i].letter){//correct letter on correct position

                nameLetters[i].bgColor = Color.GREEN
                nameLetters[i].defaultColor = Color.GREEN

                val logoNameButton = logoNameButtons[i]
                logoNameButton.setBackgroundColor(Color.GREEN)
                logoNameButton.isEnabled = false

                val letter = letters[nameLetters[i].assignedLetter!!]
                letter.bgColor = Color.GREEN
                letter.defaultColor = Color.GREEN

                val letterButton = letterButtons[letters.indexOf(letter)]
                letterButton.setBackgroundColor(Color.GREEN)
                letterButton.isEnabled = false
                letterButton.visibility = View.INVISIBLE

                correctLogoNameModified = correctLogoNameModified.replaceFirst(correctLogoNameModified[i].toString(),".")
                i++
            }
            else i++
        }
        i = 0
        val stupidKotlin = StringBuilder(LogoNameString)
        while(i < LogoNameString.length){
            if(correctLogoNameModified[i] == '.'){
                stupidKotlin.setCharAt(i, '.')
            }
            i++
        }
        i = 0
        //reset all letterbuttons that are yellow to white
        for( letter in letters){
            if(letter.bgColor == Color.YELLOW){
                val letterButton = letterButtons[letters.indexOf(letter)]
                letterButton.setBackgroundColor(Color.WHITE)
                letterButton.isEnabled = true
                letter.bgColor = Color.WHITE
                letter.defaultColor = Color.WHITE
            }
        }
        while (i < stupidKotlin.length){
            if(stupidKotlin[i] == '.'){
                i++
                continue
            }
            else if (correctLogoNameModified.contains(stupidKotlin[i])){
                //find the letter in correctLogoNameModified
                val nameLetter = nameLetters[i]
                val logoNameButton = logoNameButtons[nameLetters.indexOf(nameLetter)]
                logoNameButton.text = ""
                logoNameButton.setBackgroundColor(Color.WHITE)
                logoNameButton.isEnabled = false
                val letterIndex = nameLetter.assignedLetter
                val letter = letters[letterIndex!!]
                val letterButton = letterButtons[letters.indexOf(letter)]
                letterButton.setBackgroundColor(Color.YELLOW)
                letterButton.isEnabled = true
                letter.bgColor = Color.YELLOW
                letter.defaultColor = Color.YELLOW
                nameLetter.bgColor = Color.WHITE
                stupidKotlin.setCharAt(i, '.')
                correctLogoNameModified = correctLogoNameModified.replaceFirst(stupidKotlin[i].toString(),".")
            }
            i++
        }
        resetNameButtons()
        resetUsedLetters()
    }

    private fun resetNameButtons() {
        for (letter in nameLetters) {
            if (letter.bgColor == Color.YELLOW) {
                val logoNameButton = logoNameButtons[nameLetters.indexOf(letter)]
                logoNameButton.text = ""
                logoNameButton.setBackgroundColor(Color.WHITE)
                logoNameButton.isEnabled = false

                val nameLetter = nameLetters[logoNameButtons.indexOf(logoNameButton)]
                nameLetter.bgColor = Color.WHITE
                nameLetter.defaultColor = Color.WHITE
                nameLetter.assignedLetter = null
            }
        }
    }

    private fun resetUsedLetters() {
        for (letter in letters) {
            if (letter.bgColor == Color.DKGRAY) {
                val letterButton = letterButtons[letters.indexOf(letter)]
                letterButton.setBackgroundColor(Color.WHITE)
                letterButton.isEnabled = true
                letter.bgColor = Color.WHITE
                letter.defaultColor = Color.WHITE
            }
        }
    }
}

data class Letter(var id: Int,var assignedLetter: Int?,var letter: Char, var bgColor: Int, var defaultColor: Int )