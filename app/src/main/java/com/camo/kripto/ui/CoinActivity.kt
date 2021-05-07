package com.camo.kripto.ui

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
import com.camo.kripto.database.AppDb
import com.camo.kripto.database.model.FavCoin
import com.camo.kripto.database.repository.AppDbRepo
import com.camo.kripto.databinding.ActivityCoinBinding
import com.camo.kripto.ui.adapter.CoinActivityTabAdapter
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.preferences.SettingsActivity
import com.camo.kripto.ui.viewModel.CoinActivityVM
import com.camo.kripto.utils.Status
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


class CoinActivity : AppCompatActivity() {
    private val TAG = CoinActivity::class.simpleName
    private lateinit var binding: ActivityCoinBinding
    private lateinit var viewModel: CoinActivityVM
    private var id: String? = null
    private var repo : AppDbRepo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        repo = AppDb.getAppDb(this)?.let { AppDbRepo(it) }
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

                            result.data?.let { CD ->
                                viewModel.allCurr.postValue(CD)
                            }
                        }
                        Status.ERROR -> {
                            binding.viewPager.visibility = View.GONE
                            Toast.makeText(
                                this@CoinActivity,
                                "\uD83D\uDE28 Wooops" + it.message,
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d(TAG, "error")
                        }
                        Status.LOADING -> {
                            binding.viewPager.visibility = View.GONE
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
                    when (result.status) {
                        Status.LOADING -> {
                            Log.d(TAG, "coin loading")
                            binding.pb.visibility = View.VISIBLE
                            binding.viewPager.visibility = View.GONE
                            binding.tabLayout.visibility = View.GONE
                        }
                        Status.ERROR -> {
                            Log.d(TAG, "coin error")
                            binding.pb.visibility = View.GONE
                            //TODO show error
//                            binding.viewPager.visibility = View.GONE
//                            binding.tabLayout.visibility = View.GONE

                        }

                        Status.SUCCESS -> {
                            Log.d(TAG, "coin success")
                            binding.pb.visibility = View.GONE
//                            binding.viewPager.visibility = View.VISIBLE
//                            binding.tabLayout.visibility = View.VISIBLE
                            viewModel.currentCoinData.postValue(result.data)
                            if(it.data?.id?.let { it1 -> withContext(Dispatchers.IO){repo?.count(it1)} } !=0)setFavStatus(true)
                            else setFavStatus(false)
                        }
                    }

                }
            }
        }
    }

    private fun setFavStatus(boolean: Boolean) {
        if(menu ==null) return
        //TODO change
        var icon = R.drawable.ic_star_solid
        if(!boolean) icon = R.drawable.ic_star
        this.menu?.findItem(R.id.action_fav)?.setIcon(icon)
    }

    private var menu: Menu? =null
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
//            TODO(lazy) improve
            getNewData(id)
            true
        }

        R.id.action_fav -> {
            Toast.makeText(this,"nhi",Toast.LENGTH_LONG).show()
            val coinCd = viewModel.currentCoinData.value
            if( coinCd!=null)toggleFav(coinCd.id,coinCd.name)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private var toggleFavJob:Job?=null
    private fun toggleFav(id: String,name: String) {
        toggleFavJob?.cancel()
        toggleFavJob =lifecycleScope.launch {
            if(withContext(Dispatchers.IO){repo?.count(id)}==0){
                withContext(Dispatchers.IO){repo?.addFavCoin(FavCoin(id,name))}
                setFavStatus(true)
            }else{
                withContext(Dispatchers.IO){repo?.removeFavCoin(id)}
                setFavStatus(false)
            }
        }
    }

}
