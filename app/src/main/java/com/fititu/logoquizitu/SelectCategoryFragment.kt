package com.fititu.logoquizitu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fititu.logoquizitu.Controller.SelectCategoryAdapter
import com.fititu.logoquizitu.View.ICategoryView
import com.fititu.logoquizitu.ViewModels.SelectCategoryViewModel

class SelectCategoryFragment : Fragment(), ICategoryView {
    private lateinit var viewModel: SelectCategoryViewModel

    private lateinit var recyclerView: RecyclerView
//    private lateinit var categoryDao: CategoryDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_category, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        viewModel = ViewModelProvider(this)[SelectCategoryViewModel::class.java]
        viewModel.getCategories()

        recyclerView = view.findViewById(R.id.recycleView_category)
//        categoryDao = AppDatabase.getInstance(requireContext()).categoryDao()

//        lifecycleScope.launch {
//            val categories : List<CategoryWithCompanies> = categoryDao.getCategoriesWithCompanies()
        val adapter = SelectCategoryAdapter(requireContext(), viewModel)
        adapter.view = this
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
//        }
        return view
    }

    override fun navigateToCategory(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainMenuFragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()
    }
}