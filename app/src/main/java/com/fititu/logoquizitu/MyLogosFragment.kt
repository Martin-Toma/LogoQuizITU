package com.fititu.logoquizitu

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fititu.logoquizitu.Controller.IMainMenuController
import com.fititu.logoquizitu.View.ImageAdapter
import com.fititu.logoquizitu.Controller.MainMenuController
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.View.IMainMenuView
import com.fititu.logoquizitu.ViewModels.MyLogosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream


class MyLogosFragment : Fragment(), IMainMenuView {
    // https://stackoverflow.com/questions/62258967/android-best-approach-for-saving-images-in-room-database
    private var plusButton: Button? = null
    private var plusPresenter: IMainMenuController? = null

    private val SELECT_IMAGE_REQUEST = 1
    //private var selectedImagePath: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter

    private lateinit var viewref : View

    private var playPresenter: IMainMenuController? = null

    private lateinit var viewModel : MyLogosViewModel

    //private lateinit var imageView2 : ImageView

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_logos, container, false)
        viewref = view
        plusButton = view.findViewById(R.id.addButton)
        playPresenter = MainMenuController(this)

        plusButton?.setOnClickListener {
            (playPresenter as MainMenuController).onClickButton("toAdd")
        }

        recyclerView = view.findViewById(R.id.photoRecyclerView)
        adapter = ImageAdapter(emptyList(), requireContext()) // Initial empty list

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnEditButtonClickListener(object : ImageAdapter.OnEditButtonClickListener {
            override fun onEditButtonClicked(position: Int, id: Int) {
                Log.d("Check", "onEditButtonClicked was clicked")
                (playPresenter as MainMenuController).editDbFragment("toAdd", id)
            }
        })

        viewModel = ViewModelProvider(this)[MyLogosViewModel::class.java]

        recyclerView.adapter = adapter

        observeViewModel()
        return view
    }

    override fun changeView(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainMenuFragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()
    }
    override fun changeViewWithParam(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainMenuFragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional - Add to back stack
        transaction.commit()
    }
    override fun onResume() {
        super.onResume()

        // update the data when the fragment is resumed - when navigating back
        viewModel.loadPhotoPosts()
    }

    private fun observeViewModel(){
        viewModel.photoList.observe(viewLifecycleOwner) { photoList ->
            adapter.setPhotoList(photoList)
        }
    }
}