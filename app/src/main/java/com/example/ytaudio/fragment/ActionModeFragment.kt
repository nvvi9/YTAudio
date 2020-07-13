package com.example.ytaudio.fragment

import android.os.Bundle
import android.view.ActionMode
import androidx.fragment.app.Fragment
import com.example.ytaudio.di.Injectable
import dagger.android.support.AndroidSupportInjection


abstract class ActionModeFragment : Fragment(), Injectable {

    open var actionMode: ActionMode? = null

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        AndroidSupportInjection.inject(this)
        onCreateActionModeFragment(savedInstanceState)
    }

    open fun onCreateActionModeFragment(savedInstanceState: Bundle?) = Unit
}