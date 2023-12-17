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
import com.fititu.logoquizitu.View.ImageAdapter
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

    fun loadPhotoPosts() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                logoEntityDao.getAllPhotoPostsC()
            }
            _photoList.value = list
        }
    }
}