package com.nvvi9.ytaudio.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.databinding.ItemYoutubeBinding
import com.nvvi9.ytaudio.databinding.ItemYoutubePlaylistBinding
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlin.math.abs

class YTPlaylistItemAdapter(private val listener: YTItemListener) :
        PagingDataAdapter<YouTubeItem, RecyclerView.ViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (it) {
                is YouTubeItem.YouTubeVideoItem -> (holder as YTVideoItemViewHolder).bind(it)
                is YouTubeItem.YouTubePlaylistItem -> (holder as YTPlaylistItemViewHolder).bind(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                VIEW_TYPE_VIDEO -> YTVideoItemViewHolder(ItemYoutubeBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)
                VIEW_TYPE_PLAYLIST -> YTPlaylistItemViewHolder(ItemYoutubePlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)
                else -> super.createViewHolder(parent, viewType)
            }

    override fun getItemViewType(position: Int): Int =
            getItem(position)?.let {
                when (it) {
                    is YouTubeItem.YouTubeVideoItem -> VIEW_TYPE_VIDEO
                    is YouTubeItem.YouTubePlaylistItem -> VIEW_TYPE_PLAYLIST
                }
            } ?: super.getItemViewType(position)


    class YTPlaylistItemViewHolder(private val binding: ItemYoutubePlaylistBinding, listener: YTItemListener) : RecyclerView.ViewHolder(binding.root) {

        private val youTubeVideosAdapter = YTPlaylistItemAdapter(listener)
                .apply { withLoadStateFooter(YTLoadStateAdapter()) }

        init {
            binding.listener = listener
        }

        fun bind(youTubeItem: YouTubeItem.YouTubePlaylistItem) {
            binding.playlistItem = youTubeItem
            binding.executePendingBindings()
        }
    }

    class YTVideoItemViewHolder(
            private val binding: ItemYoutubeBinding,
            listener: YTItemListener
    ) : RecyclerView.ViewHolder(binding.root), ReboundingSwipeActionCallback.ReboundableViewHolder {

        private val addedCornerSize =
                itemView.resources.getDimension(R.dimen.small_component_corner_radius)

        init {
            binding.run {
                this.listener = listener
                root.background = YTItemSwipeActionDrawable(root.context)
            }
        }

        fun bind(youTubeItem: YouTubeItem.YouTubeVideoItem) {
            binding.youTubeItem = youTubeItem
            binding.root.isActivated = youTubeItem.isAdded
            swipeEnabled = !youTubeItem.isAdded
            updateCardViewTopLeftCornerSize(if (youTubeItem.isAdded || youTubeItem.inPlaylist) 1f else 0f)
            binding.executePendingBindings()
        }

        override val reboundableView: View = binding.cardView
        override var swipeEnabled: Boolean = false

        override fun onReboundOffsetChanged(currentSwipePercentage: Float, swipeThreshold: Float, currentTargetHasMetThresholdOnce: Boolean) {
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
            binding.youTubeItem?.let { item ->
                binding.listener?.onItemIconChanged(item, !item.isAdded)
            }
        }

        private fun updateCardViewTopLeftCornerSize(interpolation: Float) {
            binding.cardView.apply {
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                        .setBottomLeftCornerSize(interpolation * addedCornerSize)
                        .build()
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_VIDEO = 1
        private const val VIEW_TYPE_PLAYLIST = 2
    }

    private object DiffCallback : DiffUtil.ItemCallback<YouTubeItem>() {
        override fun areItemsTheSame(oldItem: YouTubeItem, newItem: YouTubeItem): Boolean = false
        override fun areContentsTheSame(oldItem: YouTubeItem, newItem: YouTubeItem): Boolean = false
    }
}