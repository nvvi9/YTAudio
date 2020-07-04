package com.example.ytaudio.utils.extensions

import kotlinx.coroutines.*


suspend fun <T> Iterable<T>.forEachParallel(f: suspend (T) -> Unit) =
    withContext(Dispatchers.IO) { map { async { f(it) } }.awaitAll() }

suspend fun <T> Iterable<T>.forEachParallel(
    dispatcher: CoroutineDispatcher,
    f: suspend (T) -> Unit
) =
    withContext(dispatcher) { map { async { f(it) } }.awaitAll() }

suspend fun <T, V> Iterable<T>.mapParallel(f: suspend (T) -> V) =
    withContext(Dispatchers.IO) { map { async { f(it) } }.awaitAll() }

suspend fun <T, V> Iterable<T>.mapParallel(dispatcher: CoroutineDispatcher, f: suspend (T) -> V) =
    withContext(dispatcher) { map { async { f(it) } }.awaitAll() }