package com.example.ytaudio.search

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.transition.TransitionManager
import com.example.ytaudio.R
import com.example.ytaudio.databinding.SearchFragmentBinding
import com.example.ytaudio.fragment.ActionModeFragment
import com.example.ytaudio.utils.FactoryUtils
import com.example.ytaudio.utils.extensions.hideKeyboard


class SearchFragment : ActionModeFragment() {

    private lateinit var binding: SearchFragmentBinding

    lateinit var viewModel: SearchViewModel
        private set

    private val videoItemAdapter = SearchAdapter(this) {
        Toast.makeText(this@SearchFragment.context, it.videoId, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate() {
        val application = requireNotNull(activity).application
        viewModel =
            ViewModelProvider(this, FactoryUtils.provideSearchViewModel(application))
                .get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchFragmentBinding.inflate(inflater)

        binding.apply {
            viewModel = this@SearchFragment.viewModel
            videoItemsView.adapter = videoItemAdapter
            videoItemsView.addItemDecoration(
                DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            )
            lifecycleOwner = this@SearchFragment
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_toolbar_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        val searchAutoComplete = SearchAutoComplete(context, searchView)

        searchView.queryHint = getString(R.string.search)

        viewModel.autoComplete.observe(viewLifecycleOwner, Observer {
            searchAutoComplete.updateRows(it)
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.hideKeyboard(context)
                query?.let {
                    viewModel.setResponse(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.setAutoComplete(it)
                }
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                searchView.isIconified = false
                searchView.requestFocusFromTouch()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?) = true
        })


        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                if (!viewModel.searchItemList.value.isNullOrEmpty()) {
                    videoItemAdapter.startActionMode()
                }
                true
            }
            R.id.action_search -> {
                activity?.let {
                    TransitionManager.beginDelayedTransition(it.findViewById(R.id.toolbar))
                    MenuItemCompat.expandActionView(item)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        binding.root.hideKeyboard(context)
        super.onPause()
    }
}