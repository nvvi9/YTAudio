package com.example.ytaudio.service.extensions

import android.content.ContentResolver
import android.net.Uri
import java.io.File

private const val AUTHORITY = "com.example.ytaudio.service.library.provider"

fun File.asArtContentUri() = Uri.Builder()
    .scheme(ContentResolver.SCHEME_CONTENT)
    .authority(AUTHORITY)
    .appendPath(this.path)
    .build()
