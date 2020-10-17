package com.nvvi9.ytaudio.domain.mapper

import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.autocomplete.AutoComplete


object SuggestionMapper : BaseMapper<AutoComplete, List<String>> {
    override fun map(type: AutoComplete) =
        type.items?.mapNotNull { it.suggestion?.data }
}