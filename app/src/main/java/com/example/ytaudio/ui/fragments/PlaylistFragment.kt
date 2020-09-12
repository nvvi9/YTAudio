package com.example.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.R
import com.example.ytaudio.databinding.FragmentPlaylistBinding
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.adapters.PlaylistItemAdapter
import com.example.ytaudio.ui.adapters.PlaylistItemListener
import com.example.ytaudio.ui.viewmodels.MainActivityViewModel
import com.example.ytaudio.ui.viewmodels.PlaylistViewModel
import com.example.ytaudio.utils.SpringAddItemAnimator
import com.example.ytaudio.vo.PlaylistItem
import javax.inject.Inject


class PlaylistFragment : YouTubeIntentFragment(), PlaylistItemListener, Injectable {

    @Inject
    lateinit var playlistViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mainActivityViewModelFactory: ViewModelProvider.Factory

    private val playlistViewModel: PlaylistViewModel by viewModels {
        playlistViewModelFactory
    }

    private val mainActivityViewModel: MainActivityViewModel by viewModels {
        mainActivityViewModelFactory
    }

    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater).apply {
            viewModel = playlistViewModel

            itemPlaylistView.run {
                adapter = PlaylistItemAdapter(this@PlaylistFragment)
                itemAnimator = SpringAddItemAnimator()
                layoutManager = LinearLayoutManager(
                    requireNotNull(this@PlaylistFragment.activity).application,
                    RecyclerView.VERTICAL,
                    false
                )
            }

            lifecycleOwner = this@PlaylistFragment

        }

        playlistViewModel.networkFailure.observe(viewLifecycleOwner) {
            binding.networkFailure.visibility = if (it) View.VISIBLE else View.GONE
        }

        return binding.root
    }

    override fun onItemClicked(item: PlaylistItem) {
        mainActivityViewModel.audioItemClicked(item.id)
    }

    override fun onItemLongClicked(item: PlaylistItem): Boolean {
        MenuBottomSheetDialogFragment(R.menu.playlist_bottom_sheet_menu) {
            when (it.itemId) {
                R.id.menu_share -> {
                    startShareIntent(item.id)
                    true
                }
                R.id.menu_delete -> {
                    playlistViewModel.deleteFromDatabase(item)
                    true
                }
                R.id.menu_open_youtube -> {
                    startYouTubeIntent(item.id)
                    true
                }
                else -> false
            }
        }.show(parentFragmentManager, null)
        return true
    }
}