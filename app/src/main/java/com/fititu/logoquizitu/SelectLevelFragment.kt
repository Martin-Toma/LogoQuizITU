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
import com.fititu.logoquizitu.Controller.SelectLevelAdapter
import com.fititu.logoquizitu.View.ILevelView
import com.fititu.logoquizitu.ViewModels.SelectLevelViewModel

class SelectLevelFragment : Fragment(), ILevelView {
    private lateinit var viewModel: SelectLevelViewModel

    private lateinit var recyclerView: RecyclerView
//    private lateinit var levelDao: LevelDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_level, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        viewModel = ViewModelProvider(this)[SelectLevelViewModel::class.java]
        viewModel.getLevels()

        recyclerView = view.findViewById(R.id.recycleView_level)
//        levelDao = AppDatabase.getInstance(requireContext()).levelDao()

//        lifecycleScope.launch {
//            val levels : List<LevelWithCompanies> = levelDao.getLevelsWithCompanies()
        val adapter = SelectLevelAdapter(requireContext(), viewModel)
        adapter.view = this
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
//        }
        return view
    }

    override fun navigateToLevel(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainMenuFragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()
    }
}