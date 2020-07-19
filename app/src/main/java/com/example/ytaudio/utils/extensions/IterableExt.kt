package com.example.ytaudio.utils.extensions

import kotlinx.coroutines.*


suspend fun <T> Iterable<T>.forEachParallel(block: suspend (T) -> Unit) =
    withContext(Dispatchers.IO) { map { async { block(it) } }.awaitAll() }

suspend fun <T> Iterable<T>.forEachParallel(
    dispatcher: CoroutineDispatcher,
    block: suspend (T) -> Unit
) = withContext(dispatcher) { map { async { block(it) } }.awaitAll() }

suspend fun <T, V> Iterable<T>.mapParallel(block: suspend (T) -> V) =
    withContext(Dispatchers.IO) { map { async { block(it) } }.awaitAll() }

suspend fun <T, V> Iterable<T>.mapParallel(
    dispatcher: CoroutineDispatcher,
    block: suspend (T) -> V
) =
    withContext(dispatcher) { map { async { block(it) } }.awaitAll() }