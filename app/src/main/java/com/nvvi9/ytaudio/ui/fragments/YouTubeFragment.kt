package com.nvvi9.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ItemTouchHelper
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.databinding.FragmentYoutubeBinding
import com.nvvi9.ytaudio.di.Injectable
import com.nvvi9.ytaudio.ui.MainActivity
import com.nvvi9.ytaudio.ui.adapters.ReboundingSwipeActionCallback
import com.nvvi9.ytaudio.ui.adapters.YTLoadStateAdapter
import com.nvvi9.ytaudio.ui.adapters.YTPlaylistItemAdapter
import com.nvvi9.ytaudio.ui.viewmodels.YouTubeViewModel
import com.nvvi9.ytaudio.utils.SpringAddItemAnimator
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@FlowPreview
class YouTubeFragment : YouTubeBaseFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val youTubeViewModel: YouTubeViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var binding: FragmentYoutubeBinding

    private val youTubeItemsAdapter = YTPlaylistItemAdapter(this)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYoutubeBinding.inflate(inflater).apply {
            viewModel = youTubeViewModel
            itemYoutubeView.run {
                ItemTouchHelper(ReboundingSwipeActionCallback()).attachToRecyclerView(this)
                adapter = youTubeItemsAdapter.withLoadStateFooter(YTLoadStateAdapter())
                itemAnimator = SpringAddItemAnimator()
            }
            swipeRefresh.setOnRefreshListener {
                youTubeItemsAdapter.refresh()
            }
            searchButton.setOnClickListener {
                findNavController().navigate(YouTubeFragmentDirections.actionYouTubeFragmentToSearchFragment())
            }
            lifecycleOwner = this@YouTubeFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        youTubeViewModel.updateYTItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        youTubeViewModel.removeSources()
    }

    override fun onItemClicked(videoItem: YouTubeItem) {
        when (videoItem) {
            is YouTubeItem.YouTubeVideoItem -> (activity as? MainActivity)?.startYouTubeIntent(videoItem.id)
        }
    }

    override fun onItemLongClicked(videoItem: YouTubeItem): Boolean {
        when (videoItem) {
            is YouTubeItem.YouTubeVideoItem -> MenuBottomSheetDialogFragment(R.menu.youtube_bottom_sheet_menu) {
                when (it.itemId) {
                    R.id.menu_share -> {
                        (activity as? MainActivity)?.startShareIntent(videoItem.id)
                        true
                    }
                    R.id.menu_open_youtube -> {
                        (activity as? MainActivity)?.startYouTubeIntent(videoItem.id)
                        true
                    }
                    R.id.menu_add -> {
                        youTubeViewModel.addToPlaylist(videoItem)
                        true
                    }
                    else -> false
                }
            }.show(parentFragmentManager, null)
        }
        return true
    }

    override fun onItemIconChanged(videoItem: YouTubeItem, newValue: Boolean) {
        when (videoItem) {
            is YouTubeItem.YouTubeVideoItem -> {
                videoItem.isAdded = newValue
                if (newValue) {
                    youTubeViewModel.addToPlaylist(videoItem)
                } else {
                    youTubeViewModel.deleteFromPlaylist(videoItem)
                }
            }
        }
    }

    override fun setItems(items: PagingData<YouTubeItem>) {
        lifecycleScope.launch {
            youTubeItemsAdapter.run {
                submitData(items)
                notifyDataSetChanged()
            }
        }
        lifecycleScope.launch {
            youTubeItemsAdapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing = it.refresh is LoadState.Loading
            }
        }
    }
}