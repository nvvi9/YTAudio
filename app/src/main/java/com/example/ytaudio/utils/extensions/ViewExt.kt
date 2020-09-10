package com.example.ytaudio.utils.extensions

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.animation.doOnEnd
import androidx.core.view.drawToBitmap
import com.google.android.material.bottomnavigation.BottomNavigationView


fun View.hideKeyboard() =
    (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(windowToken, 0)

fun View.showKeyboard() {
    (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun EditText.showKeyboard() {
    (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun BottomNavigationView.show() {
    if (visibility == VISIBLE) return

    val parent = parent as ViewGroup
    if (!isLaidOut) {
        measure(
            MeasureSpec.makeMeasureSpec(parent.width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(parent.height, MeasureSpec.AT_MOST)
        )
        layout(parent.left, parent.height - measuredHeight, parent.right, parent.height)
    }

    val drawable = BitmapDrawable(context.resources, drawToBitmap())
    drawable.setBounds(left, parent.height, right, parent.height + height)
    parent.overlay.add(drawable)
    ValueAnimator.ofInt(parent.height, top).apply {
        startDelay = 100L
        duration = 300L
        interpolator =
            AnimationUtils.loadInterpolator(context, android.R.interpolator.linear_out_slow_in)
        addUpdateListener {
            val newTop = it.animatedValue as Int
            drawable.setBounds(left, newTop, right, newTop + height)
        }
        doOnEnd {
            parent.overlay.remove(drawable)
            visibility = VISIBLE
        }
        start()
    }
}

fun BottomNavigationView.hide() {
    if (visibility == GONE) return

    val drawable = BitmapDrawable(context.resources, drawToBitmap())
    val parent = parent as ViewGroup
    drawable.setBounds(left, top, right, bottom)
    parent.overlay.add(drawable)
    visibility = GONE

    ValueAnimator.ofInt(top, parent.height).apply {
        startDelay = 100L
        duration = 200L
        interpolator =
            AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_linear_in)

        addUpdateListener {
            val newTop = it.animatedValue as Int
            drawable.setBounds(left, newTop, right, newTop + height)
        }

        doOnEnd {
            parent.overlay.remove(drawable)
        }
        start()
    }
}
