package com.camo.kripto.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.data.model.CoinCD
import com.camo.kripto.databinding.FragCoinInfoBinding
import com.camo.kripto.databinding.FragPriceChartBinding
import com.camo.kripto.ui.adapter.UrlAdapter
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.viewModel.CoinActivityVM
import com.camo.kripto.utils.Status

class CoinInfoFrag : Fragment() {
    private val TAG = CoinInfoFrag::class.simpleName

    private lateinit var binding: FragCoinInfoBinding
    private lateinit var viewModel: CoinActivityVM
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragCoinInfoBinding.inflate(inflater, container, false)

        setupViewModel()
        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.currentCoinData.observe(viewLifecycleOwner, {
            if (it != null) {
                coinChanged(it)

            } else Log.d(TAG, "coinData null")
        })
    }

    private fun coinChanged(coinCD: CoinCD) {

        binding.rvHomepageUrls.adapter = UrlAdapter(coinCD.links.homepage)
        binding.rvHomepageUrls.setHasFixedSize(true)
        binding.rvOfficialForumUrls.adapter = UrlAdapter(coinCD.links.blockchain_site)
        binding.rvOfficialForumUrls.setHasFixedSize(true)
        binding.rvBlockchainUrls.adapter = UrlAdapter(coinCD.links.official_forum_url)
        binding.rvBlockchainUrls.setHasFixedSize(true)
        val l = coinCD.links.chat_url.plus(coinCD.links.announcement_url)
        binding.rvOtherUrls.adapter = UrlAdapter(l)
        binding.rvOtherUrls.setHasFixedSize(true)

    }

    private fun setupUI() {
        binding.rvHomepageUrls.layoutManager = LinearLayoutManager(context)
        binding.rvBlockchainUrls.layoutManager = LinearLayoutManager(context)
        binding.rvOfficialForumUrls.layoutManager = LinearLayoutManager(context)
        binding.rvOtherUrls.layoutManager = LinearLayoutManager(context)

    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            requireActivity(),
            VMFactory(CGApiHelper(RetrofitBuilder.CG_SERVICE))
        ).get(CoinActivityVM::class.java)
    }


}