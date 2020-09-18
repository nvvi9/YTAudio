package com.nvvi9.ytstream.model.raw

import com.nvvi9.ytstream.network.KtorService
import com.nvvi9.ytstream.utils.decode
import com.nvvi9.ytstream.utils.tryOrNull
import kotlinx.coroutines.coroutineScope
import java.util.regex.Pattern


@Suppress("BlockingMethodInNonBlockingContext")
inline class RawResponse(val raw: String) {

    val id get() = patternVideoId.matcher(raw).takeIf { it.find() }?.group(1)
    val title get() = patternTitle.matcher(raw).takeIf { it.find() }?.group(1)
    val isLiveStream get() = patternHlsvp.matcher(raw).find()
    val author get() = patternAuthor.matcher(raw).takeIf { it.find() }?.group(1)
    val channelId get() = patternChannelId.matcher(raw).takeIf { it.find() }?.group(1)
    val description get() = patternShortDescription.matcher(raw).takeIf { it.find() }?.group(1)
    val durationSeconds get() = patternLengthSeconds.matcher(raw).takeIf { it.find() }?.group(1)?.toLong()
    val viewCount get() = patternViewCount.matcher(raw).takeIf { it.find() }?.group(1)?.toLong()
    val expiresInSeconds get() = patternExpiresInSeconds.matcher(raw).takeIf { it.find() }?.group(1)?.toLong()
    val isEncoded get() = patternCipher.matcher(raw).find()
    val statusOk get() = patternStatusOk.matcher(raw).find()

    companion object {

        internal suspend fun fromId(id: String) = coroutineScope {
            tryOrNull {
                KtorService.getVideoInfo(id).decode().replace("\\u0026", "&").let {
                    RawResponse(it)
                }
            }
        }

        private val patternTitle: Pattern = Pattern.compile("\"title\"\\s*:\\s*\"(.*?)\"")
        private val patternVideoId: Pattern = Pattern.compile("\"videoId\"\\s*:\\s*\"(.+?)\"")
        private val patternAuthor: Pattern = Pattern.compile("\"author\"\\s*:\\s*\"(.+?)\"")
        private val patternChannelId: Pattern = Pattern.compile("\"channelId\"\\s*:\\s*\"(.+?)\"")
        private val patternLengthSeconds: Pattern =
            Pattern.compile("\"lengthSeconds\"\\s*:\\s*\"(\\d+?)\"")
        private val patternViewCount: Pattern = Pattern.compile("\"viewCount\"\\s*:\\s*\"(\\d+?)\"")
        private val patternExpiresInSeconds: Pattern =
            Pattern.compile("\"expiresInSeconds\"\\s*:\\s*\"(\\d+?)\"")
        private val patternShortDescription: Pattern =
            Pattern.compile("\"shortDescription\"\\s*:\\s*\"(.+?)\"")
        private val patternStatusOk: Pattern = Pattern.compile("status=ok(&|,|\\z)")
        private val patternHlsvp: Pattern = Pattern.compile("hlsvp=(.+?)(&|\\z)")
        private val patternCipher: Pattern =
            Pattern.compile("\"signatureCipher\"\\s*:\\s*\"(.+?)\"")
    }
}