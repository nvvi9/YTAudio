package com.example.ytaudio.ui.adapters

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.example.ytaudio.R
import com.example.ytaudio.utils.extensions.themeColor
import com.example.ytaudio.utils.extensions.themeInterpolator
import com.example.ytaudio.utils.interpolateLinearly
import com.example.ytaudio.utils.interpolateLinearlyArgb
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sin


class YTItemSwipeActionDrawable(context: Context) : Drawable() {

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.themeColor(R.attr.colorSecondary)
        style = Paint.Style.FILL
    }

    private val circle = RectF()
    private var cx = 0f
    private var cr = 0f

    private val icon = AppCompatResources.getDrawable(context, R.drawable.done_white)

    private val iconMargin = 32f
    private val iconIntrinsicWidth = icon?.intrinsicWidth ?: 0
    private val iconIntrinsicHeight = icon?.intrinsicHeight ?: 0

    private val iconTint = context.themeColor(R.attr.colorOnBackground)
    private val iconTintActive = context.themeColor(R.attr.colorOnSecondary)

    private val iconMaxScaleAddition = 0.5f

    private var progress = 0f
        set(value) {
            value.coerceIn(0f, 1f).takeIf { it != field }?.let {
                field = it
                callback?.invalidateDrawable(this)
            }
        }

    private var progressAnim: ValueAnimator? = null
    private val motionDuration = 255
    private val themeInterpolator = context.themeInterpolator(R.attr.motionInterpolatorPersistent)

    override fun onBoundsChange(bounds: Rect?) {
        bounds?.let {
            circle.set(
                this.bounds.left.toFloat(),
                this.bounds.top.toFloat(),
                this.bounds.right.toFloat(),
                this.bounds.bottom.toFloat()
            )

            cx = circle.left + iconMargin + iconIntrinsicWidth / 2f
            cr = hypot(circle.right - iconMargin - iconIntrinsicWidth / 2f, circle.height() / 2f)
            callback?.invalidateDrawable(this)
        }
    }

    override fun onStateChange(state: IntArray?): Boolean {
        val initProgress = progress
        val newProgress = if (state?.contains(android.R.attr.state_activated) == true) {
            1f
        } else {
            0f
        }

        progressAnim?.cancel()
        progressAnim = ValueAnimator.ofFloat(initProgress, newProgress).apply {
            addUpdateListener {
                progress = animatedValue as Float
            }
            interpolator = themeInterpolator
            duration = (abs(newProgress - initProgress) * motionDuration).toLong()
        }
        progressAnim?.start()
        return initProgress == newProgress
    }


    override fun draw(canvas: Canvas) {
        canvas.drawCircle(cx, circle.centerY(), cr * progress, circlePaint)

        val scaleFactor =
            1 + (sin(interpolateLinearly(0f, PI.toFloat(), progress)) * iconMaxScaleAddition)
                .toDouble().coerceIn(0.0, 1.0)
        icon?.apply {
            setBounds(
                (cx - iconIntrinsicWidth / 2f * scaleFactor).toInt(),
                (circle.centerY() - iconIntrinsicHeight / 2f * scaleFactor).toInt(),
                (cx + iconIntrinsicHeight / 2f * scaleFactor).toInt(),
                (circle.centerY() + iconIntrinsicHeight / 2f * scaleFactor).toInt()
            )

            setTint(interpolateLinearlyArgb(iconTint, iconTintActive, 0f, 0.15f, progress))
            draw(canvas)
        }
    }

    override fun isStateful(): Boolean = true

    override fun setAlpha(alpha: Int) {
        circlePaint.alpha = alpha
    }

    override fun getOpacity(): Int =
        PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        circlePaint.colorFilter = colorFilter
    }
}