package com.fititu.logoquizitu.View

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.FileManagement
import com.fititu.logoquizitu.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class ImageAdapter(
    private var photoList: List<CompanyEntity>,
    private val context : Context ) :
    RecyclerView.Adapter<ImageAdapter.PhotoViewHolder>() {

    interface OnEditButtonClickListener {
        fun onEditButtonClicked(position: Int, id: Int)
    }

    interface OnDeleteButtonClickListener {
        fun onDeleteButtonClicked(position: Int)
    }

    private var editListener: OnEditButtonClickListener? = null
    private var deleteListener: OnDeleteButtonClickListener? = null

    fun setOnEditButtonClickListener(listener: OnEditButtonClickListener) {
        this.editListener = listener
    }
    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemImageView)
        val imageView2: ImageView = itemView.findViewById(R.id.image2)
        val captionTextView: TextView = itemView.findViewById(R.id.itemCaptionTextView)
        val editButton : Button = itemView.findViewById(R.id.editBtn)
        val deleteButton : Button = itemView.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return PhotoViewHolder(itemView)
    }
    fun setOnDeleteButtonClickListener(listener: OnDeleteButtonClickListener) {
        this.deleteListener = listener
    }
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentPhoto = photoList[position]
        val context = holder.itemView.context
        Log.d("Image Loading", "OK ${currentPhoto.imgOriginal} | ${Uri.parse(currentPhoto.imgOriginal)}")
        // Set image and caption
        Glide.with(context)
            .load(currentPhoto.imgOriginal)//Uri.parse(currentPhoto.imagePath))
            .into(holder.imageView)
        Glide.with(context)
            .load(currentPhoto.imgAltered)//Uri.parse(currentPhoto.imagePath))
            .into(holder.imageView2)

        holder.captionTextView.text = currentPhoto.companyName
        holder.editButton.setOnClickListener {
            editListener?.onEditButtonClicked(position, currentPhoto.id)
        }
        holder.deleteButton.setOnClickListener {
            deleteListener?.onDeleteButtonClicked(position)
        }
    }

    // Function to update the photoList in the adapter
    fun setPhotoList(newList: List<CompanyEntity>) {
        photoList = newList.filter{it.userCreated}
        notifyDataSetChanged() // Notify the adapter that the data set has changed
    }
    override fun getItemCount() = photoList.size
}