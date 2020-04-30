package com.example.ytaudio.screens.url_link

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.commit451.youtubeextractor.Stream
import com.commit451.youtubeextractor.YouTubeExtraction
import com.commit451.youtubeextractor.YouTubeExtractor
import com.example.ytaudio.R
import com.example.ytaudio.databinding.SourceLinkFragmentBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SourceLinkFragment : Fragment() {

    companion object {
        const val STATE_LINK_TEXT = "currentLink"
    }

    private val extractor = YouTubeExtractor.Builder().build()

    private lateinit var binding: SourceLinkFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.source_link_fragment, container, false)

        binding.extractButton.setOnClickListener {
            extractURL(binding.linkText.text.run {
                subSequence(indexOf('=') + 1, length).toString()
            })
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence(STATE_LINK_TEXT, binding.linkText.text)

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.run {
            binding.linkText.text = getCharSequence(STATE_LINK_TEXT, "") as Editable
        }
    }

    @SuppressLint("CheckResult")
    private fun extractURL(videoID: String) {
        extractor.extract(videoID).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({ extraction ->
                passResult(extraction)
            }, { t ->
                onError(t)
            })
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
        Toast.makeText(this.context, "Extraction failed", Toast.LENGTH_SHORT).show()
    }

    private fun passResult(result: YouTubeExtraction) {
        val videoUrl = result.streams.filterIsInstance<Stream.AudioStream>().toString()
        findNavController().navigate(
            SourceLinkFragmentDirections.actionSourceLinkDestinationToAudioPlayerFragment(
                videoUrl!!,
                result.thumbnails.first().url
            )
        )
    }
}