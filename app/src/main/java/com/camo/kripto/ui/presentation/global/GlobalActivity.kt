package com.camo.kripto.ui.presentation.global

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivityGlobalBinding
import com.camo.kripto.ktx.enforceSingleScrollDirection
import com.camo.kripto.ktx.recyclerView
import com.camo.kripto.ui.adapter.tab.GlobalActivityTabAdapter
import com.camo.kripto.ui.presentation.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GlobalActivity : BaseActivity() {

    private lateinit var binding: ActivityGlobalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlobalBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupUI()
    }

    private fun setupUI() {
        val adapter = GlobalActivityTabAdapter(this)
        binding.viewPagerGlobalActivity.adapter = adapter
        binding.viewPagerGlobalActivity.recyclerView.enforceSingleScrollDirection()
        TabLayoutMediator(
            binding.tabLayoutGlobalActivity,
            binding.viewPagerGlobalActivity
        ) { tab, position ->
            when (position) {
                0 -> tab.text = this.getString(R.string.Cryptocurrencies)
                1 -> tab.text = this.getString(R.string.defi)
            }
        }.attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}