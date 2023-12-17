package com.fititu.logoquizitu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fititu.logoquizitu.Controller.GalleryAdapter
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.SortBy
import kotlinx.coroutines.launch

class GalleryFragment(
    private val countryOfOrigin : String?,
    private val category : String?,
    private val level : Int?,
    private val sortBy : SortBy)
    : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var companyDao: CompanyDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        recyclerView = view.findViewById(R.id.recycleView_gallery)
        companyDao = AppDatabase.getInstance(requireContext()).companyDao()

        lifecycleScope.launch {
            val companies = getCompanies()
            recyclerView.adapter = GalleryAdapter(requireContext(), companies)
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        }
        return view
    }

    private suspend fun getCompanies() : List<CompanyEntity> {
        var companies : List<CompanyEntity>
        if (countryOfOrigin == null){
            if (category == null) {
                if (level == null){
                    companies = companyDao.getAll()
                }
                else{
                    companies = companyDao.getCompaniesOfLevel(level)
                }
            }
            else{
                if (level == null){
                    companies = companyDao.getCompaniesOfCategory(category)
                }
                else{
                    companies = companyDao.getCompaniesOfLevelAndCategory(level, category)
                }
            }
        }
        else{
            if (category == null) {
                if (level == null){
                    companies = companyDao.getCompaniesOfCountry(countryOfOrigin)
                }
                else{
                    companies = companyDao.getCompaniesOfLevelAndCountry(level, countryOfOrigin)
                }
            }
            else{
                if (level == null){
                    companies = companyDao.getCompaniesOfCategoryAndCountry(countryOfOrigin, category)
                }
                else{
                    companies = companyDao.getCompaniesAllFilters(level, countryOfOrigin, category)
                }
            }
        }

        companies = when (sortBy){
            SortBy.ALPHABETICALLY -> {
                companies.sortedBy { it.companyName }
            }

            SortBy.LEVEL -> {
                companies.sortedBy { it.levelId }
            }

            SortBy.AGE -> {
                companies.sortedBy { it.foundationDate.time }
            }
        }

        return companies
    }
}