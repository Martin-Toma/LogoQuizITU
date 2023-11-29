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
import androidx.activity.result.contract.ActivityResultContracts

private val alphabet = "abcdefghijklmnopqrstuvwxyz"
class GameRandomFragment : Fragment() {
    private lateinit var gameOriginalViewModel: GameOriginalViewModel
    private lateinit var logoEntityDao: LogoEntityDao
    private lateinit var randomLogo: LogoEntity
    private var letters = mutableListOf<Letter>()
    private var nameLetters = mutableListOf<Letter>()
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
        getRandomLogo()
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
            Glide.with(requireContext())
                .load(logoEntity.imagePath)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(randomLogoImageView)
        }
        val logoNameGridLayout: GridLayout = view.findViewById(R.id.LogoNameGridLayout)
        calculateLetterCount("google")//todo change to logo name when it actually works
        //var columns = min( (randomLogo.name.length + 1) / 2,8)todo uncomment
        var columns = min((10+1)/2,8)
        if(lettercount <=12) {
            logoNameGridLayout.rowCount = 1
            logoNameGridLayout.columnCount = 10 //todo uncomment this randomLogo.name.length + 1
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
        lifecycleScope.launch {
            randomLogo = withContext(Dispatchers.IO) {
                logoEntityDao.getRandomPhotoPost()!!
            }

         /*   withContext(Dispatchers.Main) {
                gameOriginalViewModel.setLogo(randomLogo)
                val path = randomLogo.imagePath
                val imageUri = Uri.parse(path)
                val logoImageView: ImageView = view?.findViewById(R.id.logoImageView)!!
                logoImageView.setImageURI(imageUri)
                /*withContext(Dispatchers.IO) {
                    try {
                        // Use ContentResolver to open an InputStream for the content URI
                        val inputStream = context?.contentResolver?.openInputStream(imageUri)

                        if (inputStream != null) {
                            // Load the InputStream into a Bitmap using BitmapFactory
                            val bitmap = BitmapFactory.decodeStream(inputStream)

                            // Close the InputStream
                            inputStream.close()

                            // Display the Bitmap in your ImageView
                            withContext(Dispatchers.Main) {
                                logoImageView.setImageBitmap(bitmap)
                            }
                        }
                    } catch (e: Exception) {
                        // Handle exceptions (e.g., IOException, FileNotFoundException)
                        e.printStackTrace()
                    }
                }*/
               /* try {
                    // Use ContentResolver to open an InputStream for the content URI
                    val uri = Uri.parse(path)
                    val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)

                    if (inputStream != null) {
                        // Load the InputStream into a Bitmap using BitmapFactory
                        val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)

                        // Close the InputStream
                        inputStream.close()

                        // Display the Bitmap in your ImageView
                        if (bitmap != null) {
                            val logoImageView: ImageView = view?.findViewById(R.id.logoImageView)!!
                            logoImageView.setImageBitmap(bitmap)
                        } else {
                            Log.e("Image Loading", "Failed to decode the bitmap.")
                        }
                    } else {
                        Log.e("Image Loading", "Failed to open InputStream for the content URI.")
                    }
                } catch (e: Exception) {
                    Log.e("Image Loading", "Exception: ${e.message}", e)
                }
            }*/
        }*/
    }
    }


    fun addLogoLetterButtons(gridLayout: GridLayout){
        val name = "google"//randomLogo.name todo delete
        for(i in 0 until 6/*randomLogo.Name.Length*/){
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
            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounder_letter)

            logoNameButtons.add(button)
            nameLetters.add(Letter(i,null,name[i],Color.WHITE,Color.WHITE))//todo change to randomLogo.name[i]
        }
        //get button with id ResetButton
        val resetButton = (view?.findViewById<Button>(R.id.resetButton))!!
        //add last button that is special
        resetButton.text = "X"
        // Add button to GridLayout
        resetButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corner)
        resetButton.setOnClickListener {
            // Handle button click
            resetLogoLetters()
        }

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
            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounder_letter)
            gridLayout.addView(button)
            letterButtons.add(button)
        }
    }
    private fun onLetterButtonClick(button: Button,letter: Letter) {
        // Handle the click event for the letter button
        letter.bgColor = Color.DKGRAY
        button.setBackgroundColor(letter.bgColor)
        button.isEnabled = false
        currentLogoNameButton.text = letter.letter.toString()
        currentLogoNameButton.setBackgroundColor(Color.YELLOW)
        currentLogoNameButton.isEnabled = true
        val logoLetter = nameLetters[logoNameButtons.indexOf(currentLogoNameButton)]
        logoLetter.bgColor = Color.YELLOW
        logoLetter.assignedLetter = letter.id
        logoLetter.letter = letter.letter
        val logoNameString = logoNameButtons.joinToString("") { it.text.toString() }
        if(logoNameString.length == 6)//todo change to RandomLogo.name.length
            checkLogoName()
        if(logoNameButtons.indexOf(currentLogoNameButton)== logoNameButtons.size-1)//last letter
            checkLogoName()
        //or all name buttons are filled
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
        val logoName = "google"
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
      //  resetLogoLetters()
        analyzeLogoName(logoNameString)
        jumpToFreeLetter()
        //todo check letter positions, if a letter is on a correct spot, make it green and disabled. if the letter is part of the name, but on wrong position, change bgcolor to yellow
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
        }
        jumpToFreeLetter()
    }
    private fun analyzeLogoName(LogoNameString: String){
      //  var logoNameString = logoNameButtons.joinToString("") { it.text.toString() }
        val CorrectLogoName = "google"//randomLogo.name todo uncomment
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

                correctLogoNameModified = correctLogoNameModified.replaceFirst(correctLogoNameModified[i].toString(),".")
                i++
            }
            else i++
        }
        i = 0
        //replace letters in LogoNameString with . if . is in correctLogoNameModified
        val stupidKotlin = StringBuilder(LogoNameString)
        while(i < LogoNameString.length){
            if(correctLogoNameModified[i] == '.'){
                stupidKotlin.setCharAt(i, '.')
            }
            i++
        }
        i = 0
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
        //loop through the remaining nameLetters and reset them
        for (letter in nameLetters){
            if(letter.bgColor == Color.YELLOW){
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
        for (letter in letters){
            if(letter.bgColor == Color.DKGRAY){
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

