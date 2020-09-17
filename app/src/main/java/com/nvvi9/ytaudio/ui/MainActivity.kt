package com.nvvi9.ytaudio.ui

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.databinding.ActivityMainBinding
import com.nvvi9.ytaudio.ui.fragments.PlayerFragment
import com.nvvi9.ytaudio.ui.viewmodels.MainActivityViewModel
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var mainActivityViewModelFactory: ViewModelProvider.Factory

    lateinit var binding: ActivityMainBinding

    private val mainActivityViewModel: MainActivityViewModel by viewModels {
        mainActivityViewModelFactory
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        binding.apply {
            bottomNav.setupWithNavController(navController)
            lifecycleScope.launchWhenResumed {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.playlistFragment, R.id.youTubeFragment, R.id.searchResultsFragment -> mainMotionLayout.transitionToStart()
                        else -> mainMotionLayout.transitionToEnd()
                    }
                }
            }
        }

        intent?.takeIf {
            it.action == Intent.ACTION_SEND
        }?.getStringExtra(Intent.EXTRA_TEXT)?.let {
            mainActivityViewModel.addToPlaylist(it.takeLast(11))
        }

        mainActivityViewModel.replaceEvent.observe(this) {
            it.getContentIfNotHandled()?.let {
                replacePlayFragment()
            }
        }
    }

    override fun onBackPressed() {
        (supportFragmentManager.findFragmentById(R.id.fragment_container) as? PlayerFragment)?.motion_layout
            ?.takeIf { it.currentState == R.id.end }
            ?.transitionToStart() ?: super.onBackPressed()
    }

    override fun androidInjector() = androidInjector

    private fun replacePlayFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PlayerFragment.newInstance())
            .commit()
    }
}