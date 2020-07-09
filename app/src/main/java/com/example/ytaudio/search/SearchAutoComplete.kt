package com.example.ytaudio.search

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.example.ytaudio.R


class SearchAutoComplete(context: Context?, private val searchView: SearchView) :
    SearchView.OnSuggestionListener {

    private val cursorAdapter = SimpleCursorAdapter(
        context, R.layout.item_search_suggest, null,
        arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1),
        intArrayOf(R.id.item_suggest),
        CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
    )

    init {
        searchView.apply {
            suggestionsAdapter = cursorAdapter
            setOnSuggestionListener(this@SearchAutoComplete)
            findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)
                .threshold = 1
        }
    }

    fun updateRows(list: List<String>) {
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))

        list.forEachIndexed { index, s ->
            cursor.addRow(arrayOf(index, s))
        }
        cursorAdapter.changeCursor(cursor)
    }

    override fun onSuggestionSelect(position: Int): Boolean = false

    override fun onSuggestionClick(position: Int): Boolean {
        val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
        val selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
        searchView.setQuery(selection, false)

        return true
    }
}