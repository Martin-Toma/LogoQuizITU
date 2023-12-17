package com.fititu.logoquizitu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.fititu.logoquizitu.Controller.GalleryAdapter
import com.fititu.logoquizitu.Controller.GalleryLayoutManager
import com.fititu.logoquizitu.Model.SortBy
import com.fititu.logoquizitu.View.IGalleryView
import com.fititu.logoquizitu.ViewModels.GalleryViewModel

class GalleryFragment(
    private val countryOfOrigin: String?,
    private val category: String?,
    private val level: Int?,
    private val sortBy: SortBy,
    private val hide: Boolean
) : Fragment(), IGalleryView {
    private lateinit var spinnerCountry: Spinner
    private lateinit var spinnerLevel: Spinner
    private lateinit var spinnerCategory: Spinner

    private lateinit var radioName: RadioButton
    private lateinit var radioAge: RadioButton
    private lateinit var radioLevel: RadioButton
    private lateinit var radioGroup : RadioGroup

    private lateinit var btnApply: Button
    private lateinit var galleryFilter: LinearLayout

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        viewModel = ViewModelProvider(this)[GalleryViewModel::class.java]
        viewModel.getFilterLists()
        viewModel.setFilters(countryOfOrigin, category, level?.toString(), sortBy)
        viewModel.getCompanies()

        spinnerCountry = view.findViewById(R.id.spinner_country)
        spinnerLevel = view.findViewById(R.id.spinner_level)
        spinnerCategory = view.findViewById(R.id.spinner_category)

        radioName = view.findViewById(R.id.radio_name)
        radioAge = view.findViewById(R.id.radio_age)
        radioLevel = view.findViewById(R.id.radio_level)
        radioGroup = view.findViewById(R.id.radio_gallery_group)

        btnApply = view.findViewById(R.id.btn_gallery_apply)
        galleryFilter = view.findViewById(R.id.gallery_filters)

        if (hide) galleryFilter.visibility = View.GONE

        // set filter dropdowns
        val adapterCountryNames: ArrayAdapter<String> =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.countryNames
            )
        adapterCountryNames.setDropDownViewResource(android.R.layout.select_dialog_item)
        val adapterCategoryNames: ArrayAdapter<String> =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                viewModel.categoryNames
            )
        adapterCategoryNames.setDropDownViewResource(android.R.layout.select_dialog_item)
        val adapterLevels: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, viewModel.levels)
        adapterLevels.setDropDownViewResource(android.R.layout.select_dialog_item)
        spinnerCountry.adapter = adapterCountryNames
        spinnerCategory.adapter = adapterCategoryNames
        spinnerLevel.adapter = adapterLevels

        recyclerView = view.findViewById(R.id.recycleView_gallery)

        val adapter = GalleryAdapter(requireContext(), viewModel)
        adapter.view = this
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GalleryLayoutManager(requireContext(), 3)

        setFragmentFilters()

        spinnerCountry.onItemSelectedListener = SpinnerCountryListener()
        spinnerCategory.onItemSelectedListener = SpinnerCategoryListener()
        spinnerLevel.onItemSelectedListener = SpinnerLevelListener()

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.sortBy = when (checkedId) {
                R.id.radio_name -> {
                    SortBy.ALPHABETICALLY
                }
                R.id.radio_age -> {
                    SortBy.AGE
                }
                R.id.radio_level ->{
                    SortBy.LEVEL
                }
                else -> {
                    SortBy.ALPHABETICALLY
                }
            }
        }

        btnApply.setOnClickListener {
            Log.i("apply", "applying filters")
            viewModel.getCompanies()
            (recyclerView.adapter as GalleryAdapter).notifyDataSetChanged()
        }

        return view
    }

    inner class SpinnerCountryListener : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.countryOfOrigin = parent?.getItemAtPosition(position).toString()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    inner class SpinnerCategoryListener : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.category = parent?.getItemAtPosition(position).toString()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    inner class SpinnerLevelListener : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.level = parent?.getItemAtPosition(position).toString()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    private fun setFragmentFilters()
    {
        spinnerCountry.setSelection(viewModel.countryNames.indexOf(viewModel.countryOfOrigin))
        spinnerCategory.setSelection(viewModel.categoryNames.indexOf(viewModel.category))
        spinnerLevel.setSelection(viewModel.levels.indexOf(viewModel.level))

        when (viewModel.sortBy){
            SortBy.ALPHABETICALLY -> {radioName.isChecked = true}
            SortBy.LEVEL -> {radioLevel.isChecked = true}
            SortBy.AGE -> {radioAge.isChecked = true}
        }
    }

    override fun navigateTo(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainMenuFragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()
    }
}
