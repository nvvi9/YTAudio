package com.example.ytaudio.ui

import android.media.AudioManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.example.ytaudio.R
import com.example.ytaudio.ui.fragments.PlayFragment
import com.example.ytaudio.ui.viewmodels.MainActivityViewModel
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var mainActivityViewModelFactory: ViewModelProvider.Factory

    private val mainActivityViewModel: MainActivityViewModel by viewModels {
        mainActivityViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        volumeControlStream = AudioManager.STREAM_MUSIC

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        bottom_nav.setupWithNavController(navController)

        lifecycleScope.launchWhenResumed {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.playlistFragment, R.id.youTubeFragment -> bottom_nav.visibility =
                        View.VISIBLE
                    else -> bottom_nav.visibility = View.GONE
                }
            }
        }

        mainActivityViewModel.replaceEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                replacePlayFragment()
            }
        })
    }

    override fun androidInjector() = androidInjector

    private fun replacePlayFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PlayFragment.newInstance())
            .commit()
    }
}