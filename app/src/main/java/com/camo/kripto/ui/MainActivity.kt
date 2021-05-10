package com.camo.kripto.ui


import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.ActionBar
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
        val arr = this.resources.getStringArray(R.array.market_duration)
        viewModel = ViewModelProviders.of(
            this, VMFactory(CGApiHelper(RetrofitBuilder.CG_SERVICE))
        ).get(MarketCapVM::class.java)

        if (viewModel.arr == null) viewModel.arr = arr
        //must post currency as soon as vm setup
        //TODO fix on rotation duration changes
        viewModel.duration.postValue(
            sharedPreferences.getString(
                "pref_per_change_dur",
                "1h"
            )
        )

        if (viewModel.prefCurrency.value == null) {
            var curr = sharedPreferences.getString("pref_currency", "inr")
            if (curr == null) curr = "inr"
            viewModel.prefCurrency.postValue(curr)
        }
        if (viewModel.orderby.value == null) viewModel.orderby.postValue(
            sharedPreferences.getString(
                "pref_order",
                "market_cap_desc"
            )
        )
    }

    private fun setupUI() {

//        setting up bottomnavigation bar

        binding?.bottomNav?.setOnNavigationItemSelectedListener {
            viewModel.currentFrag = it.itemId
            when (it.itemId) {

                R.id.menu_fav -> {
                    actionBar?.title = this.resources.getString(R.string.favourites)

                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragMarket.getInst(FragMarket.KEY_FAV))
                        commit()
                    }
                    true
                }
                R.id.menu_market -> {
                    actionBar?.title = this.resources.getString(R.string.market)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragMarket.getInst(FragMarket.KEY_ALL))
                        commit()
                    }
                    true
                }
                R.id.menu_more -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragMore())
                        commit()
                    }
                    true
                }
                R.id.menu_global -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragGlobal())
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
            } else {
                binding?.bottomNav?.selectedItemId = R.id.menu_market
            }
        } else {
            binding?.bottomNav?.selectedItemId = viewModel.currentFrag!!
        }

//        setting up trending coins rv
        trendingAdapter = TrendingAdapter()

        binding?.rvTrending?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTrending?.adapter = trendingAdapter
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

    companion object {
        private val TAG = MainActivity::class.simpleName
    }
}