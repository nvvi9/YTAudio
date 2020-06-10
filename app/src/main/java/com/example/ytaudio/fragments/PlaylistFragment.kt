package com.example.ytaudio.fragments

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.R
import com.example.ytaudio.databinding.PlaylistFragmentBinding
import com.example.ytaudio.utils.AudioInfoListener
import com.example.ytaudio.utils.FactoryUtils
import com.example.ytaudio.utils.PlaylistAdapter
import com.example.ytaudio.viewmodels.PlaylistViewModel
import com.google.android.material.textfield.TextInputEditText

class PlaylistFragment : Fragment() {

    private lateinit var audioId: String
    private lateinit var binding: PlaylistFragmentBinding
    private lateinit var viewModel: PlaylistViewModel

    companion object {
        fun getInstance() = PlaylistFragment().apply {
            arguments = Bundle().apply {
                putString(AUDIO_ID_ARG, audioId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.playlist_fragment, container, false)

        val application = requireNotNull(this.activity).application

        viewModel =
            ViewModelProvider(this, FactoryUtils.providePlaylistViewModel(audioId, application)).get(
                PlaylistViewModel::class.java
            )

        val adapter = PlaylistAdapter(AudioInfoListener {
            findNavController().navigate(
                PlaylistFragmentDirections.actionPlaylistFragmentToAudioPlayerFragment(it)
            )
        })

        binding.apply {
            playlistView.layoutManager =
                LinearLayoutManager(this@PlaylistFragment.context, RecyclerView.VERTICAL, false)

            playlistView.adapter = adapter

            viewModel = viewModel

//            toolbar.setOnMenuItemClickListener {
//                when (it.itemId) {
//                    R.id.url_link -> {
//                        if (!linkText.text.isNullOrBlank()) {
//                            this@PlaylistFragment.viewModel.onExtract(linkText.text.toString())
//                            toolbar.hideKeyboard()
//                        }
//                        true
//                    }
//                    else -> false
//                }
//            }

            linkText.setEndIconOnClickListener {
                if (!binding.linkText.editText?.text.isNullOrBlank()) {
                    this@PlaylistFragment.viewModel.onExtract(binding.linkText.editText!!.text.toString())
                    it.hideKeyboard()
                    binding.linkText.editText!!.text.clear()
                }
            }

            linkText.editText!!.setOnKeyListener { v, keyCode, event ->
                if (!(v as TextInputEditText).text.isNullOrBlank() && keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    this@PlaylistFragment.viewModel.onExtract(v.text.toString())
                    v.hideKeyboard()
                    v.text?.clear()
                    return@setOnKeyListener true
                }
                false
            }

            lifecycleOwner = this@PlaylistFragment
        }

        viewModel.audioPlaylist.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it.sortedBy { audio -> audio.audioTitle })
            }
        })

        return binding.root
    }


    override fun onPause() {
        super.onPause()
        binding.root.hideKeyboard()
    }

    private fun View.hideKeyboard() =
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
            windowToken,
            0
        )
}

private const val AUDIO_ID_ARG = "com.example.ytaudio.fragments.PlaylistFragment.AUDIO_ID"