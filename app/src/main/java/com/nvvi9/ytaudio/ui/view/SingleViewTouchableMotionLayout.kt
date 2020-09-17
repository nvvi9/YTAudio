package com.nvvi9.ytaudio.ui.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import com.nvvi9.ytaudio.R


class SingleViewTouchableMotionLayout(context: Context, attributeSet: AttributeSet? = null) :
    MotionLayout(context, attributeSet) {

    private val viewToDetectTouch by lazy {
        findViewById<View>(R.id.player_toolbar)
    }

    private val viewRect = Rect()
    private var touchStarted = false
    private val transitionListenerList = mutableListOf<TransitionListener?>().apply {
        add(object : TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                touchStarted = false
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }

    init {
        super.setTransitionListener(object : TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                transitionListenerList.filterNotNull().forEach {
                    it.onTransitionChange(p0, p1, p2, p3)
                }
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                transitionListenerList.filterNotNull().forEach {
                    it.onTransitionCompleted(p0, p1)
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }

    override fun setTransitionListener(listener: TransitionListener?) {
        transitionListenerList += listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchStarted = false
                return super.onTouchEvent(event)
            }
        }
        if (!touchStarted) {
            viewToDetectTouch.getHitRect(viewRect)
            touchStarted = viewRect.contains(event.x.toInt(), event.y.toInt())
        }

        return touchStarted && super.onTouchEvent(event)
    }
}