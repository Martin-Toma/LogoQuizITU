package com.fititu.logoquizitu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Dao.GlobalProfileDao
import com.fititu.logoquizitu.myviewmodels.StatisticsViewModel
import kotlinx.coroutines.launch

class StatisticsFragment : Fragment() {
    private lateinit var viewModel: StatisticsViewModel

    private var textGuessedLogos: TextView? = null
    private var textRemainingLogos: TextView? = null
    private var textAllLogos: TextView? = null
    private var textTimeSpent: TextView? = null
    private var textHintsUsed: TextView? = null
    private var btnGoBack: Button? = null

    private lateinit var companyDao: CompanyDao
    private lateinit var globalProfileDao: GlobalProfileDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show() // to show the action bar here
        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]
        viewModel.getStatistics()

        textGuessedLogos = view.findViewById(R.id.text_guessed_logos)
        textRemainingLogos = view.findViewById(R.id.text_remaining_logos)
        textAllLogos = view.findViewById(R.id.text_all_logos)
        textTimeSpent = view.findViewById(R.id.text_time_spent)
        textHintsUsed = view.findViewById(R.id.text_hints_used)
        btnGoBack = view.findViewById(R.id.btn_statistics_back)

        companyDao = AppDatabase.getInstance(requireContext()).companyDao()
        globalProfileDao = AppDatabase.getInstance(requireContext()).globalProfileDao()

        btnGoBack?.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }


        // get data for the fragment
        lifecycleScope.launch {

            (textTimeSpent as TextView).text = "TIME SPENT: " + viewModel.duration.toString()
            (textHintsUsed as TextView).text =
                "HINTS USED: " + viewModel.profile.hintsUsedCount.toString()
            (textGuessedLogos as TextView).text =
                "GUESSED LOGOS: " + viewModel.solvedCount.toString()
            (textRemainingLogos as TextView).text =
                "REMAINING LOGOS: " + viewModel.remainingCount.toString()
            (textAllLogos as TextView).text =
                "ALL LOGOS: " + (viewModel.solvedCount + viewModel.remainingCount).toString()
        }
        return view
    }
}