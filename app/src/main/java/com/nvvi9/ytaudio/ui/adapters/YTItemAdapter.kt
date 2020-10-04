package com.nvvi9.ytaudio.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.databinding.ItemYoutubeBinding
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlin.math.abs


class YTItemAdapter(private val listener: YTItemListener) :
    PagingDataAdapter<YouTubeItem, YTItemAdapter.YTItemViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: YTItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YTItemViewHolder =
        YTItemViewHolder(
            ItemYoutubeBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener
        )

    class YTItemViewHolder(
        private val binding: ItemYoutubeBinding,
        listener: YTItemListener
    ) : RecyclerView.ViewHolder(binding.root), ReboundingSwipeActionCallback.ReboundableViewHolder {

        init {
            binding.apply {
                this.listener = listener
                root.background = YTItemSwipeActionDrawable(root.context)
            }
        }

        private val addedCornerSize =
            itemView.resources.getDimension(R.dimen.small_component_corner_radius)

        fun bind(item: YouTubeItem) {
            binding.youTubeItem = item
            binding.root.isActivated = item.isAdded
            swipeEnabled = !item.isAdded
            updateCardViewTopLeftCornerSize(if (item.isAdded || item.inPlaylist) 1f else 0f)
            binding.executePendingBindings()
        }

        override var swipeEnabled: Boolean = false
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
            binding.cardView.apply {
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setBottomLeftCornerSize(interpolation * addedCornerSize)
                    .build()
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<YouTubeItem>() {

        override fun areItemsTheSame(oldItem: YouTubeItem, newItem: YouTubeItem) = false
        override fun areContentsTheSame(oldItem: YouTubeItem, newItem: YouTubeItem) = false
    }
}