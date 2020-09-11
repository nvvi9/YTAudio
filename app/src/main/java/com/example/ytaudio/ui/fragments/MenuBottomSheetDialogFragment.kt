package com.example.ytaudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.ytaudio.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView


class MenuBottomSheetDialogFragment(
    private val menuRes: Int,
    private val onNavigationItemSelected: (MenuItem) -> Boolean
) : BottomSheetDialogFragment() {

    private lateinit var navigationView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.menu_bottom_sheet_dialog_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationView = view.findViewById<NavigationView>(R.id.navigation_view).apply {
            inflateMenu(menuRes)

            setNavigationItemSelectedListener {
                onNavigationItemSelected(it).also { consumed ->
                    if (consumed) dismiss()
                }
            }
        }
    }
}