package com.camo.kripto.ui.presentation.search

import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivitySearchBinding
import com.camo.kripto.local.model.FavCoin
import com.camo.kripto.repos.Repository
import com.camo.kripto.ui.adapter.SearchAdapter
import com.camo.kripto.ui.presentation.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates


@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchAdapter

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

    }

    private fun setupUI() {
        binding.rvSearchResult.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResult.adapter = adapter
        binding.coinSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchStringChanged(newText)
                Timber.d(newText)
                return false
            }
        })
        registerForContextMenu(binding.rvSearchResult)
        adapter.setOnLongItemClickListener(object : SearchAdapter.OnLongItemClickListener {
            override fun itemLongClicked(v: View?, position: Int) {
                mCurrentItemPosition = position
                val coin = adapter.getItem(mCurrentItemPosition)
                if (coin != null) {
                    v?.showContextMenu()
                    //TODO set menu header
                    if (coin.isFav) {
                        menu?.findItem(R.id.action_fav)?.title = getString(R.string.remove_favourite)
                    }
                }
            }
        })
    }

    var filterJob: Job? = null
    private fun searchStringChanged(newText: String?) {
        filterJob?.cancel()
        filterJob = lifecycleScope.launchWhenStarted {
            delay(100)
            withContext(Dispatchers.IO) {
                val list = repository.getCoinFilterByName(newText ?: "")
//                list.sortedBy { it.isFav }
                withContext(Dispatchers.Main) {
                    adapter.setData(list)
                }
            }
        }
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
                    if (coin.isFav) {
                        repository.removeFavCoin(coin.coin.id)
                        withContext(Dispatchers.Main) {
                            coin.isFav = false
                            adapter.notifyItemChanged(mCurrentItemPosition)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            coin.isFav = true
                            adapter.notifyItemChanged(mCurrentItemPosition)
                        }
                        repository.addFavCoin(FavCoin(coin.coin.id, coin.coin.name))
                    }
                }
            }
        }

        return true
    }

}