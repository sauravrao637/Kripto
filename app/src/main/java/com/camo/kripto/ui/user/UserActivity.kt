package com.camo.kripto.ui.user

import android.content.SharedPreferences
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.camo.kripto.R
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.databinding.ActivityUserBinding
import com.camo.kripto.ui.FragMarket
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.viewModel.MarketCapVM
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var viewModel: MarketCapVM
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@UserActivity)

        setupVM()
        setupUI()
        setupObservers()


    }

    private fun setupVM() {
        val arr = this.resources.getStringArray(R.array.market_duration)
        viewModel = ViewModelProviders.of(
            this, VMFactory(CGApiHelper(RetrofitBuilder.CG_SERVICE))
        ).get(MarketCapVM::class.java)

        viewModel.durationArr(arr)
        //must post currency as soon as vm setup

        var curr = sharedPreferences.getString("pref_currency", "inr")
        if (curr == null) curr = "inr"
        viewModel.prefCurrency.postValue(curr)
        viewModel.duration.postValue(0)
    }

    private fun setupUI() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_frag_holder, FragMarket.getInst(FragMarket.KEY_FAV))
            commit()
        }
        binding.bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_fav -> {
                    Log.d("hi", "fav")
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragMarket.getInst(FragMarket.KEY_FAV))
                        commit()
                    }
                    true
                }
                R.id.menu_market -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragMarket.getInst(FragMarket.KEY_ALL))
                        commit()
                    }
                    true
                }
                R.id.menu_profile -> {
                    //TODO
                    true
                }

                else -> false
            }
        }
    }

    private fun setupObservers() {

    }


}