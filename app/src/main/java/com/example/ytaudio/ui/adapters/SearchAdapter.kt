package com.example.ytaudio.ui.adapters

/*
class SearchAdapter(
    private val fragment: SearchFragment,
    clickListener: (YouTubeItem) -> Unit
) : RecyclerViewAdapter<YouTubeItem, SearchItemBinding>(SearchItemDiffCallback(), fragment, clickListener) {

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.action_add -> {
                fragment.viewModel.insertInDatabase(selectedItems.toList())
                stopActionMode()
                true
            }
            else -> false
        }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) =
        mode?.run {
            menuInflater.inflate(R.menu.search_toolbar_action_mode, menu)
            true
        } ?: false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<SearchItemBinding> {
        val binding =
            SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<SearchItemBinding>, position: Int) {
        holder.binding.searchItem = getItem(position)
        super.onBindViewHolder(holder, position)
    }
}


private class SearchItemDiffCallback : DiffUtil.ItemCallback<YouTubeItem>() {

    override fun areItemsTheSame(oldItem: YouTubeItem, newItem: YouTubeItem) =
        oldItem.videoId == newItem.videoId

    override fun areContentsTheSame(oldItem: YouTubeItem, newItem: YouTubeItem) =
        oldItem == newItem
}

 */