package com.camo.kripto.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.camo.kripto.R
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.databinding.ActivityCoinBinding
import com.camo.kripto.ui.adapter.CoinActivityTabAdapter
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.preferences.SettingsActivity
import com.camo.kripto.ui.viewModel.CoinActivityVM
import com.camo.kripto.utils.Status
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class CoinActivity : AppCompatActivity() {
    private val TAG = CoinActivity::class.simpleName
    private lateinit var binding: ActivityCoinBinding
    private lateinit var viewModel: CoinActivityVM
    private var id : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

//        actionBar?.setDisplayShowCustomEnabled(true)
//        val inflater =
//            getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val v: View = CustomActionBarBinding.inflate(inflater).root
//        actionBar?.customView = v

        id = intent.getStringExtra("coinId")
        val curr = intent.getStringExtra("curr")


        viewModel = ViewModelProviders.of(
            this,
            VMFactory(CGApiHelper(RetrofitBuilder.CG_SERVICE))
        ).get(CoinActivityVM::class.java)

        viewModel.currency.postValue(curr)
        setCurrencies()


        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = CoinActivityTabAdapter(
            this
        )
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Price Chart"
                1 -> tab.text = "Info"
                2 -> tab.text = "NBA"
            }
        }.attach()

        getNewData(id)
        setCurrencies()

        viewModel.title.observe(this, {
            if (it != null) supportActionBar?.title = it
        })
    }

    private var getCurrJob: Job? = null
    private fun setCurrencies() {
        getCurrJob?.cancel()
        capDataJob = lifecycleScope.launch {
            viewModel.getSupportedCurr().collect {
                it.let { result ->
                    when (result.status) {
                        Status.SUCCESS -> {
                            binding.viewPager.visibility = View.VISIBLE
                            binding.pbCoinLoad.visibility = View.GONE
                            result.data?.let { CD ->
                                viewModel.allCurr.postValue(CD)
                            }
                        }
                        Status.ERROR -> {
                            binding.viewPager.visibility = View.GONE
                            binding.pbCoinLoad.visibility = View.GONE
                            Toast.makeText(
                                this@CoinActivity,
                                "\uD83D\uDE28 Wooops" + it.message,
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d(TAG, "error")
                        }
                        Status.LOADING -> {
                            binding.viewPager.visibility = View.GONE
                            binding.pbCoinLoad.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private var capDataJob: Job? = null
    private fun getNewData(id: String?) {
        capDataJob?.cancel()
        Log.d(TAG, "launching capDataJob")
        capDataJob = lifecycleScope.launch {
            viewModel.getCurrentData(id ?: "bitcoin").collect {
                it.let { result ->
                    viewModel.currentCoinData.postValue(result)
                }
            }
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
            val intent = Intent(this@CoinActivity, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        R.id.action_refresh -> {
//            TODO(lazy) improve
            getNewData(id)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
