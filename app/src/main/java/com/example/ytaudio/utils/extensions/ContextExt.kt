package com.example.ytaudio.utils.extensions

import android.content.Context
import android.graphics.Color
import android.view.animation.AnimationUtils
import androidx.annotation.AttrRes
import androidx.core.content.res.use


fun Context.themeColor(@AttrRes themeAttrId: Int) =
    obtainStyledAttributes(intArrayOf(themeAttrId))
        .use { it.getColor(0, Color.MAGENTA) }

fun Context.themeInterpolator(@AttrRes attr: Int) =
    AnimationUtils.loadInterpolator(this, obtainStyledAttributes(intArrayOf(attr)).use {
        it.getResourceId(0, android.R.interpolator.fast_out_slow_in)
    })