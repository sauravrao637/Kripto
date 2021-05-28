package com.camo.kripto.ui.presentation.global

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivityGlobalBinding
import com.camo.kripto.ui.adapter.GlobalActivityTabAdapter
import com.camo.kripto.ui.presentation.BaseActivity
import com.camo.kripto.ui.viewModel.GlobalVM
import com.camo.kripto.utils.ThemeUtil
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GlobalActivity : BaseActivity() {

    private lateinit var binding: ActivityGlobalBinding
    private val viewModel by viewModels<GlobalVM>()
//    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlobalBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@GlobalActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupVM()
        setupUI()
        setupObservers()
    }

    private fun setupVM() {
        val curr = sharedPreferences.getString("pref_currency", "inr") ?: "inr"
        if (!viewModel.initialized) viewModel.setValues(curr)
    }

    private fun setupUI() {
        val adapter = GlobalActivityTabAdapter(
            this
        )
        binding.viewPagerGlobalActivity.adapter = adapter

        TabLayoutMediator(
            binding.tabLayoutGlobalActivity,
            binding.viewPagerGlobalActivity
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "Crypto"
                1 -> tab.text = "Defi"
            }
        }.attach()

        binding.root.setOnRefreshListener {
            viewModel.refreshed.postValue(true)
            binding.root.isRefreshing = false
        }
    }

    private fun setupObservers() {
        viewModel.title.observe(this, {
            if (it != null) supportActionBar?.title = it
        })
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