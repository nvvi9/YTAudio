package com.example.ytaudio.screens.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.databinding.PlaylistFragmentBinding

class PlaylistFragment : Fragment() {

    private lateinit var binding: PlaylistFragmentBinding
    private lateinit var viewModel: PlaylistViewModel

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
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(PlaylistViewModel::class.java)

        val audioPlaylist = ArrayList<AudioInfo>()

        val adapter = PlaylistAdapter(audioPlaylist)

        binding.playlistView.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

        binding.playlistView.adapter = adapter

        binding.lifecycleOwner = this

        viewModel.getAudioPlaylist().observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })

        return binding.root
    }
}