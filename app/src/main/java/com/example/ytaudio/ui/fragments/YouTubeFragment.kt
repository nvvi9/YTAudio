package com.example.ytaudio.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.ytaudio.R
import com.example.ytaudio.databinding.FragmentYoutubeBinding
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.adapters.ReboundingSwipeActionCallback
import com.example.ytaudio.ui.adapters.YTItemAdapter
import com.example.ytaudio.ui.adapters.YTItemListener
import com.example.ytaudio.ui.adapters.YTLoadStateAdapter
import com.example.ytaudio.ui.viewmodels.YouTubeViewModel
import com.example.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalPagingApi
class YouTubeFragment : Fragment(), YTItemListener, Injectable {

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
    ): View? {
        binding = FragmentYoutubeBinding.inflate(inflater)

        youTubeViewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.apply {
            viewModel = youTubeViewModel
            itemYoutubeView.run {
                ItemTouchHelper(ReboundingSwipeActionCallback()).attachToRecyclerView(this)
                adapter = youTubeItemsAdapter.withLoadStateFooter(YTLoadStateAdapter())
            }
            swipeRefresh.setOnRefreshListener {
                youTubeItemsAdapter.refresh()
            }
            searchButton.setOnClickListener {
                findNavController().navigate(YouTubeFragmentDirections.actionYouTubeFragmentToSearchFragment())
            }
            lifecycleOwner = this@YouTubeFragment
        }
        setLoadState()
        setRecommended()
    }

    override fun onItemClicked(cardView: View, item: YouTubeItem) {
        startYouTubeIntent(item.videoId)
    }

    override fun onItemLongClicked(item: YouTubeItem): Boolean {
        MenuBottomSheetDialogFragment(R.menu.youtube_bottom_sheet_menu) {
            when (it.itemId) {
                R.id.menu_open_youtube -> {
                    startYouTubeIntent(item.videoId)
                    true
                }
                R.id.menu_add -> {
                    youTubeViewModel.addToPlaylist(item.videoId)
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
            youTubeViewModel.addToPlaylist(item.videoId)
        } else {
            youTubeViewModel.deleteFromPlaylist(item.videoId)
        }
    }

    private fun startYouTubeIntent(id: String) {
        try {
            context?.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
            )
        } catch (e: ActivityNotFoundException) {
            context?.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$id"))
            )
        }
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