package com.example.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.ytaudio.databinding.FragmentYoutubeBinding
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.adapters.ReboundingSwipeActionCallback
import com.example.ytaudio.ui.adapters.YTItemAdapterListener
import com.example.ytaudio.ui.adapters.YTLoadStateAdapter
import com.example.ytaudio.ui.adapters.YouTubeItemsAdapter
import com.example.ytaudio.ui.viewmodels.YouTubeViewModel
import com.example.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class YouTubeFragment : Fragment(), YTItemAdapterListener, Injectable {

    @Inject
    lateinit var youTubeViewModelFactory: ViewModelProvider.Factory

    private val youTubeViewModel: YouTubeViewModel by viewModels {
        youTubeViewModelFactory
    }

    private lateinit var binding: FragmentYoutubeBinding

    private var job: Job? = null

    private val youTubeItemsAdapter = YouTubeItemsAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYoutubeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.apply {
            viewModel = youTubeViewModel
            youtubeItemsView.apply {
                ItemTouchHelper(ReboundingSwipeActionCallback()).attachToRecyclerView(this)
                adapter = youTubeItemsAdapter
            }
            youtubeItemsView.adapter = youTubeItemsAdapter.withLoadStateFooter(YTLoadStateAdapter())
            youtubeItemsView
                .addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            swipeRefresh.setOnRefreshListener {
                youTubeItemsAdapter.refresh()
            }
            toolbar.setNavigationOnClickListener {
                findNavController().navigate(YouTubeFragmentDirections.actionYouTubeFragmentToSearchFragment())
            }
            lifecycleOwner = this@YouTubeFragment
        }

        setLoadState()
        setRecommended()
    }

    override fun onItemClicked(cardView: View, items: YouTubeItem) {
        Toast.makeText(context, items.videoId, Toast.LENGTH_SHORT).show()
    }

    override fun onItemIconChanged(item: YouTubeItem, newValue: Boolean) {
        Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
    }

    private fun setLoadState() {
        lifecycleScope.launch {
            youTubeItemsAdapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing = it.refresh is LoadState.Loading
            }
        }
    }

    private fun setRecommended() {
        job?.cancel()
        job = lifecycleScope.launch {
            youTubeViewModel.getRecommended().collectLatest {
                youTubeItemsAdapter.submitData(it)
            }
        }
    }
}