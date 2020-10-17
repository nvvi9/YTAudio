package com.nvvi9.ytaudio.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
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
import androidx.paging.ExperimentalPagingApi
import com.nvvi9.ytaudio.R
import com.nvvi9.ytaudio.databinding.ActivityMainBinding
import com.nvvi9.ytaudio.ui.viewmodels.MainViewModel
import com.nvvi9.ytaudio.ui.viewmodels.PlayerViewModel
import com.nvvi9.ytaudio.utils.extensions.*
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class MainActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val mainViewModel: MainViewModel by viewModels {
        viewModelFactory
    }

    private val playerViewModel: PlayerViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        volumeControlStream = AudioManager.STREAM_MUSIC
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        setupBinding()
        setupMainViewModelObservers()
        setupPlayerViewModelObservers()
        checkIntent()
    }

    override fun androidInjector() = androidInjector

    fun showPlayer(v: View) {
        navController.navigate(R.id.action_global_audioPlayerFragment)
    }

    fun playPause(v: View) {
        playerViewModel.playPause()
    }

    fun startShareIntent(id: String) {
        startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://youtu.be/$id")
            type = "text/plain"
        }, null))
    }

    fun startYouTubeIntent(id: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$id"))
            )
        }
    }

    private fun setupBinding() {
        binding.run {
            bottomNav.setupWithNavController(navController)
            lifecycleScope.launchWhenResumed {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.playlistFragment, R.id.youTubeFragment, R.id.searchResultsFragment -> {
                            bottomNav.show()
                            playerViewModel.nowPlayingInfo.value?.let {
                                bottomControls.visibility = View.VISIBLE
                            }
                        }
                        else -> {
                            bottomNav.hide()
                            bottomControls.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun setupMainViewModelObservers() {
        mainViewModel.errorEvent.observe(this@MainActivity) { event ->
            event?.getContentIfNotHandled()?.let {
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPlayerViewModelObservers() {
        playerViewModel.run {
            nowPlayingInfo.observe(this@MainActivity) {
                binding.nowPlaying = it
                binding.title.isSelected = true
                binding.bottomControls.visibility = it?.let {
                    when (navController.currentDestination?.id) {
                        R.id.playlistFragment, R.id.youTubeFragment, R.id.searchResultsFragment -> View.VISIBLE
                        else -> View.GONE
                    }
                } ?: View.GONE
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
    }

    private fun checkIntent() {
        intent?.takeIf {
            it.action == Intent.ACTION_SEND
        }?.getStringExtra(Intent.EXTRA_TEXT)?.let {
            mainViewModel.addToPlaylist(it.takeLast(11))
        }
    }
}