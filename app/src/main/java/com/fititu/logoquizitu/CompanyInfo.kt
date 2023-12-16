package com.fititu.logoquizitu

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream

class CompanyInfo() : Fragment() {
    private var companyId: Int = 0
    private lateinit var gameMode: String
    private lateinit var companyDao : CompanyDao
    private lateinit var company : CompanyEntity
    private lateinit var gameRandomMode: String
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
        companyId = arguments?.getInt("CompanyId")!!
        gameMode = arguments?.getString("GameMode")!!
        gameRandomMode = arguments?.getString("GameRandomMode")!!
        companyDao = AppDatabase.getInstance(requireContext()).companyDao()
        retrieveCompany(companyId!!)
        return view
    }

    private fun setCompanyDescription(view: View) {
        val descriptionText = view.findViewById<TextView>(R.id.Description)
        descriptionText.text = company.companyDescription
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCompanyDescription(view)
        updateModeButton(view)
        loadImage()
    }
    private fun loadImage(){
        val imageView = view?.findViewById<ImageView>(R.id.logoImageView)
        val lmao = Uri.parse(company.imgAltered)
        val xd = File(lmao.path!!)
        Glide.with(this)
            .load(xd)
            .into(imageView!!)
    }

    private fun updateModeButton(view: View) {
        val modeButton = view.findViewById<Button>(R.id.ModeButton)
        modeButton.text = "Next"
        modeButton.setBackgroundColor(Color.GREEN)
        modeButton.setTextColor(Color.BLACK)
        modeButton.setOnClickListener {
            navigateToNextFragment()
        }
    }

    private fun retrieveCompany(id : Int) {
        company = runBlocking(Dispatchers.IO){
            companyDao.getCompanyById(id)
        }
    }
    private fun navigateToNextFragment(){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val newFragment = when(gameMode){
            "mainMenu" -> MainMenuFragment()
            "GameRandom" -> GameRandom()
            "companyInfo" -> CompanyInfo()
            else -> MainMenuFragment()
        }
        if(gameMode == "GameRandom"){
            val parameter: String
            if(gameRandomMode == "Category")
                parameter = company.categoryName!!
            else if (gameRandomMode == "Level")
                parameter = company.levelId.toString()
            else
                parameter = ""
            val bundle = Bundle().apply {
                putString("GameMode", gameRandomMode)
                putString("GameModeParameter", parameter)
            }
            newFragment.arguments = bundle
        }
        //add handling for other modes if necessary
        fragmentTransaction.replace(R.id.mainMenuFragmentContainer, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}