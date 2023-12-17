package com.fititu.logoquizitu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Controller.IMainMenuController
import com.fititu.logoquizitu.Controller.MainMenuController
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.Date
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import com.fititu.logoquizitu.ViewModels.AddLogoViewModel
import com.fititu.logoquizitu.databinding.FragmentAddLogoBinding
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class AddLogoFragment : Fragment() {

    companion object {
        fun newInstance(param1: Int): AddLogoFragment {
            val fragment = AddLogoFragment()
            val args = Bundle()
            args.putInt("ARG_PARAM1", param1)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var binding: FragmentAddLogoBinding
    private lateinit var viewModel: AddLogoViewModel

    private var plusButton: Button? = null

    private val SELECT_IMAGE_REQUEST = 1
    private lateinit var logoEntityDao: CompanyDao
    private var selectedImagePath: String = ""
    private lateinit var viewref : View

    private var lId: Int? = null

    private var editOn: Boolean = false

    private var imageNotChanged = true

    private lateinit var backButton : Button

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentAddLogoBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_add_logo, container, false)
        viewref = view
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AddLogoViewModel::class.java]


        logoEntityDao = AppDatabase.getInstance(requireContext()).companyDao()

        // check if now is edit mode
        //var editOn : Boolean = false
        if (arguments != null) {
            Log.d("Check", "got id")
            editOn = true
            val logoId = requireArguments().getInt("ARG_PARAM1")
            lId = logoId
            lifecycleScope.launch{
                viewModel.initView(logoId, view)
            }
        }

        val descriptionEditText : EditText = view.findViewById(R.id.descriptionEditText)

        val selectImageButton: Button = view.findViewById(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE_REQUEST)
        }

        val imageButton: Button = view.findViewById(R.id.imageButton)
        imageButton.setOnClickListener {
            val caption = view.findViewById<EditText>(R.id.captionEditText).text.toString()
            val description : String = descriptionEditText.text.toString()
            if(viewModel.updateDB(caption, description, selectedImagePath, imageNotChanged, editOn, lId, requireActivity())){
                requireActivity().onBackPressedDispatcher.onBackPressed() // go back to my fragment
            }
        }
        // Clear the database on fragment creation
        /*viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext()).clearAllTables()
            }
        }*/
        // Inflate the layout for this fragment

        backButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // go back to menu fragment
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
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

            imageNotChanged = false // signalize there is new image
        }
    }
}