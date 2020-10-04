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
import com.nvvi9.ytaudio.ui.adapters.YTItemAdapter
import com.nvvi9.ytaudio.ui.adapters.YTLoadStateAdapter
import com.nvvi9.ytaudio.ui.viewmodels.MainViewModel
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
    lateinit var youTubeViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mainViewModelFactory: ViewModelProvider.Factory

    override val youTubeViewModel: YouTubeViewModel by viewModels {
        youTubeViewModelFactory
    }

    private val mainViewModel: MainViewModel by viewModels {
        mainViewModelFactory
    }

    private lateinit var binding: FragmentYoutubeBinding

    private val youTubeItemsAdapter = YTItemAdapter(this)

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

    override fun onItemClicked(cardView: View, item: YouTubeItem) {
        (activity as? MainActivity)?.startYouTubeIntent(item.id)
    }

    override fun onItemLongClicked(item: YouTubeItem): Boolean {
        MenuBottomSheetDialogFragment(R.menu.youtube_bottom_sheet_menu) {
            when (it.itemId) {
                R.id.menu_share -> {
                    (activity as? MainActivity)?.startShareIntent(item.id)
                    true
                }
                R.id.menu_open_youtube -> {
                    (activity as? MainActivity)?.startYouTubeIntent(item.id)
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
            mainViewModel.addToPlaylist(item.id)
        } else {
            mainViewModel.deleteFromPlaylist(item.id)
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