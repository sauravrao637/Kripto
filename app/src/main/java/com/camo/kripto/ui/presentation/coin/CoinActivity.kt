package com.camo.kripto.ui.presentation.coin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.camo.kripto.R
import com.camo.kripto.local.model.FavCoin
import com.camo.kripto.databinding.ActivityCoinBinding
import com.camo.kripto.ui.presentation.settings.SettingsActivity
import com.camo.kripto.ui.adapter.CoinActivityTabAdapter
import com.camo.kripto.ui.viewModel.CoinActivityVM
import com.camo.kripto.utils.Status
import com.camo.kripto.utils.ThemeUtil
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoinBinding
    private val viewModel by viewModels<CoinActivityVM>()
    private var id: String? = null
    private var toast: Toast? = null
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val theme: Int = ThemeUtil.getThemeId(sharedPreferences.getString("pref_theme", ThemeUtil.THEME_RED))
        setTheme(theme)
        binding = ActivityCoinBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        id = intent.getStringExtra("coinId")
        val curr = intent.getStringExtra("curr")

        setupVM(curr)
        setupUI()

        getNewData(id)
        setCurrencies()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.title.observe(this, {
            if (it != null) supportActionBar?.title = it
        })
    }

    private fun setupUI() {
        val adapter = CoinActivityTabAdapter(
            this
        )
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Price Chart"
                1 -> tab.text = "Info"
            }
        }.attach()
    }

    private fun setupVM(curr: String?) {
        if(!viewModel.initialized) {
            viewModel.setValues(curr)
        }
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
                            binding.pb.visibility = View.GONE
                            result.data?.let { CD ->
                                viewModel.allCurr.postValue(CD)
                            }
                        }
                        Status.ERROR -> {
                            binding.viewPager.visibility = View.INVISIBLE
                            binding.pb.visibility = View.GONE
                            Toast.makeText(
                                this@CoinActivity,
                                "\uD83D\uDE28 Wooops" + it.message,
                                Toast.LENGTH_LONG
                            ).show()
                            Timber.d(
                                "error"
                            )
                        }
                        Status.LOADING -> {
                            binding.pb.visibility = View.VISIBLE
                            binding.viewPager.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    private var capDataJob: Job? = null
    private fun getNewData(id: String?) {
        capDataJob?.cancel()
        Timber.d("launching capDataJob")
        capDataJob = lifecycleScope.launch {
            viewModel.getCurrentData(id ?: "bitcoin").collect {
                it.let { result ->
                    when (result.status) {
                        Status.LOADING -> {
                            Timber.d("coin loading")
                            binding.pb.visibility = View.VISIBLE
                            binding.viewPager.visibility = View.INVISIBLE
                            binding.tabLayout.visibility = View.INVISIBLE
                            binding.tvErrorMsg.visibility = View.INVISIBLE
                        }
                        Status.ERROR -> {
                            Timber.d("coin error")
                            binding.pb.visibility = View.INVISIBLE
                            binding.tvErrorMsg.visibility = View.VISIBLE
                        }

                        Status.SUCCESS -> {
                            Timber.d("coin success")
                            binding.pb.visibility = View.GONE
                            binding.viewPager.visibility = View.VISIBLE
                            binding.tabLayout.visibility = View.VISIBLE
                            binding.tvErrorMsg.visibility = View.INVISIBLE
                            viewModel.currentCoinData.postValue(result.data)
                            if (it.data?.id?.let { it1 ->
                                    withContext(Dispatchers.IO) {
                                        viewModel.getCount(
                                            it1
                                        )
                                    }
                                } != 0) setFavStatus(true)
                        }
                    }

                }
            }
        }
    }

    private fun setFavStatus(boolean: Boolean, show: Boolean = false) {
        if (menu == null) return
        var icon = R.drawable.ic_star_solid
        var msg = "Added"
        if (!boolean) {
            icon = R.drawable.ic_star
            msg = "Removed"
        }
        if (show) {
            toast?.cancel()
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
            toast?.show()
        }
        this.menu?.findItem(R.id.action_fav)?.setIcon(icon)
    }

    private var menu: Menu? = null


    private var toggleFavJob: Job? = null
    private fun toggleFav(id: String, name: String) {
        toggleFavJob?.cancel()
        toggleFavJob = lifecycleScope.launch {
            if (withContext(Dispatchers.IO) { viewModel.getCount(id) } == 0) {
                withContext(Dispatchers.IO) { viewModel.addFavCoin(FavCoin(id, name)) }
                setFavStatus(boolean = true, show = true)
            } else {
                withContext(Dispatchers.IO) { viewModel.removeFavCoin(id) }
                setFavStatus(boolean = false, show = true)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.coin_menu, menu)
        this.menu = menu
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
            getNewData(id)
            true
        }
        R.id.action_fav -> {
            val coinCd = viewModel.currentCoinData.value
            if (coinCd != null) toggleFav(coinCd.id, coinCd.name)
            true
        }
        android.R.id.home -> {
            this.finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
