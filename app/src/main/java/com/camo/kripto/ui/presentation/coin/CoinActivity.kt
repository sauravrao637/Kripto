package com.camo.kripto.ui.presentation.coin

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivityCoinBinding
import com.camo.kripto.error.ErrorCause
import com.camo.kripto.error.ErrorInfo
import com.camo.kripto.error.ErrorPanelHelper
import com.camo.kripto.ktx.enforceSingleScrollDirection
import com.camo.kripto.ktx.recyclerView
import com.camo.kripto.ui.adapter.tab.CoinActivityTabAdapter
import com.camo.kripto.ui.presentation.BaseActivity
import com.camo.kripto.ui.presentation.coin.CoinIdNameKeys.COIN_ID_KEY
import com.camo.kripto.ui.presentation.coin.CoinIdNameKeys.COIN_NAME_KEY
import com.camo.kripto.ui.presentation.settings.SettingsActivity
import com.camo.kripto.ui.viewModel.CoinActivityVM
import com.camo.kripto.utils.Status
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
class CoinActivity : BaseActivity() {

    private lateinit var binding: ActivityCoinBinding
    private val viewModel by viewModels<CoinActivityVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val id = intent.getStringExtra(COIN_ID_KEY)
        val name = intent.getStringExtra(COIN_NAME_KEY)
        if(id!=null && name!=null)viewModel.setIdName(id, name)
        else{
            val errorPanelHelper = ErrorPanelHelper(binding.root, ::doNothing)
            errorPanelHelper.showError(ErrorInfo(null, ErrorCause.EMPTY_COIN_ID_NAME))
            Timber.d("%s %s", id.toString(), name.toString())
        }
        setupUI()
        setupObservers()
    }
    private fun doNothing(){

    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.coinData.collectLatest { coinData ->
                val errorPanel = ErrorPanelHelper(binding.root, ::refresh)
                when (coinData.status) {
                    Status.SUCCESS -> {
                        val imageUrl = coinData.data?.image?.large
                        if (imageUrl != null) {
                            Glide.with(this@CoinActivity).asDrawable().load(imageUrl)
                                .into(object : CustomTarget<Drawable?>(56,56) {
                                    override fun onResourceReady(
                                        resource: Drawable,
                                        transition: Transition<in Drawable?>?
                                    ) {
                                        supportActionBar?.setLogo(resource)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                        }
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
            viewModel.isFav.collectLatest { isFav ->
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
        supportActionBar?.title = viewModel.getId().toUpperCase(Locale.ROOT)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        val adapter = CoinActivityTabAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Price Chart"
                1 -> tab.text = "Info"
                2 -> tab.text = "Alerts"
            }
        }.attach()
        binding.viewPager.recyclerView.enforceSingleScrollDirection()
    }

    private var menu: Menu? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.coin_menu, menu)
        this.menu = menu
        if (viewModel.isFav.value == true) {
            menu?.findItem(R.id.action_fav)?.setIcon(R.drawable.ic_star_solid)
        } else {
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
            viewModel.toggleFav()
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
