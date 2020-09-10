package com.example.ytaudio.ui.adapters

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.ln


class ReboundingSwipeActionCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    interface ReboundableViewHolder {

        val reboundableView: View?

        fun onReboundOffsetChanged(
            currentSwipePercentage: Float,
            swipeThreshold: Float,
            currentTargetHasMetThresholdOnce: Boolean
        )

        fun onRebounded()
    }

    private var currentTargetPosition: Int = -1
    private var currentTargetHasMetThresholdOnce = false

    companion object {
        private const val swipeReboundingElasticity = 0.8f
        private const val trueSwipeThreshold = 0.4f
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float =
        Float.MAX_VALUE

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float =
        Float.MAX_VALUE

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float =
        Float.MAX_VALUE

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        if (currentTargetHasMetThresholdOnce && viewHolder is ReboundableViewHolder) {
            currentTargetHasMetThresholdOnce = false
            viewHolder.onRebounded()
        }
        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder !is ReboundableViewHolder) return
        if (currentTargetPosition != viewHolder.adapterPosition) {
            currentTargetPosition = viewHolder.adapterPosition
            currentTargetHasMetThresholdOnce = false
        }

        val itemView = viewHolder.itemView
        val currentSwipePercentage = abs(dX) / itemView.width
        viewHolder.onReboundOffsetChanged(
            currentSwipePercentage,
            trueSwipeThreshold,
            currentTargetHasMetThresholdOnce
        )

        val swipeDismissDistanceHorizontal = itemView.width * trueSwipeThreshold
        val dragFraction = ln((1 + (dX / swipeDismissDistanceHorizontal)).toDouble()) / ln(3.0)
        val dragTo = dragFraction * swipeDismissDistanceHorizontal * swipeReboundingElasticity

        viewHolder.reboundableView?.translationX = dragTo.toFloat()

        if (currentSwipePercentage >= trueSwipeThreshold && !currentTargetHasMetThresholdOnce) {
            currentTargetHasMetThresholdOnce = true
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
}