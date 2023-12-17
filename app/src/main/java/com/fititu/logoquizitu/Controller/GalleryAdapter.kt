package com.fititu.logoquizitu.Controller
// Author: Ondřej Vrána (xvrana32)

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.GalleryFragment
import com.fititu.logoquizitu.R
import com.fititu.logoquizitu.ViewModels.GalleryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class GalleryAdapter(private val context: Context, private val viewModel: GalleryViewModel) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    lateinit var view: GalleryFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.gallery_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return viewModel.companies.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgButton: ImageView = itemView.findViewById(R.id.gallery_card_img)
        fun bind(position: Int) {
            val currentCompany = viewModel.companies[position]

            if (currentCompany.userCreated) {
                Glide.with(context)
                    .load(currentCompany.imgOriginal)
                    .into(imgButton)
            } else {
                if (currentCompany.solved) {
                    imgButton.setImageResource(currentCompany.imgOriginalRsc)
                } else {
                    imgButton.setImageResource(currentCompany.imgAlteredRsc)
                }
            }

            imgButton.setOnClickListener {
                Log.i("Gallery Card", "Clicked on card on position ${currentCompany.id}")
                viewModel.navigateTo(view, currentCompany)
            }
        }

    }
}