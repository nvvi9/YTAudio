package com.nvvi9.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ItemTouchHelper
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.databinding.FragmentSearchResultsBinding
import com.nvvi9.ytaudio.di.Injectable
import com.nvvi9.ytaudio.ui.MainActivity
import com.nvvi9.ytaudio.ui.adapters.ReboundingSwipeActionCallback
import com.nvvi9.ytaudio.ui.adapters.YTItemAdapter
import com.nvvi9.ytaudio.ui.adapters.YTLoadStateAdapter
import com.nvvi9.ytaudio.ui.viewmodels.YouTubeViewModel
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@FlowPreview
class SearchResultsFragment : YouTubeBaseFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val youTubeViewModel: YouTubeViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var binding: FragmentSearchResultsBinding

    private val navArgs: SearchResultsFragmentArgs by navArgs()

    private val youTubeItemsAdapter = YTItemAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(SearchResultsFragmentDirections.actionSearchResultsFragmentToYouTubeFragment())
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchResultsBinding.inflate(inflater).apply {
            itemYoutubeView.run {
                ItemTouchHelper(ReboundingSwipeActionCallback()).attachToRecyclerView(this)
                adapter = youTubeItemsAdapter.withLoadStateFooter(YTLoadStateAdapter())
            }

            searchToolbar.setNavigationOnClickListener {
                findNavController().navigate(SearchResultsFragmentDirections.actionSearchResultsFragmentToYouTubeFragment())
            }

            searchQuery.run {
                setText(navArgs.query)
                setOnClickListener {
                    findNavController().navigate(
                        SearchResultsFragmentDirections.actionSearchResultsFragmentToSearchFragment(
                            (it as EditText).text.toString()
                        )
                    )
                }
            }
            lifecycleOwner = this@SearchResultsFragment
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        youTubeViewModel.updateYTItems(navArgs.query)
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
                    youTubeViewModel.addToPlaylist(item)
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
            youTubeViewModel.addToPlaylist(item)
        } else {
            youTubeViewModel.deleteFromPlaylist(item)
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
                binding.loadState = it.refresh
            }
        }
    }
}