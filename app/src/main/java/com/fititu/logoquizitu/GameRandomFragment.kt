package com.fititu.logoquizitu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.fititu.logoquizitu.Controller.GameOriginalController
import com.fititu.logoquizitu.Model.LogoEntity
import com.fititu.logoquizitu.ViewModel.GameOriginalViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [GameRandomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameRandomFragment : Fragment() {
    private lateinit var gameOriginalViewModel: GameOriginalViewModel

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
        return inflater.inflate(R.layout.fragment_game_random,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameOriginalViewModel = ViewModelProvider(requireActivity()).get(GameOriginalViewModel::class.java)
        gameOriginalViewModel.logoEntity?.let { logoEntity ->  }
    }
}