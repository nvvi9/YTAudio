package com.example.ytaudio.playlist

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import com.example.ytaudio.R
import com.example.ytaudio.adapter.RecyclerViewAdapter
import com.example.ytaudio.databinding.ItemPlaylistBinding
import com.example.ytaudio.domain.PlaylistItem


class PlaylistAdapter(
    private val fragment: PlaylistFragment,
    clickListener: (PlaylistItem) -> Unit
) : RecyclerViewAdapter<PlaylistItem, ItemPlaylistBinding>(
    DiffCallback(),
    fragment,
    clickListener
) {

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.action_select_all -> {
                selectAll()
                fragment.actionMode?.title =
                    fragment.getString(R.string.selected_items, selectedItems.size)
                true
            }
            R.id.action_delete -> {
                fragment.playlistViewModel.deleteFromDatabase(selectedItems.map { it.id })
                stopActionMode()
                true
            }
            else -> false
        }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean =
        mode?.run {
            menuInflater.inflate(R.menu.playlist_toolbar_action_mode, menu)
            true
        } ?: false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<ItemPlaylistBinding> {
        val binding =
            ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<ItemPlaylistBinding>, position: Int) {
        holder.binding.audioItem = getItem(position)
        super.onBindViewHolder(holder, position)
    }
}


private class DiffCallback : DiffUtil.ItemCallback<PlaylistItem>() {

    override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem) =
        oldItem == newItem
}