package com.nvvi9.ytaudio.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.nvvi9.ytaudio.ui.adapters.YTItemListener
import com.nvvi9.ytaudio.ui.viewmodels.YouTubeBaseViewModel
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@ExperimentalCoroutinesApi
@FlowPreview
@ExperimentalPagingApi
abstract class YouTubeBaseFragment : Fragment(), YTItemListener {

    protected abstract val youTubeViewModel: YouTubeBaseViewModel

    protected abstract fun setItems(items: PagingData<YouTubeItem>)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        youTubeViewModel.run {
            errorEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }

            observeOnYouTubeItems(viewLifecycleOwner) {
                setItems(it)
            }
        }
    }
}