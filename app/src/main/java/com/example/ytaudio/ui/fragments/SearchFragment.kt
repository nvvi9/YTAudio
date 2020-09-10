package com.example.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.ytaudio.databinding.FragmentSearchBinding
import com.example.ytaudio.di.Injectable
import com.example.ytaudio.ui.adapters.SearchAutocompleteAdapter
import com.example.ytaudio.ui.adapters.SearchAutocompleteItemListener
import com.example.ytaudio.ui.viewmodels.SearchViewModel
import com.example.ytaudio.utils.extensions.hideKeyboard
import com.example.ytaudio.utils.extensions.showKeyboard
import javax.inject.Inject


class SearchFragment : Fragment(), SearchAutocompleteItemListener, Injectable {

    @Inject
    lateinit var searchViewModelFactory: ViewModelProvider.Factory

    private val searchViewModel: SearchViewModel by viewModels {
        searchViewModelFactory
    }

    private lateinit var binding: FragmentSearchBinding
    private val navArgs: SearchFragmentArgs by navArgs()

    override fun onItemClicked(text: String) {
        navigateToResults(text)
    }

    override fun onArrowClicked(text: String) {
        setToolbarText(text)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater).apply {
            viewModel = searchViewModel

            recyclerView.adapter = SearchAutocompleteAdapter(this@SearchFragment)
            (recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false

            searchToolbar.setNavigationOnClickListener {
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToYouTubeFragment())
            }

            searchQuery.run {
                addTextChangedListener {
                    searchViewModel.setAutocomplete(it.toString())
                }

                setOnClickListener {
                    requestFocus()
                    showKeyboard()
                }

                setOnEditorActionListener { v, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_SEARCH -> {
                            v.text.takeIf { it.isNotEmpty() }?.let {
                                navigateToResults(it.toString())
                            }
                            true
                        }
                        else -> false
                    }
                }
            }

            lifecycleOwner = this@SearchFragment
        }

        navArgs.query?.let {
            setToolbarText(it)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.searchQuery.run {
            requestFocus()
            showKeyboard()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.searchQuery.run {
            clearFocus()
            hideKeyboard()
        }
    }

    private fun setToolbarText(text: CharSequence) {
        binding.searchQuery.apply {
            setText(text)
            setSelection(text.length)
        }
    }

    private fun navigateToResults(query: String) {
        findNavController()
            .navigate(SearchFragmentDirections.actionSearchFragmentToSearchResultsFragment(query))
    }
}