package com.nvvi9.ytstream.model.streams

import com.nvvi9.ytstream.js.JsDecryption
import com.nvvi9.ytstream.model.VideoDetails
import com.nvvi9.ytstream.model.raw.Raw
import com.nvvi9.ytstream.utils.decode
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import java.util.regex.Pattern


internal class EncodedStreams(
    val encodedSignatures: Map<Int, String>,
    val streams: List<Stream>,
    val videoDetails: VideoDetails,
    private val jsDecryption: JsDecryption?
) {

    val jsCode: String?
        get() = jsDecryption?.let { (variable, function) ->
            encodedSignatures.values.foldIndexed("$variable function decode(){return ") { index, acc, s ->
                acc.plus("$function('$s') ${if (index < encodedSignatures.size - 1) "+\"\\n\"+" else ""}")
            }.plus("};decode();")
        }


    companion object {

        suspend fun fromRawFlow(raw: Raw?) = flow {
            emit(raw?.let { fromRaw(it) })
        }

        private suspend fun fromRaw(raw: Raw) = coroutineScope {
            val isEncoded = raw.videoDetails.isSignatureEncoded
            val statusOk = raw.videoDetails.statusOk

            val jsDecryptionDef = if (isEncoded || !statusOk) {
                async { JsDecryption.fromVideoPageSource(raw.videoPageSource) }
            } else {
                null
            }

            val (encodedSignatures, streams) =
                getEncSignaturesStreams(
                    isEncoded,
                    if (isEncoded || !statusOk) raw.videoPageSource else raw.videoDetails.rawResponse.raw
                )

            EncodedStreams(encodedSignatures, streams, raw.videoDetails, jsDecryptionDef?.await())
        }

        private fun getEncSignaturesStreams(
            isEncoded: Boolean,
            raw: String
        ): Pair<Map<Int, String>, MutableList<Stream>> {
            val matcher = (if (isEncoded) patternCipher else patternUrl).matcher(raw)
            val encodedSignatures = mutableMapOf<Int, String>()
            val streams = mutableListOf<Stream>()
            while (matcher.find()) {
                var sig: String? = null
                var url: String
                if (isEncoded) {
                    val cipher = matcher.group(1)
                    var mat = patternCipherUrl.matcher(cipher)

                    if (mat.find()) {
                        url = mat.group(1).decode()
                        mat = patternEncSig.matcher(cipher)
                        if (mat.find()) {
                            sig = mat.group(1).decode()
                        } else {
                            continue
                        }
                    } else {
                        continue
                    }
                } else {
                    url = matcher.group(1)
                }

                val itag = patternItag.matcher(url).takeIf { it.find() }?.group(1)
                    ?.takeUnless { it.contains("&source=yt_otf&") }?.toInt() ?: continue

                Stream.fromItag(itag, url)?.let { streams.add(it) }

                sig?.let { encodedSignatures[itag] = it }
            }
            return encodedSignatures to streams
        }

        private val patternItag: Pattern = Pattern.compile("itag=([0-9]+?)(&|\\z)")
        private val patternEncSig: Pattern = Pattern.compile("s=(.{10,}?)(\\\\\\\\u0026|\\z)")
        private val patternUrl: Pattern = Pattern.compile("\"url\"\\s*:\\s*\"(.+?)\"")
        private val patternCipher: Pattern =
            Pattern.compile("\"signatureCipher\"\\s*:\\s*\"(.+?)\"")
        private val patternCipherUrl: Pattern = Pattern.compile("url=(.+?)(\\\\\\\\u0026|\\z)")
    }
}