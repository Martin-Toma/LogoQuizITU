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
import com.fititu.logoquizitu.Controller.SelectLevelAdapter
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.LevelDao
import com.fititu.logoquizitu.Model.Entity.Relation.LevelWithCompanies
import kotlinx.coroutines.launch

class SelectLevelFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var levelDao: LevelDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_level, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        recyclerView = view.findViewById(R.id.recycleView_level)
        levelDao = AppDatabase.getInstance(requireContext()).levelDao()

        lifecycleScope.launch {
            val levels : List<LevelWithCompanies> = levelDao.getLevelsWithCompanies()
            recyclerView.adapter = SelectLevelAdapter(requireContext(), levels)
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        }
        return view
    }
}