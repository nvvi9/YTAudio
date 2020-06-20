package com.example.ytaudio.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ytaudio.AudioItem
import com.example.ytaudio.R
import com.example.ytaudio.databinding.PlaylistFragmentBinding
import com.example.ytaudio.utils.AudioInfoListener
import com.example.ytaudio.utils.FactoryUtils
import com.example.ytaudio.utils.PlaylistAdapter
import com.example.ytaudio.viewmodels.MainActivityViewModel
import com.example.ytaudio.viewmodels.PlaylistViewModel
import com.google.android.material.textfield.TextInputEditText


class PlaylistFragment : Fragment() {

    private lateinit var audioId: String
    private lateinit var binding: PlaylistFragmentBinding
    private lateinit var playlistViewModel: PlaylistViewModel
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var actionMode: ActionMode? = null

    private val actionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = mode?.run {
            menuInflater.inflate(R.menu.playlist_toolbar_action_mode, menu)
            binding.toolbar.inflateMenu(R.menu.playlist_toolbar_action_mode)
            true
        } ?: false

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.action_select_all -> {
                    playlistAdapter.selectAll()
                    true
                }
                R.id.action_delete -> {
                    mainActivityViewModel.deleteAudioInfo(
                        playlistAdapter.selectedAudioItems
                            .map { it.audioId.toLong() }
                    )
                    mode?.finish()
                    actionMode = null
                    true
                }
                else -> false
            }
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false

        override fun onDestroyActionMode(mode: ActionMode?) {
            playlistAdapter.clearSelected()
            playlistAdapter.actionMode = false
            actionMode = null
        }
    }


    private val audioInfoListener = object : AudioInfoListener {
        
        override fun onClick(item: AudioItem) {
            mainActivityViewModel.audioItemClicked(item)
        }

        override fun onLongClick(item: AudioItem) =
            startActionMode()
    }

    private val playlistAdapter = PlaylistAdapter(audioInfoListener)

    private fun startActionMode() {
        if (actionMode == null) {
            actionMode = activity?.startActionMode(actionModeCallback)
            playlistAdapter.actionMode = true
        }
    }


    companion object {
        fun getInstance(audioId: String) = PlaylistFragment().apply {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.playlist_fragment, container, false)

        binding.apply {

            playlistView.adapter = playlistAdapter
            playlistView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            playlistView.addItemDecoration(
                DividerItemDecoration(
                    activity,
                    DividerItemDecoration.VERTICAL
                )
            )

            linkText.setEndIconOnClickListener {
                if (!binding.linkText.editText?.text.isNullOrBlank()) {
                    this@PlaylistFragment.mainActivityViewModel.onExtract(binding.linkText.editText!!.text.toString())
                    it.hideKeyboard()
                    binding.linkText.editText!!.text.clear()
                }
            }

            linkText.editText!!.setOnKeyListener { v, keyCode, event ->
                if (!(v as TextInputEditText).text.isNullOrBlank() &&
                    keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_UP
                ) {
                    this@PlaylistFragment.mainActivityViewModel.onExtract(v.text.toString())
                    v.hideKeyboard()
                    v.text?.clear()
                    return@setOnKeyListener true
                }
                false
            }

            lifecycleOwner = this@PlaylistFragment
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val context = activity ?: return
        val application = requireNotNull(this.activity).application
        audioId = arguments?.getString(AUDIO_ID_ARG) ?: return

        playlistViewModel = ViewModelProvider(
            this,
            FactoryUtils.providePlaylistViewModel(audioId, application)
        ).get(PlaylistViewModel::class.java)

        mainActivityViewModel =
            ViewModelProvider(this, FactoryUtils.provideMainActivityViewModel(context))
                .get(MainActivityViewModel::class.java)

        playlistViewModel.audioItemList.observe(viewLifecycleOwner, Observer { list ->
            binding.progressBarSpinner.visibility =
                if (list.isNotEmpty()) View.INVISIBLE else View.VISIBLE
            playlistAdapter.submitList(list.sortedBy { it.title })
        })

        playlistViewModel.networkFailure.observe(viewLifecycleOwner, Observer {
            binding.networkFailure.visibility = if (it) View.VISIBLE else View.GONE
        })

        binding.viewModel = playlistViewModel
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