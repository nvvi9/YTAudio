package com.example.ytaudio.utils

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import com.google.android.material.animation.ArgbEvaluatorCompat

fun interpolateLinearly(
    start: Float,
    end: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
) = start + fraction * (end - start)

@ColorInt
fun interpolateLinearlyArgb(
    @ColorInt start: Int,
    @ColorInt end: Int,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = false) startFract: Float,
    @FloatRange(from = 0.0, fromInclusive = false, to = 1.0, toInclusive = true) endFract: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fract: Float
) = when {
    fract < startFract -> start
    fract > endFract -> end
    else -> ArgbEvaluatorCompat.getInstance()
        .evaluate((fract - startFract) / (endFract - startFract), start, end)
}

class MultiSpringAddListener(onEnd: (Boolean) -> Unit, vararg springs: SpringAnimation) {

    private val listeners = ArrayList<DynamicAnimation.OnAnimationEndListener>(springs.size)
    private var wasCancelled = false

    init {
        springs.forEach { springAnimation ->
            object : DynamicAnimation.OnAnimationEndListener {
                override fun onAnimationEnd(
                    animation: DynamicAnimation<out DynamicAnimation<*>>?,
                    canceled: Boolean,
                    value: Float,
                    velocity: Float
                ) {
                    animation?.removeEndListener(this)
                    wasCancelled = wasCancelled or canceled
                    listeners.remove(this)
                    if (listeners.isEmpty()) {
                        onEnd(wasCancelled)
                    }
                }
            }.let {
                springAnimation.addEndListener(it)
                listeners.add(it)
            }
        }
    }
}