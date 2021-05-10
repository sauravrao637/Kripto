package com.camo.kripto.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.R
import com.camo.kripto.database.AppDb
import com.camo.kripto.database.model.CoinIdName
import com.camo.kripto.database.repository.AppDbRepo
import com.camo.kripto.databinding.FragMarketBinding
import com.camo.kripto.ui.adapter.MCLoadStateAdapter
import com.camo.kripto.ui.adapter.MarketCapAdapter
import com.camo.kripto.ui.viewModel.MarketCapVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        viewModel.prefCurrency.observe(requireActivity()) {

            getNewData(it, viewModel.orderby.value, viewModel.duration.value)
            adapter.curr = it

        }
        viewModel.orderby.observe(requireActivity()) {

            getNewData(viewModel.prefCurrency.value, it, viewModel.duration.value)

        }
        viewModel.duration.observe(requireActivity()) {
            binding.tvDuration.text = it
            getNewData(viewModel.prefCurrency.value, viewModel.orderby.value, it)
        }
    }

    private fun setupVM() {
        viewModel = ViewModelProviders.of(
            requireActivity()
        ).get(MarketCapVM::class.java)
    }

    private fun setupUI() {
        binding.rvMarketCap.layoutManager = LinearLayoutManager(requireActivity())
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

//        binding.ddOrderBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                //TODO changes
//                viewModel.orderby.postValue()
//            }
//
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                viewModel.orderby.postValue(position)
//            }
//
//        }
        binding.tvDuration.setOnClickListener {
            if(viewModel.arr!=null){
                var i = viewModel.arr!!.indexOf(viewModel.duration.value)
                i = (i + 1) % 7
                viewModel.duration.postValue(viewModel.arr!![i])
            }
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
        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.order_by_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.ddOrderBy.adapter = adapter
        }

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
                    requireActivity(),
                    "\uD83D\uDE28 Wooops",
                    Toast.LENGTH_LONG
                ).show()
                Log.d(TAG, it.toString())
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
        private val TAG = FragMarket::class.simpleName
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
