package com.example.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.ytaudio.databinding.FragmentSearchResultsBinding
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.adapters.ReboundingSwipeActionCallback
import com.example.ytaudio.ui.adapters.YTItemAdapterListener
import com.example.ytaudio.ui.adapters.YTLoadStateAdapter
import com.example.ytaudio.ui.adapters.YouTubeItemsAdapter
import com.example.ytaudio.ui.viewmodels.SearchResultsViewModel
import com.example.ytaudio.vo.YouTubeItem
import kotlinx.android.synthetic.main.fragment_search_results.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalPagingApi
class SearchResultsFragment : Fragment(), YTItemAdapterListener, Injectable {

    @Inject
    lateinit var searchResultsViewModelFactory: ViewModelProvider.Factory

    private val searchResultsViewModel: SearchResultsViewModel by viewModels {
        searchResultsViewModelFactory
    }

    private lateinit var binding: FragmentSearchResultsBinding
    private var job: Job? = null
    private val navArgs: SearchResultsFragmentArgs by navArgs()
    private val youtubeItemsAdapter = YouTubeItemsAdapter(this)

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
    ): View? = FragmentSearchResultsBinding.inflate(inflater).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.recyclerView.apply {
            ItemTouchHelper(ReboundingSwipeActionCallback()).attachToRecyclerView(this)
            adapter = youtubeItemsAdapter.withLoadStateFooter(YTLoadStateAdapter())
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }

        binding.searchResultsToolbar.setNavigationOnClickListener {
            findNavController().navigate(SearchResultsFragmentDirections.actionSearchResultsFragmentToYouTubeFragment())
        }

        search_query.text = navArgs.query
        search_query.setOnClickListener {
            findNavController().navigate(
                SearchResultsFragmentDirections.actionSearchResultsFragmentToSearchFragment(
                    (it as TextView).text.toString()
                )
            )
        }

        binding.lifecycleOwner = this
        setFromQuery()
    }

    override fun onItemClicked(cardView: View, items: YouTubeItem) {
        Toast.makeText(context, items.videoId, Toast.LENGTH_SHORT).show()
    }

    override fun onItemIconChanged(item: YouTubeItem, newValue: Boolean) {
        item.isAdded = newValue
        if (newValue) {
            searchResultsViewModel.addToPlaylist(item.videoId)
        } else {
            searchResultsViewModel.deleteFromPlaylist(item.videoId)
        }
    }

    private fun setFromQuery() {
        job?.cancel()
        job = lifecycleScope.launch {
            searchResultsViewModel.getFromQuery(navArgs.query).collectLatest {
                youtubeItemsAdapter.submitData(it)
            }
        }
    }
}