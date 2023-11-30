package com.fititu.logoquizitu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.fititu.logoquizitu.Controller.IMainMenuController
import com.fititu.logoquizitu.Controller.MainMenuController
import com.fititu.logoquizitu.View.IMainMenuView

class MainMenuFragment : Fragment(), IMainMenuView {

    private var playButton: Button? = null
    private var myLogoButton: Button? = null
    private var quickPlayButton: Button? = null
    private var randomNameButton: Button? = null
    private var playPresenter: IMainMenuController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // initialize the view by inflating the fragment
        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)

        playButton = view.findViewById(R.id.PlayButton)
        myLogoButton = view.findViewById(R.id.MyLogos)
        quickPlayButton = view.findViewById(R.id.QuickPlay)
        randomNameButton = view.findViewById(R.id.RandomName)
        playPresenter = MainMenuController(this)

        playButton?.setOnClickListener {
            (playPresenter as MainMenuController).onClickButton("toModeMenu")
        }

        myLogoButton?.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton("toMyLogos")
        }
        quickPlayButton?.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton("toGameRandom")
        }
        randomNameButton?.setOnClickListener{
            (playPresenter as MainMenuController).onClickButton("toRandomName")
        }
        randomNameButton

        // Inflate the layout for this fragment
        return view//inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun changeView(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainMenuFragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()
    }


}