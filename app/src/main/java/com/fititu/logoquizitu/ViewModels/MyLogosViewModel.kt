/* Project: Logo Quiz ITU
* Author: Martin Tomasovic
* Last edit: 17.12.2023
* */
package com.fititu.logoquizitu.ViewModels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.FileManagement
import com.fititu.logoquizitu.View.ImageAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyLogosViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val appContext: Context = application.applicationContext
    private var logoEntityDao: CompanyDao = AppDatabase.getInstance(appContext).companyDao()
    private val adapter = ImageAdapter(emptyList(), appContext)

    // Define LiveData or callbacks as needed
    private val _photoList = MutableLiveData<List<CompanyEntity>>()
    val photoList: LiveData<List<CompanyEntity>> get() = _photoList

    init {
        loadPhotoPosts()
    }

    // loads entity from db posts it as LiveData
    fun loadPhotoPosts() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                logoEntityDao.getAllPhotoPostsC()
            }
            _photoList.value = list
        }
    }

    // deletes logo from db and file
    fun onDelete(position : Int){

        // Run the delete operation in a coroutine
        viewModelScope.launch {
            val itemToDelete = _photoList.value?.get(position)
            val companyDao = AppDatabase.getInstance(appContext).companyDao()
            if(itemToDelete != null){
                //if(removeLogoImgFiles(itemToDelete)){
                removeLogoImgFiles(itemToDelete)
                companyDao.delete(itemToDelete)
                loadPhotoPosts()
            }

        }
    }

    private fun removeLogoImgFiles(itemToDelete: CompanyEntity): Boolean {
        val fileMan = FileManagement()
        return (fileMan.delete_file(appContext.filesDir, itemToDelete.imgOriginal, appContext)
                && fileMan.delete_file(appContext.filesDir, itemToDelete.imgAltered, appContext))
    }
}