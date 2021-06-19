package com.camo.kripto.ui.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.databinding.FragNewsCategoryBinding
import com.camo.kripto.remote.api.CGService
import com.camo.kripto.ui.adapter.NewsAdapter
import com.camo.kripto.ui.viewModel.MarketCapVM
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

private const val FRAG_NEWS_CATEGORY = "news_category_key"

class FragNewsCategory : Fragment() {
    private lateinit var binding: FragNewsCategoryBinding
    private lateinit var adapterNews: NewsAdapter
    private lateinit var key: String
    private val viewModel: MarketCapVM by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragNewsCategoryBinding.inflate(inflater, container, false)
        key = arguments?.getString(FRAG_NEWS_CATEGORY) ?: CGService.NewsCategory.GENERAL.category
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getNewData()
    }

    private var newsJob: Job? = null
    private fun getNewData() {
        newsJob?.cancel()
        newsJob = lifecycleScope.launch {
            viewModel.getNews(key).collectLatest { pagingData ->
                adapterNews.submitData(pagingData)
            }
        }
    }

    private fun setupUI() {
        adapterNews = NewsAdapter(key,NewsAdapter.Comparator)
        binding.rvFragNews.layoutManager = LinearLayoutManager(context)
        binding.rvFragNews.adapter = adapterNews
        binding.root.setOnRefreshListener {
            refresh()
        }
        initAdapters()
    }
    private fun refresh() {
        getNewData()
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.rvFragNews.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.rvFragNews.visibility = View.VISIBLE
        }
    }

    private fun initAdapters() {
//        TODO set able to order in fragment
        adapterNews.addLoadStateListener { loadState ->
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && adapterNews.itemCount == 0
            showEmptyList(isListEmpty)
            binding.rvFragNews.isVisible = loadState.source.refresh is LoadState.NotLoading
            binding.root.isRefreshing = loadState.source.refresh is LoadState.Loading
            binding.tvPullToRefresh.isVisible = loadState.source.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireActivity(),
                    "\uD83D\uDE28 Wooops",
                    Toast.LENGTH_LONG
                ).show()
                Timber.d(it.toString())
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(key: CGService.NewsCategory) =
            FragNewsCategory().apply {
                arguments = Bundle().apply {
                    putString(FRAG_NEWS_CATEGORY, key.category)
                }
            }
    }
}
