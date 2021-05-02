package com.camo.kripto.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.data.model.Coin
import com.camo.kripto.databinding.ActivityMainBinding
import com.camo.kripto.ui.adapter.CoinAdapter
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.viewModel.CoinsVM
import com.camo.kripto.utils.Status

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: CoinsVM
    private lateinit var adapter: CoinAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setupViewModel()
        setupUI()
        setupObservers()

    }


    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            VMFactory(CGApiHelper(RetrofitBuilder.CG_SERVICE))
        ).get(CoinsVM::class.java)
    }


    private fun setupUI() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CoinAdapter(arrayListOf())
        binding.recyclerView.addItemDecoration(

                DividerItemDecoration(
                        binding.recyclerView.context,
                        (binding.recyclerView.layoutManager as LinearLayoutManager).orientation
                )
        )
        binding.recyclerView.adapter = adapter
    }
    private fun setupObservers() {
        viewModel.getCoins().observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        resource.data?.let { coins -> retrieveList(coins) }
                    }
                    Status.ERROR -> {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun retrieveList(coins: List<Coin.CoinItem>) {
        adapter.apply {
            addCoins(coins)
            notifyDataSetChanged()
        }
    }
}