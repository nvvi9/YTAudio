package com.example.ytaudio.ui.fragments


/*
class SearchFragment : ActionModeFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: SearchFragmentBinding

    val viewModel: SearchViewModel by viewModels {
        viewModelFactory
    }

    private val videoItemAdapter =
        SearchAdapter(this) {
            Toast.makeText(this@SearchFragment.context, it.videoId, Toast.LENGTH_SHORT).show()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchFragmentBinding.inflate(inflater)

        binding.apply {
            viewModel = this@SearchFragment.viewModel
            videoItemsView.adapter = videoItemAdapter
            videoItemsView.addItemDecoration(
                DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            )
            lifecycleOwner = this@SearchFragment
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_toolbar_menu, menu)

        val searchManager = SearchManager(
            menu.findItem(R.id.action_search), context,
            SearchManager.QueryTextListener(
                { viewModel.setResponse(it) },
                { viewModel.setAutoComplete(it) })
        )

        viewModel.autoComplete.observe(viewLifecycleOwner, Observer {
            searchManager.updateAutoCompleteRows(it)
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                if (!viewModel.searchItemList.value.isNullOrEmpty()) {
                    videoItemAdapter.startActionMode()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        binding.root.hideKeyboard(context)
        super.onPause()
    }
}
 */