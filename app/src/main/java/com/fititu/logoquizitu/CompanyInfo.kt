package com.fititu.logoquizitu

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class CompanyInfo() : Fragment() {
    private var param1: Int = 0
    private lateinit var param2: String
    private lateinit var companyDao : CompanyDao
    private lateinit var company : CompanyEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //      param1 = it.getString(ARG_PARAM1)
            //       param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_company_info, container, false)

        // Retrieve arguments
        param1 = arguments?.getInt("param1")!!
        param2 = arguments?.getString("param2")!!
        companyDao = AppDatabase.getInstance(requireContext()).companyDao()
        retrieveCompany(param1!!)
        val descriptionText = view.findViewById<TextView>(R.id.Description)
        descriptionText.text = company.companyDescription

        // Use the parameters as needed

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateModeButton(view)

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

        val newFragment = when(param2){
            "mainMenu" -> MainMenuFragment()
            "GameRandom" -> GameRandom()
            "companyInfo" -> CompanyInfo()
            else -> MainMenuFragment()
        }
        fragmentTransaction.replace(R.id.mainMenuFragmentContainer, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}