package com.example.ytaudio.playlist

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.R
import com.example.ytaudio.activity.MainActivityViewModel
import com.example.ytaudio.databinding.PlaylistFragmentBinding
import com.example.ytaudio.fragment.ActionModeFragment
import com.example.ytaudio.utils.FactoryUtils


class PlaylistFragment : ActionModeFragment() {

    lateinit var playlistViewModel: PlaylistViewModel
        private set

    private lateinit var binding: PlaylistFragmentBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel

    private val playlistAdapter = PlaylistAdapter(this) {
        mainActivityViewModel.audioItemClicked(it.id)
        findNavController().navigate(PlaylistFragmentDirections.actionPlaylistFragmentToAudioPlayerFragment())
    }

    override fun onCreate() {
        val application = requireNotNull(this.activity).application

        playlistViewModel = ViewModelProvider(
            this,
            FactoryUtils.providePlaylistViewModel(application)
        ).get(PlaylistViewModel::class.java)

        mainActivityViewModel =
            ViewModelProvider(this, FactoryUtils.provideMainActivityViewModel(application))
                .get(MainActivityViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PlaylistFragmentBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application

        playlistViewModel.networkFailure.observe(viewLifecycleOwner, Observer {
            binding.networkFailure.visibility = if (it) View.VISIBLE else View.GONE
        })

        binding.viewModel = this.playlistViewModel

        binding.apply {

            playlistView.adapter = playlistAdapter
            playlistView.layoutManager =
                LinearLayoutManager(application, RecyclerView.VERTICAL, false)
            playlistView.addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )

            lifecycleOwner = this@PlaylistFragment
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_playlist_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                if (!this.playlistViewModel.playlistItems.value.isNullOrEmpty()) {
                    playlistAdapter.startActionMode()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

