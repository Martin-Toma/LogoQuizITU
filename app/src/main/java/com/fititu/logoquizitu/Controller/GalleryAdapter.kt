package com.fititu.logoquizitu.Controller
// Author: Ondřej Vrána (xvrana32)

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.fititu.logoquizitu.GalleryFragment
import com.fititu.logoquizitu.R
import com.fititu.logoquizitu.ViewModels.GalleryViewModel

class GalleryAdapter(private val context: Context, private val viewModel : GalleryViewModel)
    :RecyclerView.Adapter<GalleryAdapter.ViewHolder>()
{
    lateinit var view:GalleryFragment

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

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        private val imgButton:ImageButton = itemView.findViewById(R.id.gallery_card_img)
        fun bind(position: Int) {
            val currentCompany = viewModel.companies[position]

            if (currentCompany.userCreated){
                // TODO set the img using the method for displaying user defined images
            }
            else{
                if (currentCompany.solved){
                    imgButton.setImageResource(currentCompany.imgOriginalRsc)
                }
                else{
                    imgButton.setImageResource(currentCompany.imgAlteredRsc)
                }
            }

            imgButton.setOnClickListener {
                Log.i("Gallery Card", "Clicked on card on position ${currentCompany.id}")
                viewModel.navigateTo(view, currentCompany.id)
            }
        }

    }
}