package com.nvvi9.ytstream.js

import com.nvvi9.ytstream.network.KtorService
import com.nvvi9.ytstream.utils.tryOrNull
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern


internal data class JsDecryption(
    val mainVariable: String,
    val decryptionFunction: String
) {

    companion object {

        private val jsHashMap = ConcurrentHashMap<String, JsDecryption>()

        @Suppress("BlockingMethodInNonBlockingContext")
        suspend fun fromVideoPageSource(videPageSource: String) = coroutineScope {
            patternDecryptionJsFile.matcher(videPageSource).takeIf { it.find() }
                ?.group(0)?.replace("\\/", "/")?.let { jsPath ->
                    jsHashMap.getOrPut(jsPath) {
                        tryOrNull {
                            KtorService.getJsFile(jsPath).replace("\n", " ")
                        }?.let { jsFile ->
                            var matcher = patternSignatureDecryptionFunction.matcher(jsFile)
                            if (matcher.find()) {
                                val decryptionFunction = matcher.group(1)
                                val patternMainVariable =
                                    Pattern.compile(
                                        "(var |\\s|,|;)${
                                            decryptionFunction?.replace("$", "\\$")
                                        }(=function\\((.{1,3})\\)\\{)"
                                    )
                                matcher = patternMainVariable.matcher(jsFile)
                                var mainVariable: String = if (matcher.find()) {
                                    "var $decryptionFunction ${matcher.group(2)}"
                                } else {
                                    val patternMainFunction =
                                        Pattern.compile(
                                            "function ${
                                                decryptionFunction?.replace("$", "\\$")
                                            }(\\((.{1,3})\\)\\{)"
                                        )
                                    matcher = patternMainFunction.matcher(jsFile)
                                    "function ${matcher.takeIf { it.find() }?.group(2)}"
                                }
                                var startIndex = matcher.end()
                                var braces = 1
                                for (i in startIndex until jsFile.length) {
                                    if (braces == 0 && startIndex + 5 < i) {
                                        mainVariable += jsFile.substring(startIndex, i) + ";"
                                        break
                                    }
                                    if (jsFile[i] == '{') {
                                        braces++
                                    } else if (jsFile[i] == '}') {
                                        braces--
                                    }
                                }

                                matcher = patternVariableFunction.matcher(mainVariable)

                                while (matcher.find()) {
                                    val variableDef = "var ${matcher.group(2)}={"
                                    if (mainVariable.contains(variableDef)) {
                                        continue
                                    }
                                    startIndex = jsFile.indexOf(variableDef) + variableDef.length

                                    braces = 1

                                    for (i in startIndex until jsFile.length) {
                                        if (braces == 0) {
                                            mainVariable += variableDef + jsFile.substring(
                                                startIndex,
                                                i
                                            ) + ";"
                                            break
                                        }
                                        if (jsFile[i] == '{') {
                                            braces++
                                        } else if (jsFile[i] == '}') {
                                            braces--
                                        }
                                    }
                                }

                                matcher = patternFunction.matcher(mainVariable)
                                while (matcher.find()) {
                                    val functionDef = "function ${matcher.group(2)}("
                                    if (mainVariable.contains(functionDef)) {
                                        continue
                                    }
                                    startIndex = jsFile.indexOf(functionDef) + functionDef.length

                                    braces = 0

                                    for (i in startIndex until jsFile.length) {
                                        if (braces == 0 && startIndex + 5 < i) {
                                            mainVariable += functionDef + jsFile.substring(
                                                startIndex,
                                                i
                                            ) + ";"
                                            break
                                        }
                                        if (jsFile[i] == '{') {
                                            braces++
                                        } else if (jsFile[i] == '}') {
                                            braces--
                                        }
                                    }
                                }
                                JsDecryption(mainVariable, decryptionFunction)
                            } else {
                                null
                            }
                        }
                    }
                }
        }

        private val patternVariableFunction: Pattern =
            Pattern.compile("([{; =])([a-zA-Z$][a-zA-Z0-9$]{0,2})\\.([a-zA-Z$][a-zA-Z0-9$]{0,2})\\(")
        private val patternFunction: Pattern =
            Pattern.compile("([{; =])([a-zA-Z\$_][a-zA-Z0-9$]{0,2})\\(")
        private val patternDecryptionJsFile: Pattern =
            Pattern.compile("\\\\/player\\\\/([^\"]+?)\\.js")
        private val patternSignatureDecryptionFunction: Pattern =
            Pattern.compile("(?:\\b|[^a-zA-Z0-9\$])([a-zA-Z0-9\$]{2})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)")
    }
}
