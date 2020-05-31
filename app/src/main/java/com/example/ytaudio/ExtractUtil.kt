package com.example.ytaudio

import android.annotation.SuppressLint
import com.commit451.youtubeextractor.YouTubeExtraction
import com.commit451.youtubeextractor.YouTubeExtractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


@SuppressLint("CheckResult")
fun onExtract(
    videoLink: String,
    passResult: (result: YouTubeExtraction, youtubeId: String) -> Unit,
    onError: (t: Throwable) -> Unit
) {
    val youtubeId = videoLink.takeLastWhile { it != '=' && it != '/' }
    val extractor = YouTubeExtractor.Builder().build()

    extractor.extract(youtubeId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread()).subscribe({ extraction ->
            passResult(extraction, youtubeId)
        }, { t ->
            onError(t)
        })
}