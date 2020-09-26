package com.nvvi9.ytaudio.ui

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.github.florent37.kotlin.pleaseanimate.please
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.databinding.ActivityMainBinding
import com.nvvi9.ytaudio.ui.viewmodels.MainActivityViewModel
import com.nvvi9.ytaudio.ui.viewmodels.PlayerViewModel
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var mainActivityViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var playerViewModelFactory: ViewModelProvider.Factory

    lateinit var binding: ActivityMainBinding

    private val mainActivityViewModel: MainActivityViewModel by viewModels {
        mainActivityViewModelFactory
    }

    private val playerViewModel: PlayerViewModel by viewModels {
        playerViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        binding.run {
            bottomNav.setupWithNavController(navController)
            lifecycleScope.launchWhenResumed {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.playlistFragment, R.id.youTubeFragment, R.id.searchResultsFragment -> showBottomNav()
                        else -> hideBottomNav()
                    }
                }
            }
        }

        playerViewModel.run {
            nowPlayingInfo.observe(this@MainActivity) {
                it?.let {
                    showMiniPlayer()
                    binding.nowPlaying = it
                }
            }
            currentButtonRes.observe(this@MainActivity) {
                binding.buttonRes = it
            }
        }

        intent?.takeIf {
            it.action == Intent.ACTION_SEND
        }?.getStringExtra(Intent.EXTRA_TEXT)?.let {
            mainActivityViewModel.addToPlaylist(it.takeLast(11))
        }
    }

    override fun androidInjector() = androidInjector

    fun showMiniPlayer() {
        binding.run {
            bottomControls.isEnabled = true
            please(190, AccelerateInterpolator()) {
                animate(bottomControls) {
                    aboveOf(bottomNav)
                }
            }.start()
        }
    }

    fun hideMiniPlayer() {
        binding.run {
            bottomControls.isEnabled = false
            please(190, AccelerateInterpolator()) {
                animate(bottomControls) {
                    belowOf(bottomNav)
                }
            }.start()
        }
    }

    fun showBottomNav() {
        please(190, AccelerateInterpolator()) {
            animate(binding.bottomNav) {
                bottomOfItsParent()
            }
        }.start()
    }

    fun hideBottomNav() {
        please(190, AccelerateInterpolator()) {
            animate(binding.bottomNav) {
                bottomOfItsParent()
            }
        }.start()
    }
}