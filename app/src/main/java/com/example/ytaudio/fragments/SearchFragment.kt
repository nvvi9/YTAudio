package com.example.ytaudio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.databinding.SearchFragmentBinding
import com.example.ytaudio.utils.VideoItemAdapter
import com.example.ytaudio.viewmodels.SearchViewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    private val adapter = VideoItemAdapter {
        Toast.makeText(this.context, it.id.videoId, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = SearchFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        binding.videoItemsView.adapter = adapter

        return binding.root
    }
}