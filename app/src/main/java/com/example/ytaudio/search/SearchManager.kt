package com.example.ytaudio.search

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.example.ytaudio.R
import com.example.ytaudio.utils.extensions.hideKeyboard

class SearchManager(
    searchItem: MenuItem,
    private val context: Context?,
    private val queryTextListener: QueryTextListener
) : SearchView.OnSuggestionListener, SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

    private val cursorAdapter = SimpleCursorAdapter(
        context, R.layout.item_search_suggest, null,
        arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1),
        intArrayOf(R.id.item_suggest),
        CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
    )

    private val searchView = (searchItem.actionView as SearchView).also {
        it.setOnQueryTextListener(this)
        it.setOnSuggestionListener(this)
        it.suggestionsAdapter = cursorAdapter
        it.queryHint = "Search"
        it.findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)
            .threshold = 1
    }

    init {
        searchItem.setOnActionExpandListener(this)
    }

    fun updateAutoCompleteRows(rowList: List<String>) {
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))

        rowList.forEachIndexed { index, s ->
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchView.hideKeyboard(context)
        query?.let {
            queryTextListener.queryTextSubmit(it)
        }

        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            queryTextListener.queryTextChange(it)
        }

        return true
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        searchView.isIconified = false
        searchView.requestFocusFromTouch()
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean = true


    class QueryTextListener(
        private val textSubmit: (query: String) -> Unit,
        private val textChange: (newText: String) -> Unit
    ) {
        fun queryTextSubmit(query: String) = textSubmit(query)
        fun queryTextChange(newText: String) = textChange(newText)
    }
}