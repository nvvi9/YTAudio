package com.nvvi9.ytaudio.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment


open class YouTubeIntentFragment : Fragment() {

    protected fun startShareIntent(id:String){
        startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://youtu.be/$id")
            type = "text/plain"
        }, null))
    }

    protected fun startYouTubeIntent(id: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$id"))
            )
        }
    }
}