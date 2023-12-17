package com.fititu.logoquizitu.Controller
// Author: Ondřej Vrána (xvrana32)

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.fititu.logoquizitu.R
import com.fititu.logoquizitu.SelectCategoryFragment
import com.fititu.logoquizitu.ViewModels.SelectCategoryViewModel

class SelectCategoryAdapter(
    private val context: Context,
    private val viewModel: SelectCategoryViewModel
) :
    RecyclerView.Adapter<SelectCategoryAdapter.ViewHolder>() {
    lateinit var view: SelectCategoryFragment
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.select_category_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return viewModel.categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnCategoryCard = itemView.findViewById<CardView>(R.id.category_card)
        private val textCategoryCardName = itemView.findViewById<TextView>(R.id.category_card_name)
        private val textCategoryCardLogos =
            itemView.findViewById<TextView>(R.id.category_card_logos)
        private val textCategoryCardStars =
            itemView.findViewById<TextView>(R.id.category_card_stars)
        private val imgCategoryImageView = itemView.findViewById<ImageView>(R.id.category_card_img)

        fun bind(position: Int) {
            // update the text inside of this card to reflect the info
            val thisCategory = viewModel.categories[position]

            var logosSolved = 0
            val logosCount = thisCategory.companies.size
            thisCategory.companies.forEach { if (it.solved) logosSolved++ }

            textCategoryCardName.text = "${thisCategory.category.name}"
            textCategoryCardLogos.text = "$logosSolved / $logosCount logos"
            textCategoryCardStars.text = "${logosSolved * 3} / ${logosCount * 3} stars"

            imgCategoryImageView.setImageResource(thisCategory.category.imgCategory)

            btnCategoryCard.setOnClickListener {
                Log.i("Category card", "clicked on category card $position")
                viewModel.navigateToCategory(view, thisCategory.category.name)
            }
        }
    }
}