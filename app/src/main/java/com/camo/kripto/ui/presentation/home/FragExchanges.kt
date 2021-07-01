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
import com.camo.kripto.R
import com.camo.kripto.databinding.FragExchangesBinding
import com.camo.kripto.ui.adapter.ExchangesAdapter
import com.camo.kripto.ui.adapter.CMCLoadStateAdapter
import com.camo.kripto.ui.viewModel.MarketCapVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
@AndroidEntryPoint
class FragExchanges : Fragment() {

    private lateinit var binding: FragExchangesBinding
    private val viewModel by activityViewModels<MarketCapVM>()
    private lateinit var adapter: ExchangesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragExchangesBinding.inflate(LayoutInflater.from(context))
        setupUI()
        getExchanges()
        return binding.root
    }

    private fun setupUI() {
        binding.rvFragExchanges1.layoutManager = LinearLayoutManager(context)
        adapter = ExchangesAdapter(ExchangesAdapter.Comparator)
        initAdapters()
        binding.rvFragExchanges1.adapter =
            adapter.withLoadStateFooter(footer = CMCLoadStateAdapter { adapter.retry() })
        binding.root.setOnRefreshListener {
            refresh()
            binding.root.isRefreshing = false
        }
    }

    private fun refresh() {
        getExchanges()
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {

            binding.rvFragExchanges1.visibility = View.GONE
        } else {

            binding.rvFragExchanges1.visibility = View.VISIBLE
        }
    }

    private fun initAdapters() {
//        TODO set able to order in fragment

        adapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isListEmpty)

            // Only show the list if refresh succeeds.
            binding.rvFragExchanges1.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.pbFragExchanges1.isVisible = loadState.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.btnFragExchanges1.isVisible = loadState.source.refresh is LoadState.Error

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

    private var exchangesJob: Job? = null
    private fun getExchanges() {
        exchangesJob?.cancel()
        exchangesJob = lifecycleScope.launch {
            viewModel.getExchanges().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
}