package com.example.ytaudio.screens.url_link

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.databinding.SourceLinkFragmentBinding

class SourceLinkFragment : Fragment() {

    companion object {
        const val STATE_LINK_TEXT = "currentLink"
    }


    private lateinit var binding: SourceLinkFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.source_link_fragment, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = AudioDatabase.getInstance(application).audioDatabaseDao
        val viewModelFactory = SourceLinkViewModelFactory(dataSource, application)
        val viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SourceLinkViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.startExtraction.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.onExtract(binding.linkText.text.toString())
                viewModel.onStartExtractionDone()
            }
        })

        viewModel.navigateToPlaylist.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigate(SourceLinkFragmentDirections.actionSourceLinkFragmentToPlaylistFragment())
                viewModel.navigationDone()
            }
        })

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence(STATE_LINK_TEXT, binding.linkText.text)

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.run {
            binding.linkText.text = getCharSequence(STATE_LINK_TEXT, "") as Editable
        }
    }
}