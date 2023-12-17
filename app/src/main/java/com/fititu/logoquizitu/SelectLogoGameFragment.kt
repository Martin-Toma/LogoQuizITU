/* Project: Logo Quiz ITU
* file purpose: fragment displaying select logos game view
* Author: Martin Tomasovic
* Last edit: 17.12.2023
* */
package com.fititu.logoquizitu

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.gridlayout.widget.GridLayout
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.ViewModels.SelectLogoGameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SelectLogoGameFragment : Fragment() {

    private lateinit var viewModel: SelectLogoGameViewModel

    private lateinit var companyDao : CompanyDao

    private lateinit var nameText : TextView
    private lateinit var grid : GridLayout

    private lateinit var backButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            // this code is executed when the randomLogos LiveData changes

            // generate index of to be guessed logo
            val toBeGuessedIdx = (0 until randomLogos.size).random()

            // Update the UI with the logo to be guessed
            nameText.text = randomLogos[toBeGuessedIdx].companyName

            // Update the images in the grid
            for (i in 0 until grid.childCount) {
                val rel: RelativeLayout = grid.getChildAt(i) as RelativeLayout
                val imbtn: ImageButton = rel.getChildAt(0) as ImageButton
                if(randomLogos[i].userCreated){ // load user created logos
                    Glide.with(requireContext())
                        .load(randomLogos[i].imgOriginal)
                        .into(imbtn)
                }
                else{
                    // load hard-coded logos
                    imbtn.setImageResource(randomLogos[i].imgAlteredRsc)
                }


                // set onClickListeners for the image buttons
                if (i == toBeGuessedIdx) { // success
                    imbtn.setOnClickListener {
                        disableAllButtons(grid) // disable all buttons
                        rel.setBackgroundColor(Color.GREEN) // show green frame meaning correct answer
                        change_fragment_with_delay()
                    }
                } else {
                    imbtn.setOnClickListener {// wrong guess
                        disableAllButtons(grid) // disable all buttons
                        rel.setBackgroundColor(Color.RED) // show red frame meaning incorrect answer
                        val relc: RelativeLayout = grid.getChildAt(toBeGuessedIdx) as RelativeLayout
                        relc.setBackgroundColor(Color.GREEN) // show green frame meaning correct answer
                        change_fragment_with_delay()
                    }
                }
            }
        }

        // initialize the game when the ViewModel is created
        viewModel.initGame()
    }
    // disable image buttons
    fun disableAllButtons(grid: GridLayout){
        // iterate over all grid child views
        for(i in 0 until grid.childCount) {
            val rel: RelativeLayout = grid.getChildAt(i) as RelativeLayout
            val ibtn: ImageButton = rel.getChildAt(0) as ImageButton
            ibtn.isEnabled = false // disable the image button
        }
    }

    // method for game continuation after guess
    private fun change_fragment_with_delay(){
        lifecycleScope.launch{
            delay(1000) // wait 1000 seconds

            // change fragment
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