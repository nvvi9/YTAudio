package com.example.ytaudio.ui.view

// class SingleViewTouchableMotionLayout(context: Context, attributeSet: AttributeSet? = null) :
// MotionLayout(context, attributeSet) {
//
// private val viewRect = Rect()
// private val transitionListenerList = mutableListOf<TransitionListener?>()
// private var touchStarted = false
// private val viewToDetectTouch by lazy {
// findViewById<View>(R.id.audio_view_container)
// }
//
// private val gestureDetector =
// GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
// override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
// transitionToEnd()
// return false
// }
// })
//
// init {
// transitionListenerList += object : TransitionListener {
//
// override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
//
// override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
//
// override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
//
// override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
// touchStarted = false
// }
// }
//
// super.setTransitionListener(object : TransitionListener {
//
// override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
//
// override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
//
// override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
// transitionListenerList.filterNotNull().forEach {
// it.onTransitionChange(p0, p1, p2, p3)
// }
// }
//
// override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
// transitionListenerList.filterNotNull().forEach {
// it.onTransitionCompleted(p0, p1)
// }
// }
// })
// }
//
// override fun setTransitionListener(listener: TransitionListener?) {
// transitionListenerList += listener
// }
//
// override fun onTouchEvent(event: MotionEvent?): Boolean =
// event?.let {
// when (it.actionMasked) {
// MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
// touchStarted = false
// return super.onTouchEvent(event)
// }
// }
//
// if (!touchStarted) {
// viewToDetectTouch.getHitRect(viewRect)
// touchStarted = viewRect.contains(it.x.toInt(), it.y.toInt())
// }
//
// touchStarted && super.onTouchEvent(event)
// } ?: super.onTouchEvent(event)
// }