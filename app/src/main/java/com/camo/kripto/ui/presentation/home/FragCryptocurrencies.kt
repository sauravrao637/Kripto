package com.camo.kripto.ui.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.camo.kripto.BuildConfig
import com.camo.kripto.R
import com.camo.kripto.databinding.FragCryptocurrenciesBinding
import com.camo.kripto.local.model.CoinIdName
import com.camo.kripto.repos.Repository
import com.camo.kripto.ui.adapter.CMCLoadStateAdapter
import com.camo.kripto.ui.adapter.CryptocurrenciesMarketCapAdapter
import com.camo.kripto.ui.presentation.coin.CoinActivity
import com.camo.kripto.ui.presentation.coin.CoinIdNameKeys
import com.camo.kripto.ui.viewModel.MarketCapVM
import com.camo.kripto.utils.SwipeToDeleteCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

private const val FRAG_KEY = "key"

@AndroidEntryPoint
class FragCryptocurrencies : Fragment(), CryptocurrenciesMarketCapAdapter.OnCryptocurrencyListener {

    private lateinit var adapterCryptocurrencies: CryptocurrenciesMarketCapAdapter
    private lateinit var binding: FragCryptocurrenciesBinding
    private val viewModel by activityViewModels<MarketCapVM>()
    private var key: String? = null

    @Inject
    lateinit var repo: Repository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        key = arguments?.getString(FRAG_KEY) ?: KEY_ALL
        binding = FragCryptocurrenciesBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.prefCurrency.observe(viewLifecycleOwner) {
            getNewData(it, viewModel.orderby.value, viewModel.duration.value)
            adapterCryptocurrencies.curr = it
        }
        viewModel.orderby.observe(viewLifecycleOwner) {
            getNewData(viewModel.prefCurrency.value, it, viewModel.duration.value)
        }
        viewModel.duration.observe(viewLifecycleOwner) {
            binding.tvDuration.text = it
            getNewData(viewModel.prefCurrency.value, viewModel.orderby.value, it)
        }
    }

    private fun setupUI() {
        binding.rvMarketCap.layoutManager = LinearLayoutManager(context)
        adapterCryptocurrencies =
            CryptocurrenciesMarketCapAdapter(
                this,
                viewModel.prefCurrency.value ?: "inr",
                CryptocurrenciesMarketCapAdapter.Comparator
            )
        if (BuildConfig.DEBUG && key == KEY_FAV) {
            val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    viewModel.unFav(adapterCryptocurrencies.getCoin(viewHolder.absoluteAdapterPosition))
                    //TODO optimize this
                    refresh()
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(binding.rvMarketCap)
        }
        binding.root.setOnRefreshListener {
            refresh()
        }
        binding.retryButton.setOnClickListener { adapterCryptocurrencies.retry() }
        initAdapters()
        binding.rvMarketCap.adapter = adapterCryptocurrencies.withLoadStateFooter(
            footer = CMCLoadStateAdapter { adapterCryptocurrencies.retry() })
        binding.tvDuration.setOnClickListener {
            viewModel.toggleDuration()
        }
        if (key == KEY_FAV) {
            binding.emptyList.text = this.getString(R.string.empty_favourites_msg)
            binding.emptyList.setOnClickListener {
                findNavController().navigate(R.id.fragMarkets)
            }
        }
    }

    private fun refresh() {
        getNewData(viewModel.prefCurrency.value, viewModel.orderby.value, viewModel.duration.value)
        adapterCryptocurrencies.notifyDataSetChanged()
        binding.root.isRefreshing = false
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
        adapterCryptocurrencies.addLoadStateListener { loadState ->
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && adapterCryptocurrencies.itemCount == 0
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

    private var capDataJob: Job? = null
    private fun getNewData(it: String?, order: String?, dur: String?) {
        capDataJob?.cancel()
        var coins: List<CoinIdName>? = null
        capDataJob = lifecycleScope.launch {
            if (key == KEY_FAV) {
                coins = withContext(Dispatchers.IO) { repo.getFavCoins() }
            }
            viewModel.getMarketCap(it, order, dur, coins).collectLatest { pagingData ->
                adapterCryptocurrencies.submitData(pagingData)
            }
        }
    }

    companion object {
        const val KEY_ALL = "all"
        const val KEY_FAV = "fav"

        @JvmStatic
        fun newInstance(key: String) =
            FragCryptocurrencies().apply {
                arguments = Bundle().apply {
                    putString(FRAG_KEY, key)
                }
            }
    }

    override fun onCryptocurrencyClicked(position: Int) {
        val cryptocurrency = adapterCryptocurrencies.getCoin(position) ?: return
        val intent = Intent(requireContext(),CoinActivity::class.java)
        intent.putExtra(CoinIdNameKeys.COIN_ID_KEY, cryptocurrency.id)
        intent.putExtra(CoinIdNameKeys.COIN_NAME_KEY, cryptocurrency.name)
        requireContext().startActivity(intent)
    }
}
