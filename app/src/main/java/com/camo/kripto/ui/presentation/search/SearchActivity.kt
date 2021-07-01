package com.camo.kripto.ui.presentation.search

import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivitySearchBinding
import com.camo.kripto.error.ErrorPanelHelper
import com.camo.kripto.local.model.FavCoin
import com.camo.kripto.repos.Repository
import com.camo.kripto.ui.adapter.SearchAdapter
import com.camo.kripto.ui.adapter.TrendingAdapter
import com.camo.kripto.ui.presentation.BaseActivity
import com.camo.kripto.ui.viewModel.SearchActivityVM
import com.camo.kripto.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchAdapter
    private val viewModel: SearchActivityVM by viewModels()
    private lateinit var trendingAdapter: TrendingAdapter

    @Inject
    lateinit var repository: Repository
    var mCurrentItemPosition by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.hide()
        adapter = SearchAdapter()
        setupUI()
        setupObservers()
    }

    private fun refreshTrending() {
        viewModel.getTrending()
        Timber.d("refreshing")
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            val errorPanelHelper = ErrorPanelHelper(binding.root, ::refreshTrending)
            viewModel.trending.collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        trendingAdapter.setData(it.data?.body()?.coins)
                        binding.pbTrending.visibility = View.GONE
                        binding.rvSearchResult.visibility = View.VISIBLE
                        binding.errorPanel.root.visibility = View.GONE
                        errorPanelHelper.dispose()
                        errorPanelHelper.hide()
                    }
                    Status.ERROR -> {
                        binding.rvSearchResult.visibility = View.GONE
                        binding.pbTrending.visibility = View.GONE
                        binding.errorPanel.root.visibility = View.VISIBLE
                        errorPanelHelper.showError(it.errorInfo)
                    }
                    Status.LOADING -> {
                        binding.errorPanel.root.visibility = View.GONE
                        binding.pbTrending.visibility = View.VISIBLE
                        binding.rvSearchResult.visibility = View.GONE
                        errorPanelHelper.dispose()
                        errorPanelHelper.hide()
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.searchCoinList.collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.pbSearch.visibility = View.GONE
                        if (it.data?.isNotEmpty() == true) {
                            binding.rvSearchResult.visibility = View.VISIBLE
                            binding.tvNoneFound.visibility = View.GONE
                            adapter.setData(it.data)
                        } else {
                            binding.rvSearchResult.visibility = View.INVISIBLE
                            binding.tvNoneFound.visibility = View.VISIBLE
                        }
                    }
                    Status.ERROR -> {
                        //will never be the case
                    }
                    Status.LOADING -> {
                        binding.rvSearchResult.visibility = View.INVISIBLE
                        binding.pbSearch.visibility = View.VISIBLE
                        binding.tvNoneFound.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupUI() {
        binding.rvSearchResult.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResult.adapter = adapter
        val queryListener = object: SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchStringChanged(newText)
                Timber.d(newText)
                return false
            }
        }
        binding.coinSearch.setOnQueryTextListener(queryListener)
        registerForContextMenu(binding.rvSearchResult)
        adapter.setOnLongItemClickListener(object : SearchAdapter.OnLongItemClickListener {
            override fun itemLongClicked(v: View?, position: Int) {
                mCurrentItemPosition = position
                val coin = adapter.getItem(mCurrentItemPosition)
                if (coin != null) {
                    v?.showContextMenu()
                    lifecycleScope.launchWhenStarted {
                        withContext(Dispatchers.IO){
                            val c = viewModel.getCount(coin.id)
                            if(c!=0){
                                withContext(Dispatchers.Main){
                                    menu?.findItem(R.id.action_fav)?.title =
                                        getString(R.string.remove_favourite)
                                }
                            }
                        }
                    }
                }
            }
        })
        trendingAdapter = TrendingAdapter()
        binding.rvTrending.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrending.adapter = trendingAdapter
    }

    var menu: ContextMenu? = null
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenuInfo?) {
        val inflater = menuInflater
        inflater.inflate(R.menu.searched_crypto_longclick_menu, menu)
        this.menu = menu
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val coin = adapter.getItem(mCurrentItemPosition) ?: return false
        when (item.itemId) {
            R.id.action_fav -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (viewModel.getCount(coin.id)!=0) {
                        repository.removeFavCoin(coin.id)
                        withContext(Dispatchers.Main) {
                            adapter.notifyItemChanged(mCurrentItemPosition)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            adapter.notifyItemChanged(mCurrentItemPosition)
                        }
                        repository.addFavCoin(FavCoin(coin.id, coin.name))
                    }
                }
            }
        }
        return true
    }
}
