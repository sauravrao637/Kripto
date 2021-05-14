package com.camo.kripto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.database.AppDb
import com.camo.kripto.database.model.CoinIdName
import com.camo.kripto.database.repository.AppDbRepo
import com.camo.kripto.databinding.FragMarketBinding
import com.camo.kripto.ui.adapter.MCLoadStateAdapter
import com.camo.kripto.ui.adapter.MarketCapAdapter
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.viewModel.MarketCapVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class FragMarket : Fragment() {


    private var repo: AppDbRepo? = null
    private lateinit var adapter: MarketCapAdapter
    private lateinit var binding: FragMarketBinding
    private lateinit var viewModel: MarketCapVM
    private lateinit var key: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        key = arguments?.getString("key") ?: KEY_ALL
        binding = FragMarketBinding.inflate(LayoutInflater.from(context))

        repo = context?.let { AppDb.getAppDb(it)?.let { AppDbRepo(it) } }

        setupVM()
        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.prefCurrency.observe(viewLifecycleOwner) {

            getNewData(it, viewModel.orderby.value, viewModel.duration.value)
            adapter.curr = it

        }
        viewModel.orderby.observe(viewLifecycleOwner) {

            getNewData(viewModel.prefCurrency.value, it, viewModel.duration.value)

        }
        viewModel.duration.observe(viewLifecycleOwner) {
            binding.tvDuration.text = it
            getNewData(viewModel.prefCurrency.value, viewModel.orderby.value, it)
        }
    }

    private fun setupVM() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val curr = sharedPreferences.getString("pref_currency", "inr") ?: "inr"
        val prefDur = sharedPreferences.getString("pref_per_change_dur", "1h") ?: "1h"
        val prefOrder = sharedPreferences.getString(
            "pref_order",
            "market_cap_desc"
        ) ?: "market_cap_desc"
        viewModel = ViewModelProviders.of(
            requireActivity(), VMFactory(
                CGApiHelper(RetrofitBuilder.CG_SERVICE),
                curr = curr,
                duration = prefDur,
                prefOrder = prefOrder
            )
        ).get(MarketCapVM::class.java)
    }

    private fun setupUI() {
        binding.rvMarketCap.layoutManager = LinearLayoutManager(context)
        adapter =
            MarketCapAdapter(viewModel.prefCurrency.value ?: "inr", MarketCapAdapter.Comparator)

        binding.rvMarketCap.addItemDecoration(
            DividerItemDecoration(
                binding.rvMarketCap.context,
                (binding.rvMarketCap.layoutManager as LinearLayoutManager).orientation
            )
        )

        binding.root.setOnRefreshListener {
            refresh()
            binding.root.isRefreshing = false
        }

        binding.retryButton.setOnClickListener { adapter.retry() }
        initAdapters()
        binding.rvMarketCap.adapter =
            adapter.withLoadStateFooter(footer = MCLoadStateAdapter { adapter.retry() })


        binding.tvDuration.setOnClickListener {
            viewModel.toggleDuration()
        }
    }

    private fun refresh() {
        getNewData(viewModel.prefCurrency.value, viewModel.orderby.value, viewModel.duration.value)
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.rvMarketCap.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.rvMarketCap.visibility = View.VISIBLE
        }
    }

    private fun initAdapters() {
//        TODO set able to order in fragment

        adapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isListEmpty)
            binding.rvMarketCap.isVisible = loadState.source.refresh is LoadState.NotLoading
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

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


    var capDataJob: Job? = null
    private fun getNewData(it: String?, order: String?, dur: String?) {
        capDataJob?.cancel()
        var coins: List<CoinIdName>? = null
        capDataJob = lifecycleScope.launch {
            if (key.equals(KEY_FAV)) {
                coins = withContext(Dispatchers.IO) { repo?.getFavCoins() }
            }
            viewModel.getMarketCap(it, order, dur, coins).collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }


    companion object {

        val KEY_ALL = "all"
        val KEY_FAV = "fav"

        fun getInst(data: String): FragMarket {
            val myFragment = FragMarket()
            val args = Bundle()
            args.putString("key", data)
            myFragment.arguments = args
            return myFragment
        }
    }
}
