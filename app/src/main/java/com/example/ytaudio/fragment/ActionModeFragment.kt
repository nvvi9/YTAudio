package com.example.ytaudio.fragment

import android.os.Bundle
import android.view.ActionMode
import androidx.fragment.app.Fragment


abstract class ActionModeFragment : Fragment() {

    open var actionMode: ActionMode? = null

    abstract fun onCreate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        onCreate()
    }
}