package com.fititu.logoquizitu.Controller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.Entity.Relation.CategoryWithCompanies
import com.fititu.logoquizitu.R
import com.fititu.logoquizitu.Model.Entity.Relation.LevelWithCompanies
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileDescriptor
import java.io.FileReader
import java.nio.file.Paths

class SelectCategoryAdapter(private val context : Context, private val categories: List<CategoryWithCompanies>) :
    RecyclerView.Adapter<SelectCategoryAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.select_category_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val btnCategoryCard = itemView.findViewById<CardView>(R.id.category_card)
        private val textCategoryCardName = itemView.findViewById<TextView>(R.id.category_card_name)
        private val textCategoryCardLogos = itemView.findViewById<TextView>(R.id.category_card_logos)
        private val textCategoryCardStars = itemView.findViewById<TextView>(R.id.category_card_stars)
        private val imgCategoryImageView = itemView.findViewById<ImageView>(R.id.category_card_img)

        fun bind(position: Int){
            // update the text inside of this card to reflect the info
            val thisCategory = categories[position]

            var logosSolved = 0
            val logosCount = thisCategory.companies.size
            thisCategory.companies.forEach { if (it.solved) logosSolved++ }

            textCategoryCardName.text = "${thisCategory.category.name}"
            textCategoryCardLogos.text = "$logosSolved / $logosCount logos"
            textCategoryCardStars.text = "${logosSolved*3} / ${logosCount*3} stars"

            imgCategoryImageView.setImageResource(thisCategory.category.imgCategory)

            btnCategoryCard.setOnClickListener{
                Log.i("Category card", "clicked on category card $position")
                // TODO call the gallery fragment change from here
            }
        }
    }
}