package com.fititu.logoquizitu

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fititu.logoquizitu.Model.LogoEntity
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.Entity.CompanyEntity

class ImageAdapter(private var photoList: List<CompanyEntity>) :
    RecyclerView.Adapter<ImageAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemImageView)
        val captionTextView: TextView = itemView.findViewById(R.id.itemCaptionTextView)
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
    }

    // Function to update the photoList in the adapter
    fun setPhotoList(newList: List<CompanyEntity>) {
        photoList = newList
        notifyDataSetChanged() // Notify the adapter that the data set has changed
    }

    override fun getItemCount() = photoList.size
}