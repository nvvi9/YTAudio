package com.nvvi9.ytstream.js

import kotlinx.coroutines.coroutineScope
import javax.script.Invocable
import javax.script.ScriptEngineManager


internal object JsExecutor {

    private val scriptEngineManager = ScriptEngineManager()
    private val scriptEngine = scriptEngineManager.getEngineByName("nashorn")

    suspend fun executeScript(functionName: String, script: String) = coroutineScope {
        scriptEngine.eval(script)
        ((scriptEngine as Invocable).invokeFunction(functionName) as String?)
    }
}
