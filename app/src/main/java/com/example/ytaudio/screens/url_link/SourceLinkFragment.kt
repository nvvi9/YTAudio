package com.example.ytaudio.screens.url_link

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.commit451.youtubeextractor.YouTubeExtractor
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.databinding.SourceLinkFragmentBinding

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

        val application = requireNotNull(this.activity).application
        val dataSource = AudioDatabase.getInstance(application).audioDatabaseDao
        val viewModelFactory = SourceLinkViewModelFactory(dataSource, application)
        val sourceLinkViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SourceLinkViewModel::class.java)
        binding.lifecycleOwner = this

        binding.extractButton.setOnClickListener { view ->
            if (binding.linkText.text.isNotBlank()) {
                (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
                    view.windowToken,
                    0
                )

                sourceLinkViewModel.onExtract(binding.linkText.text.toString())

//                extractURL(binding.linkText.text.takeLastWhile { it != '=' && it != '/' }
//                    .toString())
            }
        }

        sourceLinkViewModel.showToastEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(context, "Extraction failed", Toast.LENGTH_SHORT).show()
                sourceLinkViewModel.doneShowingToast()
            }
        })

        sourceLinkViewModel.navigateToPlayer.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigate(SourceLinkFragmentDirections.actionSourceLinkDestinationToPlayerFragment())
            }
        })

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

//    @SuppressLint("CheckResult")
//    private fun extractURL(videoID: String) {
//        extractor.extract(videoID).subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread()).subscribe({ extraction ->
//                passResult(extraction)
//            }, { t ->
//                onError(t)
//            })
//    }
//
//    private fun onError(t: Throwable) {
//        t.printStackTrace()
//        Toast.makeText(this.context, "Extraction failed", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun passResult(result: YouTubeExtraction) {
//        result.streams.filterIsInstance<Stream.AudioStream>().filter {
//            listOf(
//                Stream.FORMAT_M4A,
//                Stream.FORMAT_MPEG_4
//            ).any { streamType -> streamType == it.format }
//        }.takeIf { it.isNotEmpty() }?.get(0)?.url?.let {
//            findNavController().navigate(
//                SourceLinkFragmentDirections.actionSourceLinkDestinationToAudioPlayerFragment(
//                    it,
//                    result.thumbnails.first().url,
//                    result.title
//                )
//            )
//        }
//    }
}