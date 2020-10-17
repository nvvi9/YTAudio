package com.nvvi9.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.databinding.FragmentPlaylistBinding
import com.nvvi9.ytaudio.di.Injectable
import com.nvvi9.ytaudio.ui.MainActivity
import com.nvvi9.ytaudio.ui.adapters.PlaylistItemAdapter
import com.nvvi9.ytaudio.ui.adapters.PlaylistItemListener
import com.nvvi9.ytaudio.ui.viewmodels.PlaylistViewModel
import com.nvvi9.ytaudio.utils.SpringAddItemAnimator
import com.nvvi9.ytaudio.vo.PlaylistItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class PlaylistFragment :
    Fragment(),
    PlaylistItemListener,
    Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    private val playlistViewModel: PlaylistViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var binding: FragmentPlaylistBinding

    private val playlistItemAdapter = PlaylistItemAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater).apply {
            itemPlaylistView.run {
                adapter = playlistItemAdapter
                itemAnimator = SpringAddItemAnimator()
                layoutManager = LinearLayoutManager(
                    requireNotNull(this@PlaylistFragment.activity).application,
                    RecyclerView.VERTICAL,
                    false
                )
            }

            lifecycleOwner = this@PlaylistFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistViewModel.observeOnPlaylistItems(viewLifecycleOwner) {
            it?.let {
                playlistItemAdapter.submitList(it)
                playlistItemAdapter.notifyDataSetChanged()
                binding.emptyMessage.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playlistViewModel.removeSources()
    }

    override fun onItemClicked(item: PlaylistItem) {
        playlistViewModel.playFromId(item.id)
//        mainViewModel.audioItemClicked(item.id)
    }

    @ExperimentalPagingApi
    override fun onItemLongClicked(item: PlaylistItem): Boolean {
        MenuBottomSheetDialogFragment(R.menu.playlist_bottom_sheet_menu) {
            when (it.itemId) {
                R.id.menu_share -> {
                    (activity as? MainActivity)?.startShareIntent(item.id)
                    true
                }
                R.id.menu_delete -> {
                    playlistViewModel.deleteFromDatabase(item)
                    true
                }
                R.id.menu_open_youtube -> {
                    (activity as? MainActivity)?.startYouTubeIntent(item.id)
                    true
                }
                else -> false
            }
        }.show(parentFragmentManager, null)
        return true
    }
}