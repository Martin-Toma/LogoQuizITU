package com.fititu.logoquizitu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.fititu.logoquizitu.Controller.IMainMenuController
import com.fititu.logoquizitu.Controller.MainMenuController
import com.fititu.logoquizitu.View.IMainMenuView

class MainMenuFragment : Fragment(), IMainMenuView {

//    private var playButton: Button? = null
//    private var myLogoButton: Button? = null
//    private var GameRandomButton: Button? = null
//    private var randomNameButton: Button? = null
    private var btnSelectMode: Button? = null
    private var btnSettings: Button? = null
    private var btnGallery: Button? = null
    private var btnStatistics: Button? = null
    private var btnMylogos: Button? = null

    private var playPresenter: IMainMenuController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // initialize the view by inflating the fragment
        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide() // to hide the action bar on the first page

        btnSelectMode = view.findViewById(R.id.btn_select_mode)
        btnSettings = view.findViewById(R.id.btn_settings)
        btnGallery = view.findViewById(R.id.btn_gallery)
        btnStatistics = view.findViewById(R.id.btn_statistics)
        btnMylogos = view.findViewById(R.id.btn_mylogos)
//        playButton = view.findViewById(R.id.PlayButton)
//        myLogoButton = view.findViewById(R.id.MyLogos)
//        GameRandomButton = view.findViewById(R.id.GameRandomButton)
//        randomNameButton = view.findViewById(R.id.RandomName)
        playPresenter = MainMenuController(this)

        btnSelectMode?.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton(FragmentConstants.TO_SELECT_MODE)
        }
        btnSettings?.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton(FragmentConstants.TO_SETTINGS)
        }
        btnGallery?.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton(FragmentConstants.TO_GALLERY)
        }
        btnStatistics?.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton(FragmentConstants.TO_STATISTICS)
        }
        btnMylogos?.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton(FragmentConstants.TO_MY_LOGOS)
        }

//        playButton?.setOnClickListener {
//            (playPresenter as MainMenuController).onClickButton("toModeMenu")
//        }
//        myLogoButton?.setOnClickListener{
//            (playPresenter as MainMenuController).onClickButton("toMyLogos")
//        }
//        GameRandomButton?.setOnClickListener{
//            (playPresenter as MainMenuController).onClickButton("toGameRandom")
//        }
//        randomNameButton?.setOnClickListener{
//            (playPresenter as MainMenuController).onClickButton("toRandomName")
//        }

        // Inflate the layout for this fragment
        return view//inflater.inflate(R.layout.fragment_main_menu, container, false)
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