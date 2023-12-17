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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlin.math.min
import kotlin.random.Random
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.ViewModels.GameRandomViewModel
import kotlinx.coroutines.*
import java.util.*


class GameRandom : Fragment() {
    private lateinit var randomLogo: CompanyEntity //so i don't have to write viewModel.randomLogo every time
    private var letters = mutableListOf<Letter>()
    private var nameLetters = mutableListOf<Letter>()
    private var letterButtons = mutableListOf<Button>()
    private var lettercount: Int = 8
    private var logoNameButtons = mutableListOf<Button>()
    private lateinit var currentLogoNameButton: Button
    private lateinit var randomLogoImageView: ImageView
    private lateinit var viewModel: GameRandomViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[GameRandomViewModel::class.java]
        //if logo is null, navigate to main menu
        viewModel.logoNull.observe(
            viewLifecycleOwner
        ) {
            if (it) {
                navigateToNextFragment("MainMenu")
            }
        }
        viewModel.gameMode = arguments?.getString("GameMode")!!
        viewModel.gameModeParameter = arguments?.getString("GameModeParameter")!!
        return inflater.inflate(R.layout.fragment_game_random, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        randomLogoImageView = view.findViewById(R.id.logoImageView)
        viewModel.getGlobalProfile()
        if (viewModel.gameMode == "GameRandom") { // saving game state is only supported for GameRandom
            if (viewModel.globalProfile.currentCompanyId != -1) { //game in progress
                initializeSavedGame(view)
                return
            }
        }
        viewModel.getRandomLogo()
        if (viewModel.logoNull.value == true) {
            navigateToNextFragment("MainMenu")
            return
        }
        randomLogo = viewModel.randomLogo
        setImage()
        val logoNameGridLayout: GridLayout = view.findViewById(R.id.LogoNameGridLayout)
        calculateLetterCount(randomLogo.companyName)
        var columns = min(
            (randomLogo.companyName.length + 1) / 2,
            8
        )
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
        updateSkipButton()
        initReturnButton(view)
    }


    private fun setImage() {
        if (randomLogo.userCreated) {
            lifecycleScope.launch {
                var pathUI: String?
                withContext(Dispatchers.Main) {
                    val path = randomLogo.imgOriginal
                    pathUI = path
                }
                Glide.with(requireContext())
                    .load(pathUI)
                    .into(randomLogoImageView)
            }
        } else
            randomLogoImageView.setImageResource(randomLogo.imgAlteredRsc)
    }

    private fun addLogoLetterButtons(gridLayout: GridLayout) {
        for (i in 0 until randomLogo.companyName.length) {
            val button = Button(requireContext())
            button.setOnClickListener {
                onLogoLetterButtonClick(button)
            }
            if (randomLogo.companyName[i] != ' ') {
                button.text = ""
                button.setBackgroundColor(Color.WHITE)
                nameLetters.add(Letter(i, null, randomLogo.companyName[i], Color.WHITE, Color.WHITE))
            } else {
                button.text = " "
                button.setBackgroundColor(Color.RED)
                nameLetters.add(Letter(i, null, randomLogo.companyName[i], Color.RED, Color.RED))

                button.isEnabled = false
                button.isVisible = false
            }
            addLogoLetterButtonToLayout(button, gridLayout, i)
        }
        val resetButton = (view?.findViewById<Button>(R.id.resetButton))!!
        resetButton.text = buildString {
            append("RESET")
        }
        resetButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)
        resetButton.setOnClickListener {
            resetLogoLetters()
        }
        currentLogoNameButton = logoNameButtons[0]
    }

    private fun addLogoLetterButtonToLayout(button: Button, gridLayout: GridLayout, i: Int) {
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
    }

    private fun addRandomLetterButtons(gridLayout: GridLayout) {
        for (i in 0 until lettercount) {
            val button = Button(requireContext())
            button.text = letters[i].letter.toString()
            button.setTextColor(Color.BLACK)
            button.setOnClickListener {
                onLetterButtonClick(button, letters[i])
            }
            if (letters[i].bgColor == Color.GREEN) {
                button.isEnabled = false
                button.isVisible = false
            } else if (letters[i].bgColor == Color.DKGRAY) {
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
        if (viewModel.gameMode == "GameRandom")
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

    private fun checkLogoName() {
        val logoNameString = logoNameButtons.joinToString("") { it.text.toString() }
        viewModel.checkLogoSolution(logoNameString)
        if (viewModel.logoSolved.value == true) {
            viewModel.updateCompanySolved()
            navigateToNextFragment("CompanyInfo")
            return
        }
        analyzeLogoName(logoNameString)
        jumpToFreeLetter()
    }

    /*  private fun updateCompanySolved() { //todo delete this only if the viewmodel version is confirmed to work
          randomLogo.solved = true
          lifecycleScope.launch {
              withContext(Dispatchers.IO) {
                  companyDao.update(randomLogo) // need to get dao again
              }
          }
      }*/

    private fun navigateToNextFragment(fragment: String) {
        if (fragment != "MainMenu")
            viewModel.resetCurrentGameState()
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val newFragment: Fragment
        val bundle: Bundle
        when (fragment) {
            "CompanyInfo" -> {
                newFragment = CompanyInfo()
                bundle = Bundle().apply {
                    putInt("CompanyId", randomLogo.id)
                    putString("GameMode", "GameRandom")
                    putString("GameRandomMode", viewModel.gameMode)
                }
            }

            "GameRandom" -> {
                newFragment = GameRandom()
                bundle = Bundle().apply {
                    putString("GameMode", viewModel.gameMode)
                    putString("GameModeParameter", viewModel.gameModeParameter)
                }
            }

            else -> {
                newFragment = MainMenuFragment()
                bundle = Bundle().apply {}
            }
        }
        newFragment.arguments = bundle

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
        if (letterToFind.defaultColor == Color.DKGRAY) {
            letterToFind.bgColor = Color.WHITE
            letterToFind.defaultColor = Color.WHITE
        }
        buttonToChange.setBackgroundColor(letterToFind.defaultColor)
        button.text = "" //reset the text
        button.setBackgroundColor(Color.WHITE)
        buttonToChange.isEnabled = true
        button.isEnabled = false
        if (viewModel.gameMode == "GameRandom")
            saveCurrentGameState()
        jumpToFreeLetter()

    }

    private fun calculateLetterCount(logName: String) {
        val companyName = logName.filter { !it.isWhitespace() }
        var lettersToAdd = logName.length + 4
        if (lettersToAdd < 8)
            lettersToAdd = 8
        if (lettersToAdd > 16)
            lettersToAdd = 16
        lettercount = lettersToAdd
        for (letter in companyName) {
            letters.add(Letter(1, null, letter, Color.WHITE, Color.WHITE))
        }
        var existingChars = companyName.lowercase(Locale.ROOT).toSet()
        existingChars = existingChars.toMutableSet()

        val alphabet =
            "abcdefghijklmnopqrstuvwxyz".filter { !existingChars.contains(it) }//exclude letters from logo name
        for (i in 0 until (lettersToAdd - companyName.length)) {
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
        for (logoButton in logoNameButtons) {
            if (nameLetters[logoNameButtons.indexOf(logoButton)].defaultColor == Color.RED) // leave ' ' unchanged
                continue
            logoButton.text = ""
            logoButton.setBackgroundColor(Color.WHITE)
            logoButton.isEnabled = false
        }
        for (letterButton in letterButtons) {
            letterButton.isEnabled = true
            letterButton.setBackgroundColor(Color.WHITE)
            letterButton.visibility = View.VISIBLE
        }
        viewModel.resetCurrentGameState()
        jumpToFreeLetter()
    }

    private fun analyzeLogoName(logoNameString: String) {
        val correctLogoName = randomLogo.companyName
        var correctLogoNameModified = correctLogoName
        var i = 0
        while (i < correctLogoName.length) {
            if (correctLogoName[i] == nameLetters[i].letter) {//correct letter on correct position
                if (correctLogoName[i] == ' ') {
                    correctLogoNameModified =
                        correctLogoNameModified.replaceFirst(correctLogoNameModified[i].toString(), ".")
                    i++
                    continue
                }
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
        val modifiedLogoName = StringBuilder(logoNameString)
        while (i < logoNameString.length) {
            if (correctLogoNameModified[i] == '.') {
                modifiedLogoName.setCharAt(i, '.')
            }
            i++
        }
        i = 0
        resetYellowLetters()
        while (i < modifiedLogoName.length) {
            if (modifiedLogoName[i] == '.') {
                i++
                continue
            } else if (correctLogoNameModified.contains(modifiedLogoName[i])) {
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
                modifiedLogoName.setCharAt(i, '.')
                correctLogoNameModified = correctLogoNameModified.replaceFirst(modifiedLogoName[i].toString(), ".")
            }
            i++
        }
        resetNameButtons()
        resetUsedLetters()
        if (viewModel.gameMode == "GameRandom")
            saveCurrentGameState()
    }

    private fun resetYellowLetters() {
        for (letter in letters) {
            if (letter.bgColor == Color.YELLOW) {
                val letterButton = letterButtons[letters.indexOf(letter)]
                letterButton.setBackgroundColor(Color.WHITE)
                letterButton.isEnabled = true
                letter.bgColor = Color.WHITE
                letter.defaultColor = Color.WHITE
            }
        }
    }

    private fun resetNameButtons() {
        for (letter in nameLetters) {
            if (letter.bgColor == Color.YELLOW || letter.bgColor == Color.WHITE) {
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
                Color.RED -> logoColor += 'R'

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
        viewModel.saveStateToDb(logoName, logoColor, letterstring, nameColor, randomLogo.id)
    }

    private fun initializeSavedGame(view: View) {
        val logoNameGridLayout: GridLayout = view.findViewById(R.id.LogoNameGridLayout)
        viewModel.getCompanyById()
        randomLogo = viewModel.randomLogo
        setImage()
        val logoLetterCount = viewModel.globalProfile.logoLetters.length
        val logoNameCharArray = viewModel.globalProfile.logoLetters.toCharArray()
        val logoColorCharArray = viewModel.globalProfile.logoColors.toCharArray()
        val letterCharArray = viewModel.globalProfile.letters.toCharArray()
        val letterColorCharArray = viewModel.globalProfile.letterColors.toCharArray()
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
        lettersGridLayout.columnCount = min((viewModel.globalProfile.letters.length + 1) / 2, 8)
        for (i in letterCharArray.indices) {
            //create letters
            val color = when (letterColorCharArray[i]) {
                'W' -> Color.WHITE
                'Y' -> Color.YELLOW
                'G' -> Color.GREEN
                'D' -> Color.DKGRAY
                'R' -> Color.RED
                else -> Color.WHITE
            }
            letters.add(Letter(i, null, letterCharArray[i], color, color))
        }
        lettercount = letters.size
        addRandomLetterButtons(lettersGridLayout)
        createResetButton(view)
        initReturnButton(view)
        updateSkipButton()
        assignLetters()
        jumpToFreeLetter()
    }

    private fun createResetButton(view: View) {
        val resetButton = (view.findViewById<Button>(R.id.resetButton))!!
        resetButton.text = buildString {
            append("RESET")
        }
        resetButton.setBackgroundColor(Color.RED)
        resetButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)
        resetButton.setOnClickListener {
            resetLogoLetters()
        }
    }

    private fun initReturnButton(view: View) {
        val returnButton = (view.findViewById<Button>(R.id.returnButton))!!
        returnButton.text = buildString {
            append("MAIN MENU")
        }
        returnButton.setBackgroundColor(Color.RED)
        returnButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)
        returnButton.setOnClickListener {
            navigateToNextFragment("MainMenu")
        }
    }

    private fun createLogoButton(
        logoNameCharArray: CharArray,
        i: Int,
        logoColorCharArray: CharArray,
        logoNameGridLayout: GridLayout
    ) {
        val button = Button(requireContext())
        button.setOnClickListener {
            onLogoLetterButtonClick(button)
        }
        if (randomLogo.companyName[i] == ' ' && logoColorCharArray[i] == 'R') {
            button.text = " "
            button.setBackgroundColor(Color.RED)
            button.isEnabled = false
            button.isVisible = false
        } else if (logoNameCharArray[i] == ' ') {
            button.text = ""
            button.isEnabled = false
        } else
            button.text = logoNameCharArray[i].toString()
        if (logoColorCharArray[i] == 'G') {
            nameLetters.add(Letter(i, null, logoNameCharArray[i], Color.GREEN, Color.GREEN))
            button.isEnabled = false
        } else if (logoColorCharArray[i] == 'R')
            nameLetters.add(Letter(i, null, logoNameCharArray[i], Color.RED, Color.RED))
        else
            nameLetters.add(Letter(i, null, logoNameCharArray[i], Color.WHITE, Color.WHITE))
        addLogoLetterButtonToLayout(button, logoNameGridLayout, i)
        if (logoNameCharArray[i] != ' ' && (logoColorCharArray[i] == 'W' || logoColorCharArray[i] == 'Y'))
            button.isEnabled = true
    }

    private fun assignLetters() {
        for (logoLetter in nameLetters) {
            if (logoLetter.bgColor == Color.GREEN) {
                //find the letter in letters and assign it to logoLetter
                val letter =
                    letters.find { it.letter == logoLetter.letter && it.bgColor == Color.GREEN }//todo stopped here
                logoLetter.assignedLetter = letter?.id
            } else if (logoLetter.letter != ' ') {
                val letter = letters.find { it.letter == logoLetter.letter && it.bgColor == Color.DKGRAY }
                logoLetter.assignedLetter = letter?.id
            }
        }
    }

    private fun updateSkipButton() {
        val skipButton = view?.findViewById<Button>(R.id.skipButton)
        skipButton?.text = buildString {
            append("SKIP")
        }
        skipButton?.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)
        skipButton?.setOnClickListener {
            navigateToNextFragment("GameRandom")
        }

    }
}

data class Letter(var id: Int, var assignedLetter: Int?, var letter: Char, var bgColor: Int, var defaultColor: Int)