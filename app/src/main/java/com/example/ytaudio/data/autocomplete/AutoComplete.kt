package com.example.ytaudio.data.autocomplete

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "toplevel", strict = false)
class AutoComplete @JvmOverloads constructor(
    @field: ElementList(inline = true)
    var items: List<CompleteSuggestion>? = null
)


@Root(name = "CompleteSuggestion", strict = false)
class CompleteSuggestion @JvmOverloads constructor(
    @field: Element(name = "suggestion")
    var suggestion: Suggestion? = null
)


@Root(name = "suggestion", strict = false)
class Suggestion @JvmOverloads constructor(
    @field: Attribute(name = "data")
    var data: String = ""
)