package com.fititu.logoquizitu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.fititu.logoquizitu.Controller.IMainMenuController
import com.fititu.logoquizitu.Controller.MainMenuController
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.LogoEntity
import com.fititu.logoquizitu.Model.LogoEntityDao
import com.fititu.logoquizitu.Model.LogoEntityDao_Impl
import com.fititu.logoquizitu.View.IMainMenuView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MyLogosFragment : Fragment(), IMainMenuView {

    private var plusButton: Button? = null
    private var plusPresenter: IMainMenuController? = null

    private val SELECT_IMAGE_REQUEST = 1
    private lateinit var logoEntityDao: LogoEntityDao
    private var selectedImagePath: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter

    private lateinit var viewref : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_logos, container, false)
        viewref = view
        //plusButton = view.findViewById(R.id.addButton)
        plusPresenter = MainMenuController(this)

        /*
        plusButton?.setOnClickListener {
            (plusPresenter as MainMenuController).onClickButton("toAdd")
        } */

        logoEntityDao = AppDatabase.getInstance(requireContext()).logoEntityDao()

        recyclerView = view.findViewById(R.id.photoRecyclerView)
        adapter = ImageAdapter(emptyList()) // Initial empty list

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val selectImageButton: Button = view.findViewById(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE_REQUEST)
        }

        val imageButton: Button = view.findViewById(R.id.imageButton)
        imageButton.setOnClickListener {
            val caption = view.findViewById<EditText>(R.id.captionEditText).text.toString()

            if (selectedImagePath.isNotBlank() && caption.isNotBlank()) {
                val photoPost = LogoEntity(imagePath = selectedImagePath, name = caption, description = caption)
                insertPhotoPost(photoPost)
            } else {
                Toast.makeText(requireContext(), "Please select an image and enter a caption", Toast.LENGTH_SHORT).show()
            }
        }

        loadPhotoPosts()
        return view
    }

    override fun changeView(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainMenuFragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri = data.data!!
            selectedImagePath = selectedImageUri.toString()

            val imageView: ImageView = viewref.findViewById(R.id.imageView)
            Glide.with(this)
                .load(selectedImageUri)
                .into(imageView)
        }
    }

    private fun insertPhotoPost(photoPost: LogoEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                logoEntityDao.insert(photoPost)
            }

            Toast.makeText(requireContext(), "Photo post saved successfully", Toast.LENGTH_SHORT).show()
            clearFields()
            loadPhotoPosts()
        }
    }

    private fun clearFields() {
        viewref.findViewById<ImageView>(R.id.imageView).setImageResource(0)
        viewref.findViewById<EditText>(R.id.captionEditText).text.clear()
        selectedImagePath = ""
    }

    private fun loadPhotoPosts() {
        lifecycleScope.launch {
            val photoList = withContext(Dispatchers.IO) {
                logoEntityDao.getAllPhotoPosts()
            }
            adapter.setPhotoList(photoList)
        }
    }
}