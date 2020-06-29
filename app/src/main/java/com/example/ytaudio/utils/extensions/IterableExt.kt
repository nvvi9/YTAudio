package com.example.ytaudio.utils.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext


suspend fun <T> Iterable<T>.forEachParallel(f: suspend (T) -> Unit) =
    withContext(Dispatchers.IO) { map { async { f(it) } }.awaitAll() }

suspend fun <T, V> Iterable<T>.mapParallel(f: suspend (T) -> V) =
    withContext(Dispatchers.IO) { map { async { f(it) } }.awaitAll() }
