package com.nvvi9.ytstream.model.streams

import com.nvvi9.ytstream.model.codecs.AudioCodec
import com.nvvi9.ytstream.model.codecs.VideoCodec


data class Stream(
    val url: String,
    val streamDetails: StreamDetails
) {

    companion object {

        internal fun fromItag(itag: Int, uri: String): Stream? =
            Stream(uri, ITAG_MAP.getOrElse(itag) {
                return null
            })

        private val ITAG_MAP = mapOf(
            140 to StreamDetails(140, StreamType.AUDIO, Extension.M4A, AudioCodec.AAC, null, null, 128),
            141 to StreamDetails(141, StreamType.AUDIO, Extension.M4A, AudioCodec.AAC, null, null, 256),
            256 to StreamDetails(256, StreamType.AUDIO, Extension.M4A, AudioCodec.AAC, null, null, 192),
            258 to StreamDetails(258, StreamType.AUDIO, Extension.M4A, AudioCodec.AAC, null, null, 384),
            171 to StreamDetails(171, StreamType.AUDIO, Extension.WEBM, AudioCodec.VORBIS, null, null, 128),
            249 to StreamDetails(249, StreamType.AUDIO, Extension.WEBM, AudioCodec.OPUS, null, null, 48),
            250 to StreamDetails(250, StreamType.AUDIO, Extension.WEBM, AudioCodec.OPUS, null, null, 64),
            251 to StreamDetails(251, StreamType.AUDIO, Extension.WEBM, AudioCodec.OPUS, null, null, 160),

            160 to StreamDetails(160, StreamType.VIDEO, Extension.MP4, null, VideoCodec.H264, 144, null),
            133 to StreamDetails(133, StreamType.VIDEO, Extension.MP4, null, VideoCodec.H264, 240, null),
            134 to StreamDetails(134, StreamType.VIDEO, Extension.MP4, null, VideoCodec.H264, 360, null),
            135 to StreamDetails(135, StreamType.VIDEO, Extension.MP4, null, VideoCodec.H264, 480, null),
            136 to StreamDetails(136, StreamType.VIDEO, Extension.MP4, null, VideoCodec.H264, 720, null),
            137 to StreamDetails(137, StreamType.VIDEO, Extension.MP4, null, VideoCodec.H264, 1080, null),
            264 to StreamDetails(264, StreamType.VIDEO, Extension.MP4, null, VideoCodec.H264, 1440, null),
            266 to StreamDetails(266, StreamType.VIDEO, Extension.MP4, null, VideoCodec.H264, 2160, null),
            298 to StreamDetails(298, StreamType.VIDEO, Extension.MP4, null, VideoCodec.H264, 720, null, 60),
            299 to StreamDetails(299, StreamType.VIDEO, Extension.MP4, null, VideoCodec.H264, 1080, null, 60),
            278 to StreamDetails(278, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 144, null),
            242 to StreamDetails(242, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 240, null),
            243 to StreamDetails(243, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 360, null),
            244 to StreamDetails(244, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 480, null),
            247 to StreamDetails(247, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 720, null),
            248 to StreamDetails(248, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 1080, null),
            271 to StreamDetails(271, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 1440, null),
            313 to StreamDetails(313, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 2160, null),
            302 to StreamDetails(302, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 720, null, 60),
            308 to StreamDetails(308, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 1440, null, 60),
            303 to StreamDetails(303, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 1080, null, 60),
            315 to StreamDetails(315, StreamType.VIDEO, Extension.WEBM, null, VideoCodec.VP9, 2160, null, 60),

            17 to StreamDetails(17, StreamType.MULTIPLEXED, Extension.`3GP`, AudioCodec.AAC, VideoCodec.MPEG4, 144, 24),
            36 to StreamDetails(36, StreamType.MULTIPLEXED, Extension.`3GP`, AudioCodec.AAC, VideoCodec.MPEG4, 240, 32),
            5 to StreamDetails(5, StreamType.MULTIPLEXED, Extension.FLV, AudioCodec.MP3, VideoCodec.H263, 144, 64),
            43 to StreamDetails(43, StreamType.MULTIPLEXED, Extension.WEBM, AudioCodec.VORBIS, VideoCodec.VP8, 360, 128),
            18 to StreamDetails(18, StreamType.MULTIPLEXED, Extension.MP4, AudioCodec.AAC, VideoCodec.H264, 360, 96),
            22 to StreamDetails(22, StreamType.MULTIPLEXED, Extension.MP4, AudioCodec.AAC, VideoCodec.H264, 720, 192),

            91 to StreamDetails(91, StreamType.LIVE, Extension.MP4, AudioCodec.AAC, VideoCodec.H264, 144, 48),
            92 to StreamDetails(92, StreamType.LIVE, Extension.MP4, AudioCodec.AAC, VideoCodec.H264, 240, 48),
            93 to StreamDetails(93, StreamType.LIVE, Extension.MP4, AudioCodec.AAC, VideoCodec.H264, 360, 128),
            94 to StreamDetails(94, StreamType.LIVE, Extension.MP4, AudioCodec.AAC, VideoCodec.H264, 480, 128),
            95 to StreamDetails(95, StreamType.LIVE, Extension.MP4, AudioCodec.AAC, VideoCodec.H264, 720, 256),
            96 to StreamDetails(96, StreamType.LIVE, Extension.MP4, AudioCodec.AAC, VideoCodec.H264, 1080, 256)
        )
    }
}