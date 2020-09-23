package com.nvvi9.ytaudio.ui.adapters

import android.net.Uri
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.nvvi9.ytaudio.vo.PlaylistItem


@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(uri: String?) {
    uri?.let {
        Glide.with(context)
            .load(it)
            .into(this)
    }
}

@BindingAdapter("srcIcon")
fun ImageButton.setIcon(resId: Int?) {
    resId?.let {
        setImageResource(it)
    }
}

@BindingAdapter("timeFormattedSeconds")
fun MaterialTextView.setTime(time: Long?) {
    time?.let {
        text = DateUtils.formatElapsedTime(it).removePrefix("0")
    }
}

@BindingAdapter("timeFormattedMillis")
fun MaterialTextView.setTimeMillis(time: Int?) {
    time?.let {
        text = DateUtils.formatElapsedTime(it / 1000L).removePrefix("0")
    }
}

@BindingAdapter("timeFormatted")
fun MaterialTextView.setPosition(position: Long?) {
    position?.let {
        text = DateUtils.formatElapsedTime(it / 1000)
    }
}

@BindingAdapter("thumbnailUri")
fun AppCompatImageView.setImage(uri: Uri?) {
    uri?.let {
        Glide.with(context)
            .load(it)
            .into(this)
    }
}

@BindingAdapter("audioPlaylist")
fun RecyclerView.setPlaylist(playlist: List<PlaylistItem>?) {
    (adapter as? PlaylistItemAdapter)?.submitList(playlist?.sortedBy { it.title })
}

@BindingAdapter("stringList")
fun RecyclerView.setStringList(items: List<String>?) {
    (adapter as? SearchAutocompleteAdapter)?.submitList(items)
}

@BindingAdapter("layoutFullscreen")
fun View.bindLayoutFullscreen(previousFullscreen: Boolean, fullscreen: Boolean) {
    if (previousFullscreen != fullscreen && fullscreen) {
        systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
}

@BindingAdapter("refreshState")
fun SwipeRefreshLayout.setRefreshState(ytLoadState: YTLoadState?) {
    isRefreshing = ytLoadState is YTLoadState.Loading
}

@BindingAdapter("isVisible")
fun ProgressBar.setVisibility(ytLoadState: YTLoadState?) {
    visibility = if (ytLoadState is YTLoadState.Empty) View.VISIBLE else View.GONE
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