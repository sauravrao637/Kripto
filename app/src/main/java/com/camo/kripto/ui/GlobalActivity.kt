package com.camo.kripto.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.data.repository.CGRepo
import com.camo.kripto.databinding.ActivityGlobalBinding
import com.camo.kripto.ui.adapter.CoinActivityTabAdapter
import com.camo.kripto.ui.adapter.GlobalActivityTabAdapter
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.viewModel.GlobalVM
import com.google.android.material.tabs.TabLayoutMediator

class GlobalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGlobalBinding
    private lateinit var viewModel: GlobalVM
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlobalBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@GlobalActivity)

        setupVM()
        setupUI()
        setupObservers()
    }

    private fun setupVM() {
        val curr = sharedPreferences.getString("pref_currency", "inr")?:"inr"
        viewModel = ViewModelProviders.of(
            this, VMFactory(
                cgApiHelper = CGApiHelper(RetrofitBuilder.CG_SERVICE),
                curr = curr
            )
        ).get(GlobalVM::class.java)
    }

    private fun setupUI() {
        val adapter = GlobalActivityTabAdapter(
            this
        )
        binding.viewPagerGlobalActivity.adapter = adapter

        TabLayoutMediator(binding.tabLayoutGlobalActivity, binding.viewPagerGlobalActivity) { tab, position ->
            when (position) {
                0 -> tab.text = "Crypto"
                1 -> tab.text = "Defi"
            }
        }.attach()
    }

    private fun setupObservers() {
        viewModel.title.observe(this, {
            if (it != null) supportActionBar?.title = it
        })
    }
}