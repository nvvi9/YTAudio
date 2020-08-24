package com.example.ytaudio.ui.adapters

import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ytaudio.R
import com.example.ytaudio.vo.PlaylistItem
import com.example.ytaudio.vo.YouTubeItem


@BindingAdapter("audioTitle")
fun TextView.setTitle(item: PlaylistItem?) {
    text = item?.title
}

@BindingAdapter("authorName")
fun TextView.setAuthor(item: PlaylistItem?) {
    text = item?.author
}

@BindingAdapter("audioPhoto")
fun ImageView.setImage(item: PlaylistItem?) {
    item?.let {
        Glide.with(context)
            .load(it.thumbnailUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_notification)
                    .error(R.drawable.ic_notification)
            ).into(this)
    }
}

@BindingAdapter("playbackState")
fun ImageView.setPlaybackState(item: PlaylistItem?) {
    item?.let {
        this.setImageResource(it.playbackState)
    }
}

@BindingAdapter("audioDuration")
fun TextView.setDuration(item: PlaylistItem?) {
    text = DateUtils.formatElapsedTime(item?.duration ?: 0)
}

@BindingAdapter("audioPlaylist")
fun RecyclerView.setPlaylist(playlist: List<PlaylistItem>?) {
    (adapter as PlaylistAdapter).submitList(playlist?.sortedBy { it.title })
}

@BindingAdapter("stringList")
fun RecyclerView.setStringList(items: List<String>?) {
    (adapter as SearchAutocompleteAdapter).submitList(items)
}

@BindingAdapter("videoTitle")
fun TextView.setVideoTitle(item: YouTubeItem?) {
    text = item?.title
}

@BindingAdapter("channelTitle")
fun TextView.setChannelTitle(item: YouTubeItem?) {
    text = item?.channelTitle
}

@BindingAdapter("videoThumbnail")
fun ImageView.setThumbnail(item: YouTubeItem?) {
    item?.let {
        Glide.with(context)
            .load(it.thumbnailUri)
            .into(this)
    }
}

@BindingAdapter("layoutFullscreen")
fun View.bindLayoutFullscreen(previousFullscreen: Boolean, fullscreen: Boolean) {
    if (previousFullscreen != fullscreen && fullscreen) {
        systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
}

@BindingAdapter(
    "paddingLeftSystemWindowInsets",
    "paddingTopSystemWindowInsets",
    "paddingRightSystemWindowInsets",
    "paddingBottomSystemWindowInsets",
    requireAll = false
)
fun View.applySystemWindowInsetsPadding(
    previousApplyLeft: Boolean,
    previousApplyTop: Boolean,
    previousApplyRight: Boolean,
    previousApplyBottom: Boolean,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    if (previousApplyLeft == applyLeft &&
        previousApplyTop == applyTop &&
        previousApplyRight == applyRight &&
        previousApplyBottom == applyBottom
    ) {
        return
    }

    doOnApplyWindowInsets { view, insets, padding, _ ->
        val left = if (applyLeft) insets.systemWindowInsetLeft else 0
        val top = if (applyTop) insets.systemWindowInsetTop else 0
        val right = if (applyRight) insets.systemWindowInsetRight else 0
        val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

        view.setPadding(
            padding.left + left,
            padding.top + top,
            padding.right + right,
            padding.bottom + bottom
        )
    }
}

@BindingAdapter(
    "marginLeftSystemWindowInsets",
    "marginTopSystemWindowInsets",
    "marginRightSystemWindowInsets",
    "marginBottomSystemWindowInsets",
    requireAll = false
)
fun View.applySystemWindowInsetsMargin(
    previousApplyLeft: Boolean,
    previousApplyTop: Boolean,
    previousApplyRight: Boolean,
    previousApplyBottom: Boolean,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    if (previousApplyLeft == applyLeft &&
        previousApplyTop == applyTop &&
        previousApplyRight == applyRight &&
        previousApplyBottom == applyBottom
    ) {
        return
    }

    doOnApplyWindowInsets { view, insets, _, margin ->
        val left = if (applyLeft) insets.systemWindowInsetLeft else 0
        val top = if (applyTop) insets.systemWindowInsetTop else 0
        val right = if (applyRight) insets.systemWindowInsetRight else 0
        val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = margin.left + left
            topMargin = margin.top + top
            rightMargin = margin.right + right
            bottomMargin = margin.bottom + bottom
        }
    }
}

fun View.doOnApplyWindowInsets(block: (View, WindowInsets, InitialPadding, InitialMargin) -> Unit) {
    val initialPadding = InitialPadding.recordForView(this)
    val initialMargin = InitialMargin.recordForView(this)

    setOnApplyWindowInsetsListener { v, insets ->
        block(v, insets, initialPadding, initialMargin)
        insets
    }

    requestApplyInsetsWhenAttached()
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

            override fun onViewAttachedToWindow(v: View?) {
                v?.removeOnAttachStateChangeListener(this)
                v?.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View?) = Unit
        })
    }
}


class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int) {

    companion object {
        fun recordForView(view: View) =
            view.run { InitialPadding(paddingLeft, paddingTop, paddingRight, paddingBottom) }
    }
}


class InitialMargin(val left: Int, val top: Int, val right: Int, val bottom: Int) {

    companion object {
        fun recordForView(view: View) =
            (view.layoutParams as? ViewGroup.MarginLayoutParams
                ?: throw IllegalArgumentException("invalid view layout params"))
                .run { InitialMargin(leftMargin, topMargin, rightMargin, bottomMargin) }
    }
}