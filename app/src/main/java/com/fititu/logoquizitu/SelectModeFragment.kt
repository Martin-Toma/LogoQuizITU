package com.fititu.logoquizitu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
//import com.fititu.logoquizitu.Controller.MainMenuController
import com.fititu.logoquizitu.View.ISelectModeView
import com.fititu.logoquizitu.ViewModels.SelectModeViewModel

class SelectModeFragment : Fragment(), ISelectModeView {
    private lateinit var viewModel: SelectModeViewModel

    private lateinit var btnRandom: Button
    private lateinit var btnLevel: Button
    private lateinit var btnSelect: Button
    private lateinit var btnName: Button
    private lateinit var btnCategory: Button
    private lateinit var btnPlayMyLogs: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_mode, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show() // to show the action bar
        viewModel = ViewModelProvider(this)[SelectModeViewModel::class.java]

        btnRandom = view.findViewById(R.id.btn_Random)
        btnLevel = view.findViewById(R.id.btn_Level)
        btnSelect = view.findViewById(R.id.btn_select)
        btnName = view.findViewById(R.id.btn_name)
        btnCategory = view.findViewById(R.id.btn_category)
        btnPlayMyLogs = view.findViewById(R.id.btn_play_my_logos) // the id is play_my_logos !!!

        btnRandom.setOnClickListener{
            viewModel.navigateTo(this, FragmentConstants.TO_RANDOM)
        }
        btnLevel.setOnClickListener{
            viewModel.navigateTo(this, FragmentConstants.TO_LEVEL)
        }
        btnSelect.setOnClickListener{
            viewModel.navigateTo(this, FragmentConstants.TO_SELECT)
        }
        btnName.setOnClickListener{
            viewModel.navigateTo(this, FragmentConstants.TO_NAME)
        }
        btnCategory.setOnClickListener{
            viewModel.navigateTo(this, FragmentConstants.TO_CATEGORY)
        }
        btnPlayMyLogs.setOnClickListener{
            viewModel.navigateTo(this, FragmentConstants.TO_PLAY_MY_LOGOS)
        }
        return view
    }

    override fun changeView(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainMenuFragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()
    }
}