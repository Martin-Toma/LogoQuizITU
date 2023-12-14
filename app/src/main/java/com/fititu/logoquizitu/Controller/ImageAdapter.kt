package com.fititu.logoquizitu.Controller

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class ImageAdapter(
    private var photoList: List<CompanyEntity>,
    private val context : Context ) :
    RecyclerView.Adapter<ImageAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemImageView)
        val captionTextView: TextView = itemView.findViewById(R.id.itemCaptionTextView)
        val editButton : Button = itemView.findViewById(R.id.editBtn)
        val deleteButton : Button = itemView.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentPhoto = photoList[position]
        val context = holder.itemView.context
        Log.d("Image Loading", "OK ${currentPhoto.imgOriginal} | ${Uri.parse(currentPhoto.imgOriginal)}")
        // Set image and caption
        Glide.with(context)
            .load(currentPhoto.imgOriginal)//Uri.parse(currentPhoto.imagePath))
            .into(holder.imageView)

        holder.captionTextView.text = currentPhoto.companyName
        holder.editButton.setOnClickListener {

        }
        holder.deleteButton.setOnClickListener {
            val itemToDelete = photoList[position]

            // Run the delete operation in a coroutine
            CoroutineScope(Dispatchers.IO).launch {
                val companyDao = AppDatabase.getInstance(context).companyDao()
                companyDao.delete(itemToDelete)

                withContext(Dispatchers.Main) {
                    // Update the RecyclerView
                    photoList = companyDao.getAll()
                    notifyDataSetChanged()
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Function to update the photoList in the adapter
    fun setPhotoList(newList: List<CompanyEntity>) {
        photoList = newList
        notifyDataSetChanged() // Notify the adapter that the data set has changed
    }
    override fun getItemCount() = photoList.size
}