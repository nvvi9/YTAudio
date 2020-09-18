package com.nvvi9.ytstream.utils

import com.nvvi9.ytstream.model.streams.Stream
import java.net.URLDecoder
import java.net.URLEncoder


internal fun String.decode(): String =
    URLDecoder.decode(this, "UTF-8")

internal fun String.encode(): String =
    URLEncoder.encode(this, "UTF-8")

internal inline fun <A, B, R> ifNotNull(a: A?, b: B?, f: (A, B) -> R): R? {
    return if (a != null && b != null) {
        f(a, b)
    } else {
        null
    }
}

internal inline fun <R> tryOrNull(action: () -> R) =
    try {
        action()
    } catch (t: Throwable) {
        null
    }

internal fun MutableList<Stream>.encodeStreams(
    decodeSignatures: List<String>,
    encSignatures: Map<Int, String>
): List<Stream> = apply {
    encSignatures.keys.zip(decodeSignatures).forEach { (key, signature) ->
        find { it.streamDetails.itag == key }.also { remove(it) }?.url?.plus("&sig=$signature")
            ?.let { Stream.fromItag(key, it) }?.let { add(it) }
    }
}