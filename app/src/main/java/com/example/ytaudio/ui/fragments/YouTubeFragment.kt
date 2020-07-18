package com.example.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.databinding.FragmentYoutubeBinding
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.adapters.YouTubeItemsAdapter
import com.example.ytaudio.ui.viewmodels.YouTubeViewModel
import com.example.ytaudio.vo.Result
import com.example.ytaudio.vo.Result.Loading
import javax.inject.Inject


class YouTubeFragment : Fragment(), Injectable {

    @Inject
    lateinit var youTubeViewModelFactory: ViewModelProvider.Factory

    private val youTubeViewModel: YouTubeViewModel by viewModels {
        youTubeViewModelFactory
    }

    private lateinit var binding: FragmentYoutubeBinding

    private val youTubeItemsAdapter = YouTubeItemsAdapter {
        Toast.makeText(context, it.videoId, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYoutubeBinding.inflate(inflater)
        val application= requireNotNull(activity).application

        youTubeViewModel.youTubeItemList.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Loading -> binding.progressView.visibility = View.VISIBLE
                is Error -> Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                is Result.Success -> {
                    binding.progressView.visibility = View.GONE
                    youTubeItemsAdapter.submitList(it.data)
                }
            }
        })

        binding.apply {
            viewModel = youTubeViewModel
            youtubeItemsView.adapter = youTubeItemsAdapter
            youtubeItemsView
                .addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

            youtubeItemsView.layoutManager=
                LinearLayoutManager(application, RecyclerView.VERTICAL, false)

            lifecycleOwner = this@YouTubeFragment
        }

        return binding.root
    }
}