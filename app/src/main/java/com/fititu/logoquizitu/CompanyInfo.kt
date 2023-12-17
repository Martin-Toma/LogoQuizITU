package com.fititu.logoquizitu

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.ViewModels.CompanyInfoViewModel
import java.io.File

class CompanyInfo : Fragment() {
    private lateinit var viewModel: CompanyInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_company_info, container, false)
        viewModel = ViewModelProvider(this)[CompanyInfoViewModel::class.java]
        viewModel.setParameters(arguments?.getInt("CompanyId")!!, arguments?.getString("GameMode")!!, arguments?.getString("GameRandomMode")!!)
        viewModel.retrieveCompany()
        return view
    }

    private fun setCompanyDescription(view: View) {
        val descriptionText = view.findViewById<TextView>(R.id.Description)
        descriptionText.text = viewModel.company.companyDescription
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCompanyDescription(view)
        updateModeButton(view)
        loadImage()
    }
    private fun loadImage(){
        val imageView = view?.findViewById<ImageView>(R.id.logoImageView)
        val lmao = Uri.parse(viewModel.company.imgAltered)
        val xd = File(lmao.path!!)
        Glide.with(this)
            .load(xd)
            .into(imageView!!)
    }

    private fun updateModeButton(view: View) {
        val modeButton = view.findViewById<Button>(R.id.ModeButton)
        modeButton.text = "NEXT"
        modeButton.setOnClickListener {
            navigateToNextFragment()
        }
    }
    private fun navigateToNextFragment(){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val newFragment = when(viewModel.gameMode){
            "mainMenu" -> MainMenuFragment()
            "GameRandom" -> GameRandom()
            "companyInfo" -> CompanyInfo()
            else -> MainMenuFragment()
        }
        if(viewModel.gameMode == "GameRandom"){
            val parameter: String = when(viewModel.gameRandomMode){
                "Category" -> viewModel.company.categoryName!!
                "Level" -> viewModel.company.levelId.toString()
                else -> ""
            }
            val bundle = Bundle().apply {
                putString("GameMode", viewModel.gameRandomMode)
                putString("GameModeParameter", parameter)
                putInt("CompanyId", -1)
            }
            newFragment.arguments = bundle
        }
        fragmentTransaction.replace(R.id.mainMenuFragmentContainer, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}