package com.camo.kripto.ui.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivityMainBinding
import com.camo.kripto.repos.Repository
import com.camo.kripto.ui.adapter.TrendingAdapter
import com.camo.kripto.ui.presentation.BaseActivity
import com.camo.kripto.ui.presentation.search.SearchActivity
import com.camo.kripto.ui.presentation.settings.SettingsActivity
import com.camo.kripto.ui.viewModel.MarketCapVM
import com.camo.kripto.utils.Extras
import com.camo.kripto.utils.Status
import com.camo.kripto.works.SyncLocalWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private var binding: ActivityMainBinding? = null
    private val viewModel by viewModels<MarketCapVM>()
    private var actionBar: ActionBar? = null
    private var trendingAdapter: TrendingAdapter? = null

    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding?.root)
        actionBar = this.supportActionBar
//        TODO add custom action bar
//        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
//        actionBar?.setDisplayShowCustomEnabled(true)
//        actionBar?.setCustomView(R.layout.custom_action_bar)
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        incLaunchCount()
        shouldSync()
        setupVM()
        setupUI()
        setupObservers()
    }

    private fun shouldSync() {
        lifecycleScope.launch(Dispatchers.IO)
        {
            if (repository.getCurrCount() == 0) {
                try {
                    if (repository.pingCG().code() == 200) {
                        setupForFirstTime()
                    } else {
                        //TODO show error nicely
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "Server Down :(, Try Again Later",
                                Toast.LENGTH_LONG
                            ).show()
                            delay(2000)
                            this@MainActivity.finish()
                        }
                    }
                } catch (e: Exception) {
                    //TODO show error nicely
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Couldn't reach server :(, Try Again Later",
                            Toast.LENGTH_LONG
                        ).show()
                        delay(2000)
                        this@MainActivity.finish()
                    }
                }
            }
        }
    }

    private var firstTimeJob: Job? = null
    private fun setupForFirstTime() {
        if (firstTimeJob != null) {
            firstTimeJob?.cancel()
            Timber.d("not null")
        }
        firstTimeJob = GlobalScope.launch(Dispatchers.IO) {
            val syncWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<SyncLocalWorker>()
                    .build()
            WorkManager
                .getInstance(this@MainActivity)
                .enqueue(syncWorkRequest)
        }
    }

    private fun incLaunchCount() {
        var c: Int = sharedPreferences.getInt("numRun", 0)
        c++
        sharedPreferences.edit().putInt("numRun", c).apply()
    }

    override fun onPause() {
        super.onPause()
        decLaunchCount()
    }

    private fun decLaunchCount() {
        if (!isFinishing) {
            var c = sharedPreferences.getInt("numRun", 0)
            c--
            sharedPreferences.edit().putInt("numRun", c).apply()
        }
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
                    binding?.pbTrending?.visibility = View.INVISIBLE
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
        if (!viewModel.intialized) {
            val curr = sharedPreferences.getString("pref_currency", "inr") ?: "inr"
            val prefDur = sharedPreferences.getString("pref_per_change_dur", "1h") ?: "1h"
            val prefOrder = sharedPreferences.getString(
                "pref_order",
                "market_cap_desc"
            ) ?: "market_cap_desc"
            val arr = this.resources.getStringArray(R.array.market_duration)
            viewModel.arr = arr
            viewModel.setValues(curr, prefDur, prefOrder)
            viewModel.intialized = true
        }
    }

    private fun setupUI() {
        if (binding != null) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            val appBarConfiguration = AppBarConfiguration(
                topLevelDestinationIds = setOf(
                    R.id.fragMarketFav,
                    R.id.fragMarkets,
                    R.id.fragMore
                )
            )
            binding!!.bottomNav.setupWithNavController(navController)
            setupActionBarWithNavController(navController, appBarConfiguration)
            if (viewModel.currentFrag == null) {
                when (sharedPreferences.getString("pref_def_frag", "1")) {
                    "0" -> {
                        navController.navigate(R.id.fragMarketFav)
                    }
                    "1" -> {
                        navController.navigate(R.id.fragMarkets)
                    }
                }
            } else {
                binding!!.bottomNav.selectedItemId = viewModel.currentFrag!!
            }
            val destinationListener =
                NavController.OnDestinationChangedListener { controller, destination, bundle ->
                    viewModel.currentFrag = destination.id
                }
            navController.addOnDestinationChangedListener(destinationListener)
        }
//        set view to default//curr fragment

/*
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
*/
    }

    private var getTrendingJob: Job? = null
    private fun getTrending() {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // User chose the "Settings" item, show the app settings UI...
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_share -> {
                Extras.share(this)
                true
            }
            R.id.search -> {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}