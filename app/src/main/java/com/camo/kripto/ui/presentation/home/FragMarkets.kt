package com.camo.kripto.ui.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.camo.kripto.databinding.FragMarketsBinding
import com.camo.kripto.ui.adapter.MarketsTabAdapter
import com.google.android.material.tabs.TabLayoutMediator

class FragMarkets: Fragment() {
    private lateinit var binding: FragMarketsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragMarketsBinding.inflate(inflater,container,false)
        setupUI()

        return binding.root
    }

    private fun setupUI() {
        val adapter = activity?.let {
            MarketsTabAdapter(
                it
            )
        }
        binding.vpFragMarkets1.adapter = adapter

        TabLayoutMediator(binding.tlFragMarkets1, binding.vpFragMarkets1) { tab, position ->
            when (position) {
                0 -> tab.text = "Cryptocurrencies"
                1 -> tab.text = "Exchanges"
            }
        }.attach()
    }
}