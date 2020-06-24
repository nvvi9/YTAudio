package com.example.ytaudio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.ytaudio.databinding.SearchFragmentBinding
import com.example.ytaudio.network.VideoItem
import com.example.ytaudio.utils.ClickListener
import com.example.ytaudio.utils.VideoItemAdapter
import com.example.ytaudio.viewmodels.SearchViewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    private val adapter = VideoItemAdapter(object : ClickListener<VideoItem> {
        override fun onClick(item: VideoItem) {
            Toast.makeText(this@SearchFragment.context, item.id.videoId, Toast.LENGTH_SHORT).show()
        }

        override fun onLongClick(item: VideoItem) {
            Toast.makeText(
                this@SearchFragment.context,
                item.snippet.liveBroadcastContent,
                Toast.LENGTH_SHORT
            ).show()
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = SearchFragmentBinding.inflate(inflater)
        binding.apply {
            viewModel = viewModel
            videoItemsView.adapter = adapter
            videoItemsView.addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )
            lifecycleOwner = this@SearchFragment
        }

        return binding.root
    }
}