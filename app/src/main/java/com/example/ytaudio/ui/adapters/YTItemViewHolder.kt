package com.example.ytaudio.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.databinding.ItemYoutubeBinding
import com.example.ytaudio.vo.YouTubeItem
import kotlin.math.abs


class YTItemViewHolder(
    private val binding: ItemYoutubeBinding,
    listener: YTItemAdapterListener
) : RecyclerView.ViewHolder(binding.root), ReboundingSwipeActionCallback.ReboundableViewHolder {

    init {
        binding.apply {
            this.listener = listener
            root.background = YTItemSwipeActionDrawable(root.context)
        }
    }

    fun bind(item: YouTubeItem) {
        binding.youTubeItem = item
        binding.root.isActivated = item.isAdded
        updateCardViewTopLeftCornerSize(if (item.isAdded) 1f else 0f)
        binding.executePendingBindings()
    }

    override val reboundableView: View? = binding.cardView

    override fun onReboundOffsetChanged(
        currentSwipePercentage: Float,
        swipeThreshold: Float,
        currentTargetHasMetThresholdOnce: Boolean
    ) {
        if (currentTargetHasMetThresholdOnce) return

        val isAdded = binding.youTubeItem?.isAdded ?: false

        val interpolation = (currentSwipePercentage / swipeThreshold).coerceIn(0f, 1f)
        val adjustInterpolation = abs((if (isAdded) 1f else 0f) - interpolation)
        updateCardViewTopLeftCornerSize(adjustInterpolation)

        val thresholdMet = currentSwipePercentage >= swipeThreshold
        val shouldAdd = when {
            thresholdMet && isAdded -> false
            thresholdMet && !isAdded -> true
            else -> return
        }
        binding.root.isActivated = shouldAdd
    }

    override fun onRebounded() {
        val item = binding.youTubeItem ?: return
        binding.listener?.onItemIconChanged(item, !item.isAdded)
    }

    private fun updateCardViewTopLeftCornerSize(interpolation: Float) {
        binding.cardView?.apply {
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setTopLeftCornerSize(interpolation * 24f)
                .build()
        }
    }
}