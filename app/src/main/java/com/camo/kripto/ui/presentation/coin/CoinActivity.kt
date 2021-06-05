package com.camo.kripto.ui.presentation.coin

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivityCoinBinding
import com.camo.kripto.error.ErrorPanelHelper
import com.camo.kripto.ui.adapter.CoinActivityTabAdapter
import com.camo.kripto.ui.presentation.BaseActivity
import com.camo.kripto.ui.presentation.settings.SettingsActivity
import com.camo.kripto.ui.viewModel.CoinActivityVM
import com.camo.kripto.utils.Status
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class CoinActivity : BaseActivity() {
    companion object{
        const val COIN_ID_KEY = "coinID"
        const val COIN_NAME_KEY = "coinName"
    }
    private lateinit var binding: ActivityCoinBinding
    private val viewModel by viewModels<CoinActivityVM>()
    private var id: String? = null
    private var name: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (id == null) {
            id = intent.getStringExtra(COIN_ID_KEY)
            name = intent.getStringExtra(COIN_NAME_KEY)
            viewModel.setID(id)
            viewModel.getCryptoCapData()
        }
        setupUI()
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.coinData.collect { coinData ->
                val errorPanel = ErrorPanelHelper(binding.root, ::refresh)
                when (coinData.status) {
                    Status.SUCCESS -> {
                        binding.groupActivityCoin.visibility = View.VISIBLE
                        binding.pb.visibility = View.GONE
                        binding.errorPanel.root.visibility = View.GONE
                        errorPanel.hide()
                        errorPanel.dispose()
                    }
                    Status.ERROR -> {
                        errorPanel.showError(coinData.errorInfo)
                        binding.errorPanel.root.visibility = View.VISIBLE
                        binding.pb.visibility = View.GONE
                        binding.groupActivityCoin.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        binding.groupActivityCoin.visibility = View.GONE
                        binding.pb.visibility = View.VISIBLE
                        binding.errorPanel.root.visibility = View.GONE
                        errorPanel.hide()
                        errorPanel.dispose()
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isFav.collect { isFav ->
                val item = menu?.findItem(R.id.action_fav)
                when (isFav) {
                    null -> {
                        item?.isVisible = false
                    }
                    true -> {
                        item?.isVisible = true
                        item?.setIcon(R.drawable.ic_star_solid)
                        item?.setTitle(R.string.add_to_fav)
                    }
                    false -> {
                        item?.isVisible = true
                        item?.setIcon(R.drawable.ic_star)
                        item?.setTitle(R.string.remove_favourite)
                    }
                }
            }
        }
    }

    private fun refresh() {
        viewModel.getCryptoCapData()
    }

    private fun setupUI() {
        val adapter = CoinActivityTabAdapter(
            this
        )
        binding.viewPager.adapter = adapter
        supportActionBar?.title = id?.toUpperCase(Locale.getDefault())
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Price Chart"
                1 -> tab.text = "Info"
            }
        }.attach()
    }

    private var menu: Menu? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.coin_menu, menu)
        this.menu = menu
        if(viewModel.isFav.value == true) {
            menu?.findItem(R.id.action_fav)?.setIcon(R.drawable.ic_star_solid)
        }
        else {
            menu?.findItem(R.id.action_fav)?.setIcon(R.drawable.ic_star)
        }
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
            refresh()
            true
        }
        R.id.action_fav -> {
            if (id != null && name != null) viewModel.toggleFav(id!!, name!!)
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
