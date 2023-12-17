package com.fititu.logoquizitu.Controller
// Author: Ondřej Vrána (xvrana32)

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.fititu.logoquizitu.R
import com.fititu.logoquizitu.SelectLevelFragment
import com.fititu.logoquizitu.ViewModels.SelectLevelViewModel

class SelectLevelAdapter(
    private val context: Context,
    private val viewModel: SelectLevelViewModel
) :
    RecyclerView.Adapter<SelectLevelAdapter.ViewHolder>() {
    lateinit var view: SelectLevelFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.select_level_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return viewModel.levels.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnLevelCard = itemView.findViewById<CardView>(R.id.level_card)
        private val textLevelCardName = itemView.findViewById<TextView>(R.id.level_card_name)
        private val textLevelCardLogos = itemView.findViewById<TextView>(R.id.level_card_logo_count)
        private val textLevelCardStars = itemView.findViewById<TextView>(R.id.level_card_stars)

        fun bind(position: Int) {
            // update the text inside of this card to reflect the info
            val thisLevel = viewModel.levels[position]

            var logosSolved = 0
            val logosCount = thisLevel.companies.size
            thisLevel.companies.forEach { if (it.solved) logosSolved++ }

            textLevelCardName.text = "Level ${thisLevel.levelEntity.id}"
            textLevelCardLogos.text = "$logosSolved / $logosCount logos"
            textLevelCardStars.text = "${logosSolved * 3} / ${logosCount * 3} stars"

            btnLevelCard.setOnClickListener {
                Log.i("Level card", "clicked on level card $position")
                viewModel.navigateToLevel(view, thisLevel.levelEntity.id)
            }
        }
    }
}