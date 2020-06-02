package com.example.ytaudio.screens.playlist

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.databinding.PlaylistFragmentBinding

class PlaylistFragment : Fragment() {

    private lateinit var binding: PlaylistFragmentBinding
    private lateinit var playlistViewModel: PlaylistViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.playlist_fragment, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = AudioDatabase.getInstance(application).audioDatabaseDao
        val viewModelFactory = PlaylistViewModelFactory(dataSource, application)
        playlistViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(PlaylistViewModel::class.java)

        val adapter = PlaylistAdapter(AudioInfoListener { audioUri, audioTitle, photoUri ->
            findNavController().navigate(
                PlaylistFragmentDirections.actionPlaylistFragmentToAudioPlayerFragment(
                    audioPhotoUri = photoUri,
                    audioUri = audioUri,
                    audioTitle = audioTitle
                )
            )
        })

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sourceLinkFragment -> {
                    Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(PlaylistFragmentDirections.actionPlaylistFragmentToSourceLink())
                    true
                }
                else -> false
            }
        }

        binding.apply {
            playlistView.layoutManager =
                LinearLayoutManager(this@PlaylistFragment.context, RecyclerView.VERTICAL, false)

            playlistView.adapter = adapter

            viewModel = playlistViewModel

            lifecycleOwner = this@PlaylistFragment
        }

        playlistViewModel.audioPlaylist.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}

