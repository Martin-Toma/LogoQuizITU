package com.fititu.logoquizitu

import android.graphics.Color
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.gridlayout.widget.GridLayout
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.ViewModels.AddLogoViewModel
import com.fititu.logoquizitu.ViewModels.SelectLogoGameViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import androidx.activity.OnBackPressedDispatcher

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectLogoGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectLogoGameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: SelectLogoGameViewModel

    private lateinit var companyDao : CompanyDao

    private lateinit var nameText : TextView
    private lateinit var grid : GridLayout

    private lateinit var backButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_logo_game, container, false)
        companyDao = AppDatabase.getInstance(requireContext()).companyDao()

        nameText = view.findViewById(R.id.logo_to_be_guessed)
        grid = view.findViewById(R.id.selectGrid)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SelectLogoGameFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SelectLogoGameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[SelectLogoGameViewModel::class.java]
        //init_game(view)
        backButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // go back to menu fragment
        }
        //viewModel.initGame()
        observeViewModelSelectGame()
    }

    private fun observeViewModelSelectGame() {
        // Observe changes in the randomLogos LiveData
        viewModel.randomLogos.observe(viewLifecycleOwner) { randomLogos ->
            // This block of code is executed when the randomLogos LiveData changes

            // Do something with the random logos
            val toBeGuessedIdx = (0 until randomLogos.size).random()

            // Update the UI with the logo to be guessed
            nameText.text = randomLogos[toBeGuessedIdx].companyName

            // Update the images in the grid
            for (i in 0 until grid.childCount) {
                val rel: RelativeLayout = grid.getChildAt(i) as RelativeLayout
                val imbtn: ImageButton = rel.getChildAt(0) as ImageButton
                if(randomLogos[i].userCreated){
                    Glide.with(requireContext())
                        .load(randomLogos[i].imgOriginal)
                        .into(imbtn)
                }
                else{
                    imbtn.setImageResource(randomLogos[i].imgAlteredRsc)
                }


                // Set onClickListeners for the image buttons
                if (i == toBeGuessedIdx) {
                    imbtn.setOnClickListener {
                        disableAllButtons(grid)
                        rel.setBackgroundColor(Color.GREEN)
                        change_fragment_with_delay()
                    }
                } else {
                    imbtn.setOnClickListener {
                        disableAllButtons(grid)
                        rel.setBackgroundColor(Color.RED)
                        val relc: RelativeLayout = grid.getChildAt(toBeGuessedIdx) as RelativeLayout
                        //val imbtnCorrect: ImageButton = relc.getChildAt(0) as ImageButton
                        relc.setBackgroundColor(Color.GREEN)
                        change_fragment_with_delay()
                    }
                }
            }
        }

        // Initialize the game when the ViewModel is created
        viewModel.initGame()
    }
    // disable image buttons
    fun disableAllButtons(grid: GridLayout){
        for(i in 0 until grid.childCount) {
            val rel: RelativeLayout = grid.getChildAt(i) as RelativeLayout
            val ibtn: ImageButton = rel.getChildAt(0) as ImageButton
            ibtn.isEnabled = false
        }
    }
    /*fun init_game(view : View){
        viewModel.randomLogos
        // get view components
        val nameText : TextView = view.findViewById(R.id.logo_to_be_guessed)
        val grid : GridLayout = view.findViewById(R.id.selectGrid)

        // choose random logo name
        val randomLogos: List<CompanyEntity> = runBlocking(Dispatchers.IO) {
            companyDao.getRandomLogos()
        }

        val to_be_guessed_idx = (0..7).random()

        // set nameText to the to be guessed logo name - the to_be_guessed_idx-th from list of random logos
        nameText.setText(randomLogos[to_be_guessed_idx].companyName)

        // set images to the imageButtons and set onClickListeners to the imageButtons
        for(i in 0 until grid.childCount){
            var imbtn : ImageButton = grid.getChildAt(i) as ImageButton //view.findViewById<ImageButton>(R.id.imageButton1)

            Glide.with(requireContext())
                    .load(randomLogos[i].imgOriginal)
                    .into(imbtn)

            if (i == to_be_guessed_idx) {
                imbtn.setOnClickListener {
                    nameText.setText("Correct")
                    change_fragment_with_delay()
                }
            }
                else{
                    imbtn.setOnClickListener {
                        nameText.setText("Wrong")
                        change_fragment_with_delay()
                }
            }
        }

    }*/

    private fun change_fragment_with_delay(){
        lifecycleScope.launch{
            delay(1000)
            val currentFragment = requireActivity().supportFragmentManager.fragments.last()

            requireActivity().supportFragmentManager.beginTransaction()
                .detach(currentFragment)
                .commit()
            requireActivity().supportFragmentManager.beginTransaction()
                .attach(currentFragment)
                .commit()
        }
    }
}