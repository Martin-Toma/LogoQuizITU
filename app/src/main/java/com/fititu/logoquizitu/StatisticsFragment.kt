package com.fititu.logoquizitu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fititu.logoquizitu.Controller.IMainMenuController
import com.fititu.logoquizitu.Controller.MainMenuController
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Dao.GlobalProfileDao
import com.fititu.logoquizitu.Model.Entity.GlobalProfileEntity
import com.fititu.logoquizitu.View.IMainMenuView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class StatisticsFragment : Fragment(), IMainMenuView {

    private var textGuessedLogos : TextView? = null
    private var textRemainingLogos : TextView? = null
    private var textAllLogos : TextView? = null
    private var textTimeSpent : TextView? = null
    private var textHintsUsed : TextView? = null
    private var btnGoBack : Button? = null

    private lateinit var companyDao: CompanyDao
    private lateinit var globalProfileDao: GlobalProfileDao

    private var playPresenter: IMainMenuController? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show() // to show the action bar here

        textGuessedLogos = view.findViewById(R.id.text_guessed_logos)
        textRemainingLogos = view.findViewById(R.id.text_remaining_logos)
        textAllLogos = view.findViewById(R.id.text_all_logos)
        textTimeSpent = view.findViewById(R.id.text_time_spent)
        textHintsUsed = view.findViewById(R.id.text_hints_used)
        btnGoBack = view.findViewById(R.id.btn_statistics_back)

        playPresenter = MainMenuController(this)
        companyDao = AppDatabase.getInstance(requireContext()).companyDao()
        globalProfileDao = AppDatabase.getInstance(requireContext()).globalProfileDao()

        btnGoBack?.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }


        // get data for the fragment
        lifecycleScope.launch {
            val profile : GlobalProfileEntity = globalProfileDao.get()[0]
            val companies = companyDao.getAll()
            val duration = profile.gameTime.toDuration(DurationUnit.SECONDS)

            (textTimeSpent as TextView).text = "TIME SPENT: " + duration.toString()
            (textHintsUsed as TextView).text = "HINTS USED: " + profile.hintsUsedCount.toString()
            var solvedCount : Int = 0
            var remainingCount : Int = 0
            companies.forEach{
                if (it.solved) solvedCount++ else remainingCount++
            }
            (textGuessedLogos as TextView).text = "GUESSED LOGOS: " + solvedCount.toString()
            (textRemainingLogos as TextView).text = "REMAINING LOGOS: " + remainingCount.toString()
            (textAllLogos as TextView).text = "ALL LOGOS: " + (solvedCount + remainingCount).toString()
        }
        return view
    }

    override fun changeView(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainMenuFragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()
    }

    override fun changeViewWithParam(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainMenuFragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()
    }

}