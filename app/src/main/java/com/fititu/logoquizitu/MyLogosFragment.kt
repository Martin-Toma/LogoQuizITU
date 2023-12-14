package com.fititu.logoquizitu

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.PrimaryKey
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Controller.IMainMenuController
import com.fititu.logoquizitu.Controller.MainMenuController
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.LogoEntity
import com.fititu.logoquizitu.Model.LogoEntityDao
import com.fititu.logoquizitu.View.IMainMenuView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.Date


class MyLogosFragment : Fragment(), IMainMenuView {
    // https://stackoverflow.com/questions/62258967/android-best-approach-for-saving-images-in-room-database
    private var plusButton: Button? = null
    private var plusPresenter: IMainMenuController? = null

    private val SELECT_IMAGE_REQUEST = 1
    private lateinit var logoEntityDao: CompanyDao
    //private var selectedImagePath: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter

    private lateinit var viewref : View

    private var playPresenter: IMainMenuController? = null

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
        //imageView2 = viewref.findViewById(R.id.imageView2)

        plusButton?.setOnClickListener {
            (playPresenter as MainMenuController).onClickButton("toAdd")
        }

        logoEntityDao = AppDatabase.getInstance(requireContext()).companyDao()

        recyclerView = view.findViewById(R.id.photoRecyclerView)
        adapter = ImageAdapter(emptyList()) // Initial empty list

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
/*
        val selectImageButton: Button = view.findViewById(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE_REQUEST)
        }

        val imageButton: Button = view.findViewById(R.id.imageButton)
        imageButton.setOnClickListener {
            val caption = view.findViewById<EditText>(R.id.captionEditText).text.toString()

            if (selectedImagePath.isNotBlank() && caption.isNotBlank()) {
                //val imgBitmap = imageGetBitmap(selectedImagePath)
                /*if(imgBitmap == null){
                    Toast.makeText(requireContext(), "Error decoding the image", Toast.LENGTH_SHORT).show()
                }
                else {*/

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

                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)

                if(inputStream != null){
                    // Copy the data from the input stream to the output stream
                    FileUtils.copy(inputStream, outputStream)
                    //outputStream.getChannel().transferFrom(inputStream, 0, inputStream.size());
                }
                else{
                    Log.e("ERR", "empty input")
                }
                Log.d("ERR", "${uri}")

                val outputPath = file.absolutePath //dir_path.toString() + new_image_file_name;

                /*Glide.with(requireContext())
                    .load(outputPath)
                    .into(imageView2)*/
                outputStream.close()
                Log.d("File name", outputPath)

                // prepare masked image
                //maskLogo(outputPath);

                val photoPost = CompanyEntity(
                    id = 0,
                    imgOriginal = outputPath,//selectedImagePath,
                    companyName = caption,
                    companyDescription = caption, //imageBitmap = imgBitmap
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
                //}
            } else {
                Toast.makeText(requireContext(), "Please select an image and enter a caption", Toast.LENGTH_SHORT).show()
            }
        }*/
        // Clear the database on fragment creation
        /*viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext()).clearAllTables()
            }
        }*/
        loadPhotoPosts()
        return view
    }

    /*fun get_file_type(whole_path : String?) : String?{
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
    }*/
/*
    // convert from bitmap to byte array
    fun getBytes(bitmap: Bitmap?): ByteArray? {
        if(bitmap != null){
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
            return stream.toByteArray()
        }
        return null
    }*/

    /*fun imageGetBitmap(path: String): ByteArray? {
        try {
            val options = BitmapFactory.Options()
            val inputStream = requireContext().contentResolver.openInputStream(Uri.parse(path))
            if(inputStream != null){
                return getBytes(BitmapFactory.decodeStream(inputStream, null, options))
            }
            else{
                Log.e("EMPTY STREAM", "Error: input stream null")
                return null
            }
        } catch (e: Exception){
            Log.e("EMPTY BITMAP", "Error: ${e}")
            return null
        }
    }*/

    override fun changeView(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainMenuFragmentContainer, fragment)
        transaction.addToBackStack(null) // Optional: Add to back stack
        transaction.commit()
    }
/*
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
*//*
    private fun insertPhotoPost(photoPost: CompanyEntity) {
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
*/
    private fun loadPhotoPosts() {
        lifecycleScope.launch {
            val photoList = withContext(Dispatchers.IO) {
                logoEntityDao.getAllPhotoPostsC()
            }
            adapter.setPhotoList(photoList)
        }
    }

    // for masked image
    private fun maskLogo(origPath : String) {
        // Load the image
        val bitmap = BitmapFactory.decodeFile(origPath)

        // Create a mutable bitmap to draw on
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Prepare to draw on the image
        val canvas = Canvas(mutableBitmap)
        val paint = Paint()
        paint.color = Color.RED
        paint.strokeWidth = 10f

        // Draw a line on the image
        canvas.drawLine(0f, 0f, 100f, 100f, paint)

        // Save the image to a new file
        val out = FileOutputStream("path_to_your_new_image_file")
        mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
    }
}