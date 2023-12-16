package com.fititu.logoquizitu.ViewModels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.Date

class AddLogoViewModel(application: Application) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel

    @SuppressLint("StaticFieldLeak")
    private val appContext: Context = application.applicationContext
    val logoEntityDao : CompanyDao = AppDatabase.getInstance(appContext).companyDao()

    fun insertPhotoPost(photoPost: CompanyEntity) {
        // coroutine to run db operations nonblocking
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                // add logo to db
                logoEntityDao.insert(photoPost)
                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "Logo saved successfully", Toast.LENGTH_SHORT).show()
                    //requireActivity().onBackPressedDispatcher.onBackPressed() // go back to my fragment
                }

            }

            //clearFields()
        }
    }

    suspend fun updatePhotoPost(photoPost: CompanyEntity) = coroutineScope {
        // Launching a coroutine to run db operations nonblocking
        val job = launch(Dispatchers.IO) {
            // update item in db
            logoEntityDao.update(photoPost)
        }

        // Waiting for the result
        job.join()
        withContext(Dispatchers.Main) {
            onDBUpdateComplete()
        }
    }

    fun onDBUpdateComplete() {
        Toast.makeText(appContext, "Logo edited successfully", Toast.LENGTH_SHORT).show()
        //requireActivity().onBackPressedDispatcher.onBackPressed() // go back to my fragment
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

        val imageView: ImageView = view.findViewById(R.id.imageView)
        Glide.with(appContext)
            .load(editLogo.imgOriginal)
            .into(imageView)
        val nameEditText = view.findViewById<EditText>(R.id.captionEditText)
        val descriptionEditText = view.findViewById<EditText>(R.id.descriptionEditText)

        nameEditText.setText(editLogo.companyName)
        descriptionEditText.setText(editLogo.companyDescription)

    }

    fun delete_file(dirPath: File, fileName : String) : Boolean{
        val fileR = File(dirPath, fileName)

        if (fileR.exists()){
            if(fileR.delete()){
                Log.d("Test out", "File deleted")
                return true
            }
            else{
                Log.d("Test out", "File not deleted")
                return false
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun updateDB(
        caption : String,
        description : String,
        selectedImagePath : String,
        imageNotChanged : Boolean,
        editOn : Boolean,
        lId : Int?)
    {
        Log.d("Test out", "caption = ${caption} desc ${description}")
        if ((selectedImagePath.isNotBlank() && caption.isNotBlank() )|| (imageNotChanged == false)) {
            //val imgBitmap = imageGetBitmap(selectedImagePath)
            /*if(imgBitmap == null){
                Toast.makeText(requireContext(), "Error decoding the image", Toast.LENGTH_SHORT).show()
            }
            else {*/

            var outputPath : String = ""
            if(!imageNotChanged || !editOn) {

                Log.d("IMG", "GOt to image insert")

                val image_file_name = selectedImagePath.toString()
                val new_image_file_name = caption + "." + get_file_type(image_file_name)
                Log.d("File name", new_image_file_name)

                val dir_path = appContext.filesDir
                delete_file(dir_path, new_image_file_name)

                val file = File(dir_path, new_image_file_name)

                // Open an output stream to the destination file
                // Open an output stream to the destination file
                val outputStream: OutputStream = FileOutputStream(file)

                // Copy the data from the input stream to the output stream
                val uri = Uri.parse(selectedImagePath)

                val inputStream: InputStream? =
                    appContext.contentResolver.openInputStream(uri)

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
                val dirPath = appContext.filesDir
                viewModelScope.launch {
                    val item = logoEntityDao.getCompanyById(cId!!)
                    delete_file(dirPath, item.imgOriginal)
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
            Toast.makeText(appContext, "Please select an image and enter a caption", Toast.LENGTH_SHORT).show()
        }
    }
}