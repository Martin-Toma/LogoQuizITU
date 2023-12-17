package com.fititu.logoquizitu.Controller

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GalleryLayoutManager(val context: Context, val colSpan: Int) :
    GridLayoutManager(context, colSpan) {

    override fun checkLayoutParams(lp : RecyclerView.LayoutParams) : Boolean {
        val cardWidthHeight = (width / 3) - 10*2
        lp.height = cardWidthHeight
        lp.width = cardWidthHeight
        return true
    }
}