package com.example.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.databinding.FragmentSearchBinding
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.adapters.AutocompleteAdapterClickListener
import com.example.ytaudio.ui.adapters.SearchAutocompleteAdapter
import com.example.ytaudio.ui.viewmodels.SearchViewModel
import javax.inject.Inject


class SearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var searchViewModelFactory: ViewModelProvider.Factory

    private val searchViewModel: SearchViewModel by viewModels {
        searchViewModelFactory
    }

    private lateinit var binding: FragmentSearchBinding

    private val adapter = SearchAutocompleteAdapter(AutocompleteAdapterClickListener(
        { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
        { binding.searchText.setText(it) }
    ))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater).apply {
            viewModel = searchViewModel
            recyclerView.adapter = adapter
            lifecycleOwner = this@SearchFragment
            searchText.addTextChangedListener {
                searchViewModel.setAutocomplete(it.toString())
            }
        }

        return binding.root
    }
}