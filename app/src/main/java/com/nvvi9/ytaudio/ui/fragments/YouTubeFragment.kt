package com.nvvi9.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ItemTouchHelper
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.databinding.FragmentYoutubeBinding
import com.nvvi9.ytaudio.di.Injectable
import com.nvvi9.ytaudio.ui.adapters.ReboundingSwipeActionCallback
import com.nvvi9.ytaudio.ui.adapters.YTItemAdapter
import com.nvvi9.ytaudio.ui.adapters.YTItemListener
import com.nvvi9.ytaudio.ui.adapters.YTLoadStateAdapter
import com.nvvi9.ytaudio.ui.viewmodels.YouTubeViewModel
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@FlowPreview
class YouTubeFragment : YouTubeIntentFragment(), YTItemListener, Injectable {

    @Inject
    lateinit var youTubeViewModelFactory: ViewModelProvider.Factory

    private val youTubeViewModel: YouTubeViewModel by viewModels {
        youTubeViewModelFactory
    }

    private lateinit var binding: FragmentYoutubeBinding
    private val youTubeItemsAdapter = YTItemAdapter(this)
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentYoutubeBinding.inflate(inflater).apply {
        viewModel = youTubeViewModel
        itemYoutubeView.run {
            ItemTouchHelper(ReboundingSwipeActionCallback()).attachToRecyclerView(this)
            adapter = youTubeItemsAdapter.withLoadStateFooter(YTLoadStateAdapter())
            itemAnimator = null
        }
        swipeRefresh.setOnRefreshListener {
            youTubeItemsAdapter.refresh()
        }
        searchButton.setOnClickListener {
            findNavController().navigate(YouTubeFragmentDirections.actionYouTubeFragmentToSearchFragment())
        }
        lifecycleOwner = this@YouTubeFragment
    }.also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        youTubeViewModel.run {
            updateRecommended()
            errorEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }

            recommendedItems.observe(viewLifecycleOwner) {
                setRecommended(it)
            }
        }
    }

    override fun onItemClicked(cardView: View, item: YouTubeItem) {
        startYouTubeIntent(item.id)
    }

    override fun onItemLongClicked(item: YouTubeItem): Boolean {
        MenuBottomSheetDialogFragment(R.menu.youtube_bottom_sheet_menu) {
            when (it.itemId) {
                R.id.menu_share -> {
                    startShareIntent(item.id)
                    true
                }
                R.id.menu_open_youtube -> {
                    startYouTubeIntent(item.id)
                    true
                }
                R.id.menu_add -> {
                    youTubeViewModel.addToPlaylist(item.id)
                    true
                }
                else -> false
            }
        }.show(parentFragmentManager, null)
        return true
    }

    override fun onItemIconChanged(item: YouTubeItem, newValue: Boolean) {
        item.isAdded = newValue
        if (newValue) {
            youTubeViewModel.addToPlaylist(item.id)
        } else {
            youTubeViewModel.deleteFromPlaylist(item.id)
        }
    }

    private fun setRecommended(items: PagingData<YouTubeItem>) {
        job?.cancel()
        job = lifecycleScope.launch {
            youTubeItemsAdapter.submitData(items)
            binding.itemYoutubeView.layoutManager?.scrollToPosition(0)
        }
    }
}