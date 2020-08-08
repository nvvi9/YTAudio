package com.example.ytaudio.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use


@ColorInt
fun Context.themeColor(@AttrRes themeAttrId: Int) =
    obtainStyledAttributes(intArrayOf(themeAttrId))
        .use { it.getColor(0, Color.MAGENTA) }

@SuppressLint("Recycle")
fun Context.themeInterpolator(@AttrRes attr: Int): Interpolator =
    AnimationUtils.loadInterpolator(this, obtainStyledAttributes(intArrayOf(attr)).use {
        it.getResourceId(0, android.R.interpolator.fast_out_slow_in)
    })