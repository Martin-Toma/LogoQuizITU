package com.fititu.logoquizitu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
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

    private var plusButton: Button? = null

    private val SELECT_IMAGE_REQUEST = 1
    private lateinit var logoEntityDao: CompanyDao
    private var selectedImagePath: String = ""
    private lateinit var viewref : View

    private var lId: Int? = null

    private var editOn: Boolean = false

    private var imageNotChanged = true

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_logo, container, false)
        viewref = view

        logoEntityDao = AppDatabase.getInstance(requireContext()).companyDao()

        // check if now is edit mode
        //var editOn : Boolean = false
        if (arguments != null) {
            Log.d("Check", "got id")
            editOn = true
            val logoId = requireArguments().getInt("ARG_PARAM1")
            lId = logoId
            lifecycleScope.launch{
                initView(logoId, view)
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

            if ((selectedImagePath.isNotBlank() && caption.isNotBlank()) || imageNotChanged == true) {
                //val imgBitmap = imageGetBitmap(selectedImagePath)
                /*if(imgBitmap == null){
                    Toast.makeText(requireContext(), "Error decoding the image", Toast.LENGTH_SHORT).show()
                }
                else {*/
                var outputPath : String = ""
                if(!imageNotChanged || !editOn) {

                    val image_file_name = selectedImagePath.toString()
                    val new_image_file_name = caption + "." + get_file_type(image_file_name)
                    Log.d("File name", new_image_file_name)

                    val dir_path = requireContext().filesDir
                    val file = File(dir_path, new_image_file_name)

                    // Open an output stream to the destination file
                    // Open an output stream to the destination file
                    val outputStream: OutputStream = FileOutputStream(file)

                    // Copy the data from the input stream to the output stream
                    val uri = Uri.parse(selectedImagePath)

                    val inputStream: InputStream? =
                        requireContext().contentResolver.openInputStream(uri)

                    if (inputStream != null) {
                        // Copy the data from the input stream to the output stream
                        FileUtils.copy(inputStream, outputStream)
                        //outputStream.getChannel().transferFrom(inputStream, 0, inputStream.size());
                    } else {
                        Log.e("ERR", "empty input")
                    }
                    Log.d("ERR", "${uri}")

                    val outPath = file.absolutePath //dir_path.toString() + new_image_file_name;
                    outputPath = outPath
                    /*Glide.with(requireContext())
                    .load(outputPath)
                    .into(imageView2)*/
                    outputStream.close()
                    Log.d("File name", outputPath)
                }
                // prepare masked image
                //maskLogo(outputPath);
                if(editOn){
                    val cId = lId
                    lifecycleScope.launch {
                        val item = logoEntityDao.getCompanyById(cId!!)
                        if(!imageNotChanged){ // change image path if new
                            item.imgOriginal = outputPath
                            item.imgAltered = outputPath
                        }
                        item.companyName = caption
                        item.companyDescription = description
                        item.solved = false
                        item.foundationDate = Date()
                        item.categoryName = null
                        item.countryOfOriginName = null
                        item.gameState = null
                        item.levelId = null

                        updatePhotoPost(item)
                    }

                }
                else{
                    val photoPost = CompanyEntity(
                        id = 0,
                        imgOriginal = outputPath,//selectedImagePath,
                        companyName = caption,
                        companyDescription = description, //imageBitmap = imgBitmap
                        solved = false,
                        imgAltered = outputPath,
                        foundationDate = Date(),
                        userCreated = true,
                        categoryName = null,
                        countryOfOriginName = null,
                        gameState = null,
                        levelId = null
                    )
                    insertPhotoPost(photoPost)
                }

            } else {
                Toast.makeText(requireContext(), "Please select an image and enter a caption", Toast.LENGTH_SHORT).show()
            }

        }
        // Clear the database on fragment creation
        /*viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext()).clearAllTables()
            }
        }*/
        // Inflate the layout for this fragment
        return view
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
    /*private fun clearFields() {
        viewref.findViewById<ImageView>(R.id.imageView).setImageResource(0)
        viewref.findViewById<EditText>(R.id.captionEditText).text.clear()
        selectedImagePath = ""
    }*/
    private fun insertPhotoPost(photoPost: CompanyEntity) {
        // coroutine to run db operations nonblocking
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                 // add logo to db
                logoEntityDao.insert(photoPost)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Logo saved successfully", Toast.LENGTH_SHORT).show()
                    //requireActivity().onBackPressedDispatcher.onBackPressed() // go back to my fragment
                }

            }

            //clearFields()
        }
    }

    private fun updatePhotoPost(photoPost: CompanyEntity) {
        // coroutine to run db operations nonblocking
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // update item in db
                logoEntityDao.update(photoPost)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Logo edited successfully", Toast.LENGTH_SHORT).show()
                   // requireActivity().onBackPressedDispatcher.onBackPressed() // go back to my fragment
                }
            }
            //clearFields()
        }
    }

    fun get_file_type(whole_path : String?) : String?{
        val uri = Uri.parse(whole_path)

        // Get the path from the URI
        val path = uri.path ?: return null

        // Use File to get the file name
        val file = File(path)

        // Get the file name
        val fileName = file.name

        // Split the file name by dot to get the parts
        val parts = fileName.split(".")

        // The last part will be the file extension
        return if (parts.size > 1) {
            parts.last()
        } else {
            return null
        }
    }
    suspend fun initView(logoId: Int, view: View){
         val editLogo = logoEntityDao.getCompanyById(logoId)

        val imageView: ImageView = viewref.findViewById(R.id.imageView)
        Glide.with(this)
            .load(editLogo.imgOriginal)
            .into(imageView)
        val nameEditText = view.findViewById<EditText>(R.id.captionEditText)
        val descriptionEditText = view.findViewById<EditText>(R.id.descriptionEditText)

        nameEditText.setText(editLogo.companyName)
        descriptionEditText.setText(editLogo.companyDescription)

    }
}