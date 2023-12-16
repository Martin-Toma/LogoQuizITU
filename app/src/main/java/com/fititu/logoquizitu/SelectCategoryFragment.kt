package com.fititu.logoquizitu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fititu.logoquizitu.Controller.SelectCategoryAdapter
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CategoryDao
import com.fititu.logoquizitu.Model.Entity.Relation.CategoryWithCompanies
import kotlinx.coroutines.launch

class SelectCategoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryDao: CategoryDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_category, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        recyclerView = view.findViewById(R.id.recycleView_category)
        categoryDao = AppDatabase.getInstance(requireContext()).categoryDao()

        lifecycleScope.launch {
            val categories : List<CategoryWithCompanies> = categoryDao.getCategoriesWithCompanies()
            recyclerView.adapter = SelectCategoryAdapter(requireContext(), categories)
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        }
        return view
    }
}