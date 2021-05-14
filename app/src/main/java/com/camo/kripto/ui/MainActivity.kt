package com.camo.kripto.ui


import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.R
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.databinding.ActivityMainBinding
import com.camo.kripto.ui.adapter.TrendingAdapter
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.viewModel.MarketCapVM
import com.camo.kripto.utils.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    private var binding: ActivityMainBinding? = null
    private lateinit var viewModel: MarketCapVM
    private lateinit var sharedPreferences: SharedPreferences
    private var actionBar: ActionBar? = null
    private var trendingAdapter: TrendingAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding?.root)
        actionBar = this.supportActionBar
//        TODO add custom action bar
//        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
//        actionBar?.setDisplayShowCustomEnabled(true)
//        actionBar?.setCustomView(R.layout.custom_action_bar)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)

        setupVM()
        setupUI()
        setupObservers()


    }

    private fun setupObservers() {
        viewModel.trending.observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding?.rvTrending?.visibility = View.VISIBLE
                    binding?.pbTrending?.visibility = View.GONE
                    binding?.btnRefreshTrending?.visibility = View.GONE
                    if (it.data != null) {
//                TODO get marketCap data for trending
                        trendingAdapter?.list = it.data.coins
                    }
                }

                Status.LOADING -> {
                    binding?.rvTrending?.visibility = View.INVISIBLE
                    binding?.pbTrending?.visibility = View.VISIBLE
                }

                Status.ERROR -> {
                    binding?.rvTrending?.visibility = View.INVISIBLE
                    binding?.pbTrending?.visibility = View.INVISIBLE
                    binding?.btnRefreshTrending?.visibility = View.VISIBLE

                }
            }

        })
    }

    private fun setupVM() {

        val curr = sharedPreferences.getString("pref_currency", "inr") ?: "inr"
        val prefDur = sharedPreferences.getString("pref_per_change_dur", "1h") ?: "1h"
        val prefOrder = sharedPreferences.getString(
            "pref_order",
            "market_cap_desc"
        ) ?: "market_cap_desc"
        viewModel = ViewModelProviders.of(
            this,
            VMFactory(
                CGApiHelper(RetrofitBuilder.CG_SERVICE),
                curr = curr,
                duration = prefDur,
                prefOrder = prefOrder
            )
        ).get(MarketCapVM::class.java)

        if (viewModel.arr == null) {
            val arr = this.resources.getStringArray(R.array.market_duration)
            viewModel.arr = arr
        }

    }

    private fun setupUI() {

//        setting up bottomnavigation bar

        binding?.bottomNav?.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.menu_fav -> {
                    viewModel.currentFrag = it.itemId
                    actionBar?.title = resources.getString(R.string.favourites)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragMarket.getInst(FragMarket.KEY_FAV))
                        commit()
                    }
                    true
                }
                R.id.menu_cryptocurrencies -> {
                    viewModel.currentFrag = it.itemId
                    actionBar?.title = resources.getString(R.string.Cryptocurrencies)

                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragMarket.getInst(FragMarket.KEY_ALL))
                        commit()
                    }
                    true
                }
                R.id.menu_more -> {
                    viewModel.currentFrag = it.itemId
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragMore())
                        commit()
                    }
                    true
                }
                R.id.menu_exchanges -> {
                    viewModel.currentFrag = it.itemId
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragExchanges())
                        commit()
                    }
                    true
                }
                else -> false
            }
        }
//        set view to default//curr fragment
        if (viewModel.currentFrag == null) {
            if (sharedPreferences.getString("pref_def_frag", "0").equals("0")) {
                binding?.bottomNav?.selectedItemId = R.id.menu_fav
            } else if (sharedPreferences.getString("pref_def_frag", "0").equals("1")) {
                binding?.bottomNav?.selectedItemId = R.id.menu_cryptocurrencies
            } else if (sharedPreferences.getString("pref_def_frag", "0").equals("2")) {
                binding?.bottomNav?.selectedItemId = R.id.menu_exchanges
            }
        } else {
            binding?.bottomNav?.selectedItemId = viewModel.currentFrag!!
        }

//        setting up trending coins rv
        trendingAdapter = TrendingAdapter()

        binding?.rvTrending?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTrending?.adapter = trendingAdapter
        // add the decoration. done.
        // add the decoration. done.
        trendingAdapter?.curr = viewModel.prefCurrency.value ?: "inr"
        getTrending()

        binding?.btnRefreshTrending?.setOnClickListener {
            getTrending()
        }

    }

    var getTrendingJob: Job? = null
    fun getTrending() {
        getTrendingJob?.cancel()
        getTrendingJob = lifecycleScope.launch {
            viewModel.getTrending().collect {
                viewModel.trending.postValue(it)
            }
        }
    }


    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }


}