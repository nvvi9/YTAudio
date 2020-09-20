package com.nvvi9.ytaudio.utils

import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation


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