package com.camo.kripto.ui


import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.camo.kripto.R
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.databinding.ActivityMainBinding
import com.camo.kripto.ui.FragMarket
import com.camo.kripto.ui.FragMore
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.viewModel.MarketCapVM

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var viewModel: MarketCapVM
    private lateinit var sharedPreferences: SharedPreferences
    private var actionBar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding?.root)
        actionBar = this.supportActionBar
//        TODO add custom action bar
//        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
//        actionBar?.setDisplayShowCustomEnabled(true)
//        actionBar?.setCustomView(R.layout.custom_action_bar)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)

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
        viewModel.duration.postValue(sharedPreferences.getString("pref_per_change_dur","1h"))
        viewModel.prefCurrency.postValue(curr)
        viewModel.orderby.postValue(sharedPreferences.getString("pref_order", "market_cap_desc"))
    }

    private fun setupUI() {


        binding?.bottomNav?.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_fav -> {
                    actionBar?.title = this.resources.getString(R.string.favourites)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragMarket.getInst(FragMarket.KEY_FAV))
                        commit()
                    }
                    true
                }
                R.id.menu_market -> {
                    actionBar?.title = this.resources.getString(R.string.market)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragMarket.getInst(FragMarket.KEY_ALL))
                        commit()
                    }
                    true
                }
                R.id.menu_more -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_frag_holder, FragMore())
                        commit()
                    }
                    true
                }

                else -> false
            }
        }

        if (sharedPreferences.getString("pref_def_frag", "0").equals("0")) {
            binding?.bottomNav?.selectedItemId = R.id.menu_fav
        } else {
            binding?.bottomNav?.selectedItemId = R.id.menu_market
        }
    }

    private fun setupObservers() {

    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    companion object {
        private val TAG = MainActivity::class.simpleName
    }
}