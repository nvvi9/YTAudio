package com.nvvi9.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
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
import com.nvvi9.ytaudio.ui.adapters.ReboundingSwipeActionCallback
import com.nvvi9.ytaudio.ui.adapters.YTItemAdapter
import com.nvvi9.ytaudio.ui.adapters.YTItemListener
import com.nvvi9.ytaudio.ui.adapters.YTLoadStateAdapter
import com.nvvi9.ytaudio.ui.viewmodels.SearchResultsViewModel
import com.nvvi9.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@FlowPreview
class SearchResultsFragment :
    YouTubeIntentFragment(),
    YTItemListener,
    Injectable {

    @Inject
    lateinit var youTubeViewModelFactory: ViewModelProvider.Factory

    private val searchResultsViewModel: SearchResultsViewModel by viewModels {
        youTubeViewModelFactory
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        searchResultsViewModel.run {
            errorEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }

            youTubeItems.observe(viewLifecycleOwner) {
                setItems(it)
            }

            updateYTItems(navArgs.query)
        }
    }

    override fun onResume() {
        super.onResume()
        searchResultsViewModel.updateYTItems(navArgs.query)
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
                    searchResultsViewModel.addToPlaylist(item.id)
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
            searchResultsViewModel.addToPlaylist(item.id)
        } else {
            searchResultsViewModel.deleteFromPlaylist(item.id)
        }
    }

    private fun setItems(items: PagingData<YouTubeItem>) {
        lifecycleScope.launch {
            youTubeItemsAdapter.submitData(items)
        }
        lifecycleScope.launch {
            youTubeItemsAdapter.loadStateFlow.collectLatest {
                binding.loadState = it.refresh
            }
        }
    }
}