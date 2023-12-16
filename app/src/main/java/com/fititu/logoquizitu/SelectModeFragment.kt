package com.fititu.logoquizitu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fititu.logoquizitu.Controller.IMainMenuController
import com.fititu.logoquizitu.Controller.MainMenuController
import com.fititu.logoquizitu.View.IMainMenuView

class SelectModeFragment : Fragment(), IMainMenuView {
    private lateinit var btnRandom: Button
    private lateinit var btnLevel: Button
    private lateinit var btnSelect: Button
    private lateinit var btnName: Button
    private lateinit var btnCategory: Button
    private lateinit var btnPlayMyLogs: Button

    private var playPresenter: IMainMenuController? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_mode, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show() // to show the action bar

        btnRandom = view.findViewById(R.id.btn_Random)
        btnLevel = view.findViewById(R.id.btn_Level)
        btnSelect = view.findViewById(R.id.btn_select)
        btnName = view.findViewById(R.id.btn_name)
        btnCategory = view.findViewById(R.id.btn_category)
        btnPlayMyLogs = view.findViewById(R.id.btn_play_my_logos) // the id is play_my_logos !!!

        playPresenter = MainMenuController(this)

        btnRandom.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton(FragmentConstants.TO_RANDOM)
        }
        btnLevel.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton(FragmentConstants.TO_LEVEL)
        }
        btnSelect.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton(FragmentConstants.TO_SELECT)
        }
        btnName.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton(FragmentConstants.TO_NAME)
        }
        btnCategory.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton(FragmentConstants.TO_CATEGORY)
        }
        btnPlayMyLogs.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton(FragmentConstants.TO_PLAY_MY_LOGOS)
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
    }
}