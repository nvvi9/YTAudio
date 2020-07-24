package com.example.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ytaudio.databinding.FragmentSearchBinding
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.adapters.AutocompleteAdapterClickListener
import com.example.ytaudio.ui.adapters.SearchAutocompleteAdapter
import com.example.ytaudio.ui.viewmodels.SearchViewModel
import com.example.ytaudio.utils.extensions.hideKeyboard
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
        { binding.searchToolbar.searchText.setText(it) }
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
            searchToolbar.searchText.addTextChangedListener {
                searchViewModel.setAutocomplete(it.toString())
            }
            searchToolbar.toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            searchToolbar.searchText.setOnEditorActionListener { v, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        v.text.takeIf { it.isNotEmpty() }?.let {
                            findNavController().navigate(
                                SearchFragmentDirections.actionSearchFragmentToSearchResultsFragment(
                                    it.toString()
                                )
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.root.hideKeyboard(context)
    }
}