package com.camo.kripto.ui.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.camo.kripto.R
import com.camo.kripto.databinding.FragNewsBinding
import com.camo.kripto.ktx.enforceSingleScrollDirection
import com.camo.kripto.ktx.recyclerView
import com.camo.kripto.ui.adapter.tab.NewsCategoriesTabAdapter
import com.google.android.material.tabs.TabLayoutMediator

class FragNews: Fragment() {
    private lateinit var binding: FragNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragNewsBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        if (activity != null) {
            val adapter = NewsCategoriesTabAdapter(requireActivity())
            binding.vpFragNews1.adapter = adapter
            TabLayoutMediator(binding.tlFragNews1, binding.vpFragNews1) { tab, position ->
                when (position) {
                    0 -> tab.text = this.getString(R.string.general)
                    1 -> tab.text = this.getString(R.string.milestone)
                    2 -> tab.text = this.getString(R.string.partnership)
                    3 -> tab.text = this.getString(R.string.exchange_listing)
                    4 -> tab.text = this.getString(R.string.software_release)
                    5 -> tab.text = this.getString(R.string.fund_movement)
                    6 -> tab.text = this.getString(R.string.new_listings)
                    7 -> tab.text = this.getString(R.string.event)
                }
            }.attach()
        }
        binding.vpFragNews1.recyclerView.enforceSingleScrollDirection()
    }
}