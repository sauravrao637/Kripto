package com.camo.kripto.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivityMarketCapBinding
import com.camo.kripto.ui.adapter.MCLoadStateAdapter
import com.camo.kripto.ui.adapter.MarketCapAdapter
import com.camo.kripto.ui.preferences.SettingsActivity
import com.camo.kripto.ui.viewModel.MarketCapVM
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MarketCap : AppCompatActivity() {

    private val TAG = MarketCap::class.simpleName

    private lateinit var adapter: MarketCapAdapter
    private lateinit var binding: ActivityMarketCapBinding
    private lateinit var viewModel: MarketCapVM



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketCapBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        setupViewModel()
        setupUI()
        setupObservers()


    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this
        ).get(MarketCapVM::class.java)

        //must post currency as soon as vm setup
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var curr = sharedPreferences.getString("pref_currency", "inr")
        if (curr == null) curr = "inr"
        viewModel.prefCurrency.postValue(curr)
    }

    private fun setupUI() {
        binding.rvMarketCap.layoutManager = LinearLayoutManager(this)
        adapter =
            MarketCapAdapter(viewModel.prefCurrency.value ?: "inr", MarketCapAdapter.Comparator)
        binding.rvMarketCap.addItemDecoration(
            DividerItemDecoration(
                binding.rvMarketCap.context,
                (binding.rvMarketCap.layoutManager as LinearLayoutManager).orientation
            )
        )

        binding.retryButton.setOnClickListener { adapter.retry() }
        initAdapter()
        binding.rvMarketCap.adapter =
            adapter.withLoadStateFooter(footer = MCLoadStateAdapter { adapter.retry() })

    }

    private fun initAdapter() {

        adapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isListEmpty)

            // Only show the list if refresh succeeds.
            binding.rvMarketCap.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    this,
                    "\uD83D\uDE28 Wooops",
                    Toast.LENGTH_LONG
                ).show()
                Log.d(TAG,it.toString())
            }
        }
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


    private fun setupObservers() {
        viewModel.prefCurrency.observe(this) {
            if (it != null) {
                getNewData(it);
                adapter.curr = it;
            }
        }
    }

    private var capDataJob: Job? = null
    private fun getNewData(it: String?) {
        capDataJob?.cancel()
        capDataJob = lifecycleScope.launch {
            viewModel.getMarketCap(it).collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
//            adapter.loadStateFlow.collectLatest { loadStates ->
//                binding.progressBar.isVisible = loadStates.refresh is LoadState.Loading
////                retry.isVisible = loadState.refresh !is LoadState.Loading
//                binding.errorMsg.isVisible = loadStates.refresh is LoadState.Error
//            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.market_cap, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        R.id.action_refresh -> {
//            TODO(lazy) improve
            adapter.retry()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}