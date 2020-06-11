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

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        volumeControlStream = AudioManager.STREAM_MUSIC

        viewModel =
            ViewModelProvider(this, FactoryUtils.provideMainActivityViewModel(this)).get(
                MainActivityViewModel::class.java
            )

        viewModel.navigateToFragment.observe(this, Observer {
            it.getContentIfNotHandled()?.let { request ->
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment, request.fragment, request.tag)
                if (request.addToBackStack) transaction.addToBackStack(null)
                transaction.commit()
            }
        })

        viewModel.navigateToPlaylist.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { audioId ->
                navigateToPlaylist(audioId)
            }
        })

        viewModel.rootMediaId.observe(this, Observer {
            it?.let {
                navigateToPlaylist(it)
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