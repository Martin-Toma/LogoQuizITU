package com.fititu.logoquizitu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.fititu.logoquizitu.Controller.GalleryAdapter
import com.fititu.logoquizitu.Controller.GalleryLayoutManager
import com.fititu.logoquizitu.Model.SortBy
import com.fititu.logoquizitu.ViewModels.GalleryViewModel

class GalleryFragment(
    private val countryOfOrigin: String?,
    private val category: String?,
    private val level: Int?,
    private val sortBy: SortBy
) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        viewModel = ViewModelProvider(this)[GalleryViewModel::class.java]
        viewModel.setFilters(countryOfOrigin, category, level, sortBy)
        viewModel.getCompanies()

        recyclerView = view.findViewById(R.id.recycleView_gallery)

        val galleryAdapter = GalleryAdapter(requireContext(), viewModel)
        recyclerView.adapter = galleryAdapter
        recyclerView.layoutManager = GalleryLayoutManager(requireContext(), 3)

        return view
    }

//    private suspend fun getCompanies() : List<CompanyEntity> {
//        var companies : List<CompanyEntity>
//        if (countryOfOrigin == null){
//            if (category == null) {
//                if (level == null){
//                    companies = companyDao.getAll()
//                }
//                else{
//                    companies = companyDao.getCompaniesOfLevel(level)
//                }
//            }
//            else{
//                if (level == null){
//                    companies = companyDao.getCompaniesOfCategory(category)
//                }
//                else{
//                    companies = companyDao.getCompaniesOfLevelAndCategory(level, category)
//                }
//            }
//        }
//        else{
//            if (category == null) {
//                if (level == null){
//                    companies = companyDao.getCompaniesOfCountry(countryOfOrigin)
//                }
//                else{
//                    companies = companyDao.getCompaniesOfLevelAndCountry(level, countryOfOrigin)
//                }
//            }
//            else{
//                if (level == null){
//                    companies = companyDao.getCompaniesOfCategoryAndCountry(countryOfOrigin, category)
//                }
//                else{
//                    companies = companyDao.getCompaniesAllFilters(level, countryOfOrigin, category)
//                }
//            }
//        }
//
//        companies = when (sortBy){
//            SortBy.ALPHABETICALLY -> {
//                companies.sortedBy { it.companyName }
//            }
//
//            SortBy.LEVEL -> {
//                companies.sortedBy { it.levelId }
//            }
//
//            SortBy.AGE -> {
//                companies.sortedBy { it.foundationDate.time }
//            }
//        }
//
//        return companies
//    }
}