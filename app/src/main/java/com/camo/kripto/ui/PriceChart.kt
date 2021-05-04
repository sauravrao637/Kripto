package com.camo.kripto.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SpinnerAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.camo.kripto.R
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.data.model.CoinCD
import com.camo.kripto.databinding.FragmentPriceChartBinding
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.viewModel.CoinActivityVM

class PriceChart : Fragment() {
    private val TAG = PriceChart::class.simpleName
    private lateinit var viewModel: CoinActivityVM
    private lateinit var binding: FragmentPriceChartBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentPriceChartBinding.inflate(inflater, container, false)

        setupViewModel()
        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            requireActivity(),
            VMFactory(CGApiHelper(RetrofitBuilder.CG_SERVICE))
        ).get(CoinActivityVM::class.java)

    }

    private fun setupObservers() {
        viewModel.CD.observe(viewLifecycleOwner, {
            if (it != null) {
                coinChanged(it)
            }
        })
        viewModel.duration.observe(viewLifecycleOwner, {
            setPerChange(it)
        })

        viewModel.currency.observe(viewLifecycleOwner, {
            val cd = viewModel.CD.value
            if (cd != null) {
                updateUI(cd, it)
                setPerChange(viewModel.duration.value)
            }
        })

        viewModel.allCurr.observe(viewLifecycleOwner, {
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                it
            ) as SpinnerAdapter
            binding.ddCurrency.adapter = adapter
        })

        viewModel.allCurr.observe(viewLifecycleOwner, {
            binding.ddCurrency.setSelection(it?.indexOf(viewModel.currency.value) ?: 0)
            }
        )
    }

    private fun coinChanged(coinCD: CoinCD) {
        viewModel.title.postValue(coinCD.name)
        context?.let { it1 ->
            val iv = activity?.actionBar?.customView?.findViewById<ImageView>(R.id.iv_ab_coin)
            if (iv != null)
                Glide.with(it1)
                    .load(coinCD.image.small)
                    .into(iv)
        }
        updateUI(coinCD, viewModel.currency.value ?: "inr")
        setPerChange(viewModel.duration.value)

    }

    private fun setPerChange(it: String?) {
        val coinCD = viewModel.CD.value
        val curr = viewModel.currency.value ?: "inr"
        var change: Double? = null
        if (coinCD != null) {
            when {
                it.equals("24h") -> {
                    change =
                        coinCD.market_data.price_change_percentage_24h_in_currency[curr]
                    setCurrentSelected(binding.tv24h.id)
                }
                it.equals("7d") -> {
                    change =
                        coinCD.market_data.price_change_percentage_7d_in_currency[curr]
                    setCurrentSelected(binding.tv7d.id)
                }
                it.equals("14d") -> {
                    change =
                        coinCD.market_data.price_change_percentage_14d_in_currency[curr]
                    setCurrentSelected(binding.tv2w.id)
                }
                it.equals("30d") -> {
                    change =
                        coinCD.market_data.price_change_percentage_30d_in_currency[curr]
                    setCurrentSelected(binding.tv1m.id)
                }
                it.equals("60d") -> {
                    change =
                        coinCD.market_data.price_change_percentage_60d_in_currency[curr]
                    setCurrentSelected(binding.tv2m.id)
                }
                it.equals("200d") -> {
                    change =
                        coinCD.market_data.price_change_percentage_200d_in_currency[curr]
                    setCurrentSelected(binding.tv200d.id)
                }

            }
            if (change != null) {
                if (change >= 0) {
                    binding.tvPerChange.setTextColor(Color.GREEN)
                } else {
                    binding.tvPerChange.setTextColor(Color.RED)
                }
                binding.tvPerChange.text = change.toString()
            } else {
                binding.tvPerChange.text = "NA"

            }
        }
    }

    private fun setCurrentSelected(id: Int) {
        if (id == binding.tv24h.id) binding.tv24h.setBackgroundColor(Color.GREEN)
        else binding.tv24h.setBackgroundColor(Color.TRANSPARENT)
        if (id == binding.tv7d.id) binding.tv7d.setBackgroundColor(Color.GREEN)
        else binding.tv7d.setBackgroundColor(Color.TRANSPARENT)
        if (id == binding.tv2w.id) binding.tv2w.setBackgroundColor(Color.GREEN)
        else binding.tv2w.setBackgroundColor(Color.TRANSPARENT)
        if (id == binding.tv1m.id) binding.tv1m.setBackgroundColor(Color.GREEN)
        else binding.tv1m.setBackgroundColor(Color.TRANSPARENT)
        if (id == binding.tv2m.id) binding.tv2m.setBackgroundColor(Color.GREEN)
        else binding.tv2m.setBackgroundColor(Color.TRANSPARENT)
        if (id == binding.tv200d.id) binding.tv200d.setBackgroundColor(Color.GREEN)
        else binding.tv200d.setBackgroundColor(Color.TRANSPARENT)


    }


    private fun setupUI() {
        val listener = View.OnClickListener {
            var text = "24h"
            when (it.id) {
                R.id.tv_7d -> text = "7d"
                R.id.tv_1m -> text = "30d"
                R.id.tv_2w -> text = "14d"
                R.id.tv_2m -> text = "60d"
                R.id.tv_200d -> text = "200d"
            }
            viewModel.duration.postValue(text)

        }

        binding.tv24h.setOnClickListener(listener)
        binding.tv7d.setOnClickListener(listener)
        binding.tv1m.setOnClickListener(listener)
        binding.tv2w.setOnClickListener(listener)
        binding.tv2m.setOnClickListener(listener)
        binding.tv200d.setOnClickListener(listener)

        binding.ddCurrency.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
//                   TODO
                }
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.currency.postValue(viewModel.allCurr.value?.get(position))
                }
            }


    }

    private fun updateUI(coinCD: CoinCD, curr: String) {

        binding.tv24HHigh.text = coinCD.market_data.high_24h[curr].toString()
        binding.tvAllTimeHigh.text = coinCD.market_data.ath[curr].toString()
        binding.tvAllTimeHightOn.text = coinCD.market_data.ath_date[curr].toString()
        binding.tvAllTimeLow.text = coinCD.market_data.atl[curr].toString()
        binding.tvAllTimeLowOn.text = coinCD.market_data.atl_date[curr].toString()
        binding.tv24HLow.text = coinCD.market_data.low_24h[curr].toString()
        binding.tvCurrentPrice.text = coinCD.market_data.current_price[curr].toString()
        binding.tvAvailableSupply.text = coinCD.market_data.circulating_supply.toString()
        binding.tvFullyDilutedValuation.text =
            coinCD.market_data.fully_diluted_valuation[curr].toString()
        binding.tvMarketCap.text = coinCD.market_data.market_cap[curr].toString()
        binding.tvMarketCapRank.text = coinCD.market_cap_rank.toString()
        binding.tvTotalSupply.text = coinCD.market_data.total_supply.toString()
        binding.tvTradingVolume.text = coinCD.market_data.total_volume[curr].toString()


    }

}