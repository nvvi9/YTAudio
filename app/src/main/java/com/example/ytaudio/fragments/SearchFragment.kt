package com.example.ytaudio.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.ytaudio.R
import com.example.ytaudio.databinding.SearchFragmentBinding
import com.example.ytaudio.network.VideoItem
import com.example.ytaudio.utils.ClickListener
import com.example.ytaudio.utils.VideoItemAdapter
import com.example.ytaudio.viewmodels.SearchViewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    private lateinit var searchView: SearchView

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
            viewModel = this@SearchFragment.viewModel
            videoItemsView.adapter = adapter
            videoItemsView.addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )
            lifecycleOwner = this@SearchFragment
        }

        viewModel.autoComplete.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_toolbar_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchItem?.let { item ->
            searchView = MenuItemCompat.getActionView(item) as SearchView
            searchView.setOnCloseListener(object : SearchView.OnCloseListener {

                override fun onClose() = true
            })

            val searchPlate: EditText =
                searchView.findViewById(androidx.appcompat.R.id.search_src_text)

            searchPlate.hint = "Search"

            val searchPlateView: View =
                searchView.findViewById(androidx.appcompat.R.id.search_plate)

            searchPlateView.setBackgroundColor(Color.TRANSPARENT)

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?) =
                    query?.let {
                        viewModel.getResponse(it)
                        true
                    } ?: false

                override fun onQueryTextChange(newText: String?): Boolean = false
            })
        }
    }
}