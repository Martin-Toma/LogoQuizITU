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
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.AppDatabase
import kotlin.math.min
import kotlin.random.Random
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Dao.GlobalProfileDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.Entity.GlobalProfileEntity
import com.fititu.logoquizitu.Model.LogoEntityDao
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.List
//todo address issue with ' ' in logo name
class GameRandom : Fragment() {
    private lateinit var companyDao: CompanyDao
    private lateinit var logoEntityDao: LogoEntityDao
    private lateinit var randomLogo: CompanyEntity
    private lateinit var globalProfileDao: GlobalProfileDao
    private lateinit var globalProfiles: List<GlobalProfileEntity>
    private lateinit var globalProfile: GlobalProfileEntity
    private var letters = mutableListOf<Letter>()
    private var nameLetters = mutableListOf<Letter>()
    private var letterButtons = mutableListOf<Button>()
    private var lettercount: Int = 8
    private var logoNameButtons = mutableListOf<Button>()
    private lateinit var currentLogoNameButton: Button
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
        globalProfileDao = AppDatabase.getInstance(requireContext()).globalProfileDao()
        return inflater.inflate(R.layout.fragment_game_random, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        randomLogoImageView = view.findViewById(R.id.logoImageView)
        getGlobalProfile()
        if (globalProfile.currentCompanyId != -1) { //no game in progress
            initializeSavedGame(view)
            return
        }
        getRandomLogo()
        val logoNameGridLayout: GridLayout = view.findViewById(R.id.LogoNameGridLayout)
        calculateLetterCount(randomLogo.companyName)
        var columns = min((randomLogo.companyName.length + 1) / 2, 8) //todo could potentially create a row for each word in the logo name - nope, just make ' ' invisible, set a unique color so i will know it should be disabled.
        if (lettercount <= 12) {
            logoNameGridLayout.rowCount = 1
            logoNameGridLayout.columnCount = randomLogo.companyName.length + 1
        } else {
            logoNameGridLayout.rowCount = 2
            logoNameGridLayout.columnCount = columns
        }
        addLogoLetterButtons(logoNameGridLayout)

        // create buttons for each letter in the logo name
        val lettersGridLayout: GridLayout = view.findViewById(R.id.lettersGridLayout)
        columns = min((lettercount + 1) / 2, 8)
        lettersGridLayout.rowCount = 2
        lettersGridLayout.columnCount = columns
        // Add random letter buttons
        addRandomLetterButtons(lettersGridLayout)
        UpdateSkipButton()
    }


     fun getRandomLogo() {
         randomLogo = runBlocking(Dispatchers.IO) {
             companyDao.getRandomCompany()
         }

         lifecycleScope.launch {
             var path_UI: String?
             withContext(Dispatchers.Main) {
                 val path = randomLogo.imgOriginal//randomLogo2.imagePath
                 path_UI = path

                 Log.d("Image Loading", "other ${Uri.parse(path)}")
             }
             Glide.with(requireContext())
                 .load(path_UI)
                 .into(randomLogoImageView)
             //setImage(path_UI, bitmap)
         }
     }

    fun getGlobalProfile() {
        globalProfiles = runBlocking(Dispatchers.IO) {
            globalProfileDao.get()
        }
        globalProfile = globalProfiles[0]
    }

    fun addLogoLetterButtons(gridLayout: GridLayout) { //todo add text when " "
        for (i in 0 until randomLogo.companyName.length) {
            val button = Button(requireContext())
            button.setOnClickListener {
                onLogoLetterButtonClick(button)
            }
            if(randomLogo.companyName[i] != ' ') {
                button.text = ""
                button.setBackgroundColor(Color.WHITE)
            }
            else{
                button.text = " "
                button.setBackgroundColor(Color.RED)
                button.isEnabled = false
                button.isVisible = false
            }
            button.setTextColor(Color.BLACK)
            // Add button to GridLayout
            val params = GridLayout.LayoutParams()
            if (gridLayout.rowCount == 1)
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
            nameLetters.add(Letter(i, null, randomLogo.companyName[i], Color.WHITE, Color.WHITE))
        }
        val resetButton = (view?.findViewById<Button>(R.id.resetButton))!!
        resetButton.text = "X"
        resetButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)
        resetButton.setOnClickListener {
            resetLogoLetters()
        }
        currentLogoNameButton = logoNameButtons[0]
    }

    fun addRandomLetterButtons(gridLayout: GridLayout) {
        for (i in 0 until lettercount) {
            val button = Button(requireContext())
            button.text = letters[i].letter.toString()
            button.setTextColor(Color.BLACK)
            button.setOnClickListener {
                onLetterButtonClick(button, letters[i])
            }
            if(letters[i].bgColor == Color.GREEN) {
                button.isEnabled = false
                button.isVisible = false
            }
            else if(letters[i].bgColor == Color.DKGRAY){
                button.isEnabled = false
            }
            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(i / gridLayout.columnCount)
            params.columnSpec = GridLayout.spec(i % gridLayout.columnCount)
            params.width = 100
            params.setMargins(10, 10, 10, 10)
            button.layoutParams = params
            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_letter)
            gridLayout.addView(button)
            letterButtons.add(button)
            val letter = letters[i]
            val letterButton = letterButtons[letters.indexOf(letter)]
            letterButton.setBackgroundColor(letter.bgColor)
        }
    }

    private fun onLetterButtonClick(button: Button, letter: Letter) {
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
        saveCurrentGameState()
        if (logoNameString.length == randomLogo.companyName.length)
            checkLogoName()
        else {
            jumpToFreeLetter()
        }
    }

    private fun jumpToFreeLetter() {
        for (text in logoNameButtons) {
            if (text.text == "") {
                currentLogoNameButton = text
                break
            }
        }
    }

    fun checkLogoName() {
        val logoNameString = logoNameButtons.joinToString("") { it.text.toString() }
        if (randomLogo.companyName == logoNameString) {
            updateCompanySolved()
            navigateToNextFragment()
            return
        }
        analyzeLogoName(logoNameString)
        jumpToFreeLetter()
    }

    private fun updateCompanySolved() {
        randomLogo.solved = true
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                companyDao.update(randomLogo)
            }
        }
    }

    private fun navigateToNextFragment() {
        resetCurrentGameState()
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val newFragment = CompanyInfo()
        val bundle = Bundle().apply {
            putInt("CompanyId", randomLogo.id)
            putString("GameMode", "GameRandom")
        }
        newFragment.arguments = bundle

        fragmentTransaction.replace(R.id.mainMenuFragmentContainer, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun skipLogo(){
        resetCurrentGameState()
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val newFragment = GameRandom()
        fragmentTransaction.replace(R.id.mainMenuFragmentContainer, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun onLogoLetterButtonClick(button: Button) {
        val logoLetter = nameLetters[logoNameButtons.indexOf(button)] //find corresponding logoLetter
        logoLetter.bgColor = Color.WHITE
        logoLetter.letter = ' '
        val letterToFind = letters[logoLetter.assignedLetter!!]
        logoLetter.assignedLetter = null
        letterToFind.bgColor = letterToFind.defaultColor
        val buttonToChange = letterButtons[letters.indexOf(letterToFind)]
        //find a button with the same letter and gray background
        buttonToChange.setBackgroundColor(letterToFind.defaultColor)
        button.text = "" //reset the text
        button.setBackgroundColor(Color.WHITE)
        buttonToChange.isEnabled = true
        button.isEnabled = false
        saveCurrentGameState()
        jumpToFreeLetter()

    }

    private fun calculateLetterCount(logName: String) {
        var lettersToAdd = logName.length + 4
        if (lettersToAdd < 8)
            lettersToAdd = 8
        if (lettersToAdd > 16)
            lettersToAdd = 16
        lettercount = lettersToAdd
        for (letter in logName) {
            letters.add(Letter(1, null, letter, Color.WHITE, Color.WHITE))
        }
        var existingChars = logName.lowercase(Locale.ROOT).toSet()
        existingChars = existingChars.toMutableSet()
        //exclude ' '
        if(existingChars.contains(' '))
            existingChars.remove(' ')
        val alphabet =
            "abcdefghijklmnopqrstuvwxyz".filter { !existingChars.contains(it) }//exclude letters from logo name
        for (i in 0 until (lettersToAdd - logName.length)) {
            val random = Random.Default
            val randomLetter = alphabet[random.nextInt(0, alphabet.length)]
            letters.add(Letter(1, null, randomLetter, Color.WHITE, Color.WHITE))
        }
        letters.shuffle()
        //assign id to each letter
        for (i in 0 until letters.size) {
            letters[i].id = i
        }
    }

    private fun resetLogoLetters() {
        for (letter in letters) {
            letter.bgColor = Color.WHITE
        }
        for (logobutton in logoNameButtons) {
            logobutton.text = ""
            logobutton.setBackgroundColor(Color.WHITE)
            logobutton.isEnabled = false
        }
        for (letterButton in letterButtons) {
            letterButton.isEnabled = true
            letterButton.setBackgroundColor(Color.WHITE)
            letterButton.visibility = View.VISIBLE
        }
        resetCurrentGameState()
        jumpToFreeLetter()
    }

    private fun analyzeLogoName(LogoNameString: String) {
        val CorrectLogoName = randomLogo.companyName
        var correctLogoNameModified = CorrectLogoName
        var i = 0
        while (i < CorrectLogoName.length) {
            if (CorrectLogoName[i] == nameLetters[i].letter) {//correct letter on correct position

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

                correctLogoNameModified =
                    correctLogoNameModified.replaceFirst(correctLogoNameModified[i].toString(), ".")
                i++
            } else i++
        }
        i = 0
        val stupidKotlin = StringBuilder(LogoNameString)
        while (i < LogoNameString.length) {
            if (correctLogoNameModified[i] == '.') {
                stupidKotlin.setCharAt(i, '.')
            }
            i++
        }
        i = 0
        //reset all letterbuttons that are yellow to white
        for (letter in letters) {
            if (letter.bgColor == Color.YELLOW) {
                val letterButton = letterButtons[letters.indexOf(letter)]
                letterButton.setBackgroundColor(Color.WHITE)
                letterButton.isEnabled = true
                letter.bgColor = Color.WHITE
                letter.defaultColor = Color.WHITE
            }
        }
        while (i < stupidKotlin.length) {
            if (stupidKotlin[i] == '.') {
                i++
                continue
            } else if (correctLogoNameModified.contains(stupidKotlin[i])) {
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
                correctLogoNameModified = correctLogoNameModified.replaceFirst(stupidKotlin[i].toString(), ".")
            }
            i++
        }
        resetNameButtons()
        resetUsedLetters()
        saveCurrentGameState()
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

    private fun resetCurrentGameState() {
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

    private fun saveCurrentGameState() {
        var logoName = ""
        for (button in logoNameButtons) {
            if (button.text != "") {
                logoName += button.text
            } else logoName += " "
        }
        var logoColor = ""

        for (letter in nameLetters) {
            when (letter.bgColor) {
                Color.WHITE -> logoColor += 'W'
                Color.YELLOW -> logoColor += 'Y'
                Color.GREEN -> logoColor += 'G'
                Color.DKGRAY -> logoColor += 'D'

            }
        }
        val letterstring = letters.joinToString("") { it.letter.toString() }
        var nameColor = ""
        for (letter in letters) {
            when (letter.bgColor) {
                Color.WHITE -> nameColor += "W"
                Color.YELLOW -> nameColor += "Y"
                Color.GREEN -> nameColor += "G"
                Color.DKGRAY -> nameColor += "D"
            }
        }
        runBlocking(Dispatchers.IO) {
            globalProfileDao.update(
                GlobalProfileEntity(
                    globalProfile.id,
                    globalProfile.hintsCount,
                    globalProfile.hintsUsedCount,
                    globalProfile.gameTime,
                    randomLogo.id,
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

    private fun initializeSavedGame(view: View) {
        val logoNameGridLayout: GridLayout = view.findViewById(R.id.LogoNameGridLayout)
        LoadLogo()
        val logoLetterCount = globalProfile.logoLetters.length
        val logoNameCharArray = globalProfile.logoLetters.toCharArray()
        val logoColorCharArray = globalProfile.logoColors.toCharArray()
        val letterCharArray = globalProfile.letters.toCharArray()
        val letterColorCharArray = globalProfile.letterColors.toCharArray()
        if (letterCharArray.size <= 12) { //todo same here, create a row for each word
            logoNameGridLayout.rowCount = 1
            logoNameGridLayout.columnCount = logoLetterCount + 1
        } else {
            logoNameGridLayout.rowCount = 2
            logoNameGridLayout.columnCount = min((logoLetterCount + 1) / 2, 8)
        }
        //create buttons and letter objects for logo name

        for (i in logoNameCharArray.indices) {
            createLogoButton(logoNameCharArray, i, logoColorCharArray, logoNameGridLayout)
            val buttonLetter = nameLetters[i]
            val logoButton = logoNameButtons[nameLetters.indexOf(buttonLetter)]
            logoButton.setBackgroundColor(buttonLetter.bgColor)
        }

        val lettersGridLayout: GridLayout = view.findViewById(R.id.lettersGridLayout)
        lettersGridLayout.rowCount = 2
        lettersGridLayout.columnCount = min((globalProfile.letters.length + 1) / 2, 8)
        for(i in letterCharArray.indices){
            //create letters
            val color = when(letterColorCharArray[i]){
                'W' -> Color.WHITE
                'Y' -> Color.YELLOW
                'G' -> Color.GREEN
                'D' -> Color.DKGRAY
                else -> Color.WHITE
            }
            letters.add(Letter(i, null, letterCharArray[i], color, color))
        }
        lettercount = letters.size
        addRandomLetterButtons(lettersGridLayout)
        createResetButton(view)
        UpdateSkipButton()
        AssignLetters()
        jumpToFreeLetter()
    }

    private fun createResetButton(view: View) {
        val resetButton = (view.findViewById<Button>(R.id.resetButton))!!
        resetButton.text = "X"
        resetButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)
        resetButton.setOnClickListener {
            resetLogoLetters()
        }
    }

    private fun createLogoButton( //todo add text when " ", will be tricky, mby with the help of color, i.e. space = red, space !=red if empty slot
        logoNameCharArray: CharArray,
        i: Int,
        logoColorCharArray: CharArray,
        logoNameGridLayout: GridLayout
    ) {
        val button = Button(requireContext())
        button.setOnClickListener {
            onLogoLetterButtonClick(button)
        }
        if (logoNameCharArray[i] == ' ') {
            button.text = ""
            button.isEnabled = false
        } else
            button.text = logoNameCharArray[i].toString()
        if(logoColorCharArray[i] == 'G'){
            nameLetters.add(Letter(i, null, logoNameCharArray[i], Color.GREEN, Color.GREEN))
            button.isEnabled = false
        }
        else
            nameLetters.add(Letter(i, null, logoNameCharArray[i], Color.WHITE, Color.WHITE))
        button.setTextColor(Color.BLACK)
        // Add button to GridLayout
        val params = GridLayout.LayoutParams()
        if (logoNameGridLayout.rowCount == 1)
            params.rowSpec = GridLayout.spec(0)
        else
            params.rowSpec = GridLayout.spec(i / logoNameGridLayout.columnCount)
        params.columnSpec = GridLayout.spec(i % logoNameGridLayout.columnCount)
        params.width = 100
        params.setMargins(10, 10, 10, 10)
        button.layoutParams = params
        logoNameGridLayout.addView(button)
        button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_letter)
        logoNameButtons.add(button)

    }
    private fun AssignLetters(){
        for(logoLetter in nameLetters){
            if(logoLetter.bgColor == Color.GREEN){
                //find the letter in letters and assign it to logoLetter
                val letter = letters.find { it.letter == logoLetter.letter && it.bgColor == Color.GREEN }//todo stopped here
                logoLetter.assignedLetter = letter?.id
            }
            else if(logoLetter.letter != ' '){
                val letter = letters.find { it.letter == logoLetter.letter && it.bgColor == Color.DKGRAY }
                logoLetter.assignedLetter = letter?.id
            }
        }
    }
    private fun LoadLogo() {
        randomLogo = runBlocking(Dispatchers.IO) {
            companyDao.getCompanyById(globalProfile.currentCompanyId)
        }
        lifecycleScope.launch {
            var path_UI: String?
            withContext(Dispatchers.Main) {
                val path = randomLogo.imgOriginal//randomLogo2.imagePath
                path_UI = path

                Log.d("Image Loading", "other ${Uri.parse(path)}")
            }
            Glide.with(requireContext())
                .load(path_UI)
                .into(randomLogoImageView)
            //setImage(path_UI, bitmap)
        }

    }
    private fun UpdateSkipButton(){
        val skipButton = view?.findViewById<Button>(R.id.skipButton)
        skipButton?.text = "Skip"
        skipButton?.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)
        skipButton?.setOnClickListener {
            skipLogo()
        }

    }
}
    data class Letter(var id: Int, var assignedLetter: Int?, var letter: Char, var bgColor: Int, var defaultColor: Int)