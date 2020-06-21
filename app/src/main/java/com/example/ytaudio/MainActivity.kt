package com.example.ytaudio

import android.media.AudioManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ytaudio.fragments.PlaylistFragment
import com.example.ytaudio.utils.FactoryUtils
import com.example.ytaudio.viewmodels.MainActivityViewModel


class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this, FactoryUtils.provideMainActivityViewModel(this)).get(
            MainActivityViewModel::class.java
        )
    }

    companion object {
        fun getInstance() = MainActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        volumeControlStream = AudioManager.STREAM_MUSIC

        viewModel.navigateToFragment.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { request ->
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, request.fragment, request.tag)
                if (request.addToBackStack) transaction.addToBackStack(null)
                transaction.commit()
            }
        })

        viewModel.rootMediaId.observe(this, Observer {
            it?.let {
                navigateToPlaylist(it)
            }
        })

        viewModel.needUpdateAudioInfoList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.updateAudioInfoList(it)
            }
        })
    }

    private fun navigateToPlaylist(audioId: String) {
        var fragment: PlaylistFragment? =
            supportFragmentManager.findFragmentByTag(audioId) as PlaylistFragment?
        if (fragment == null) {
            fragment = PlaylistFragment.getInstance(audioId)
            viewModel.showFragment(fragment, !isRoot(audioId), audioId)
        }
    }

    private fun isRoot(audioId: String) = audioId == viewModel.rootMediaId.value
}