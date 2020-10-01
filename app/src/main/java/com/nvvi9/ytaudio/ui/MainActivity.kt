package com.nvvi9.ytaudio.ui

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.databinding.ActivityMainBinding
import com.nvvi9.ytaudio.ui.viewmodels.MainViewModel
import com.nvvi9.ytaudio.ui.viewmodels.PlayerViewModel
import com.nvvi9.ytaudio.utils.extensions.fixPercentBounds
import com.nvvi9.ytaudio.utils.extensions.fixToPercent
import com.nvvi9.ytaudio.utils.extensions.fixToStep
import com.nvvi9.ytaudio.utils.extensions.percentToMillis
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

    private lateinit var navController: NavController

    private val mainViewModel: MainViewModel by viewModels {
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

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        binding.run {
            bottomNav.setupWithNavController(navController)
            lifecycleScope.launchWhenResumed {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.playlistFragment, R.id.youTubeFragment, R.id.searchResultsFragment -> {
                            showBottomNav()
                            playerViewModel.nowPlayingInfo.value?.let {
                                showMiniPlayer()
                            }
                        }
                        else -> {
                            hideBottomNav()
                            hideMiniPlayer()
                        }
                    }
                }
            }
        }

        mainViewModel.networkFailure.observe(this) {
            it?.getContentIfNotHandled()?.let { isNetworkFailure ->
                if (isNetworkFailure) {
                    Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show()
                }
            }
        }

        playerViewModel.run {
            nowPlayingInfo.observe(this@MainActivity) {
                binding.nowPlaying = it
                it?.let {
                    when (navController.currentDestination?.id) {
                        R.id.playlistFragment, R.id.youTubeFragment, R.id.searchResultsFragment -> showMiniPlayer()
                        else -> hideMiniPlayer()
                    }
                } ?: hideMiniPlayer()
            }
            currentButtonRes.observe(this@MainActivity) {
                binding.buttonRes = it
            }
            currentPositionMillis.observe(this@MainActivity) { pos ->
                binding.progressCircular.run {
                    (nowPlayingInfo.value?.durationMillis ?: 0).let { total ->
                        progress.percentToMillis(total).fixToStep(1000).takeIf {
                            it != pos
                        }?.let {
                            progress = pos.fixToPercent(total).fixPercentBounds()
                        }
                    }
                }
            }
        }

        intent?.takeIf {
            it.action == Intent.ACTION_SEND
        }?.getStringExtra(Intent.EXTRA_TEXT)?.let {
            mainViewModel.addToPlaylist(it.takeLast(11))
        }
    }

    override fun androidInjector() = androidInjector

    fun showPlayer(v: View) {
        navController.navigate(R.id.action_global_audioPlayerFragment)
    }

    fun playPause(v: View) {
        playerViewModel.playPause()
    }

    fun showMiniPlayer() {
        binding.bottomControls.visibility = View.VISIBLE
//        binding.run {
//            bottomControls.isEnabled = true
//            please(190, AccelerateInterpolator()) {
//                animate(bottomControls) {
//                    aboveOf(bottomNav)
//                }
//            }.start()
//        }
    }

    fun hideMiniPlayer() {
        binding.bottomControls.visibility = View.GONE
//        binding.run {
//            bottomControls.isEnabled = false
//            please(190, AccelerateInterpolator()) {
//                animate(bottomControls) {
//                    belowOf(bottomNav)
//                }
//            }.start()
//        }
    }

    fun showBottomNav() {
//        binding.bottomNav.show()
        binding.bottomNav.visibility = View.VISIBLE
//        please(190, AccelerateInterpolator()) {
//            animate(binding.bottomNav) {
//                bottomOfItsParent()
//            }
//        }.start()
    }

    fun hideBottomNav() {
//        binding.bottomNav.hide()
        binding.bottomNav.visibility = View.GONE
//        please(190, AccelerateInterpolator()) {
//            animate(binding.bottomNav) {
//                belowOf(binding.mainContainer)
//            }
//        }.start()
    }
}