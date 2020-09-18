package com.nvvi9.ytstream.js

import com.squareup.duktape.Duktape
import kotlinx.coroutines.coroutineScope


internal object JsExecutor {

    private val duktape = Duktape.create()

    suspend fun executeScript(script: String) = coroutineScope {
        duktape.evaluate(script) as String?
    }
}