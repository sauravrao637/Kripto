package com.camo.kripto.ui.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivityMainBinding
import com.camo.kripto.ui.presentation.BaseActivity
import com.camo.kripto.ui.presentation.search.SearchActivity
import com.camo.kripto.ui.presentation.settings.SettingsActivity
import com.camo.kripto.ui.viewModel.MarketCapVM
import com.camo.kripto.utils.Extras
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MarketCapVM>()
    private var actionBar: ActionBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        actionBar = this.supportActionBar
//        TODO add custom action bar
//        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
//        actionBar?.setDisplayShowCustomEnabled(true)
//        actionBar?.setCustomView(R.layout.custom_action_bar)
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        setupVM()
        setupUI()
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
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.fragMarketFav,
                R.id.fragMarkets,
                R.id.fragMore,
                R.id.fragNews
            )
        )
        binding.bottomNav.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
        if (viewModel.currentFrag == null) {
            when (sharedPreferences.getString("pref_def_frag", "1")) {
                "0" -> {
                    navController.navigate(R.id.fragMarketFav)
//                    automatically navigate to markets if no fav coins,  no one wants to see blank
//                    screen
                    lifecycleScope.launchWhenStarted {
                        withContext(Dispatchers.IO) {
                            if (viewModel.isFavCoinsEmpty()) {
                                withContext(Dispatchers.Main) {
                                    navController.navigate(R.id.fragMarkets)
                                }
                            }
                        }
                    }
                }
                "2" -> {
                    navController.navigate(R.id.fragNews)
                }
                else -> {
                    navController.navigate(R.id.fragMarkets)
                }
            }
        } else {
            binding.bottomNav.selectedItemId = viewModel.currentFrag!!
        }
        val destinationListener =
            NavController.OnDestinationChangedListener { controller, destination, bundle ->
                viewModel.currentFrag = destination.id
            }
        navController.addOnDestinationChangedListener(destinationListener)
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