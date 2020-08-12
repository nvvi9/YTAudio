package com.example.ytaudio.ui.view

import android.content.Context
import android.content.res.Resources
import android.view.View


class StatusBarView(context: Context) : View(context) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), getHeight(resources))
    }

    companion object {
        fun getHeight(resources: Resources) =
            resources.getIdentifier("status_bar_height", "dimen", "android")
                .takeIf { it > 0 }?.let { resources.getDimensionPixelSize(it) } ?: 0
    }
}