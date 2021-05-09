package com.camo.kripto.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.camo.kripto.R
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.data.model.CoinCD
import com.camo.kripto.data.model.MarketChart
import com.camo.kripto.databinding.FragPriceChartBinding
import com.camo.kripto.ui.base.VMFactory
import com.camo.kripto.ui.viewModel.CoinActivityVM
import com.camo.kripto.utils.ChartMarker
import com.camo.kripto.utils.Formatter
import com.camo.kripto.utils.Graph
import com.camo.kripto.utils.Status
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class FragPriceChart : Fragment() {
    private val TAG = FragPriceChart::class.simpleName
    private lateinit var viewModel: CoinActivityVM
    private lateinit var binding: FragPriceChartBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragPriceChartBinding.inflate(inflater, container, false)
        binding.root.visibility = View.INVISIBLE
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

        binding.btnRefreshGraph.setOnClickListener {
            updateChart()
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

    private fun setupObservers() {
        //observing coin
        viewModel.currentCoinData.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.root.visibility = View.VISIBLE
                coinChanged(it)
            } else {

                Log.d(TAG, "coinData null")
            }
            updateChart()
        })
        //observing duration selected
        viewModel.duration.observe(viewLifecycleOwner, {
            setPerChange(it)
            updateChart()
        })

        //observing currency selected
        viewModel.currency.observe(viewLifecycleOwner, {
            val cd = viewModel.currentCoinData.value
            if (cd != null) {
                updateUI(cd, it)
                setPerChange(viewModel.duration.value)
                updateChart()
            }
        })

        //observing all supported currencies
        //TODO make room and remove this in future
        viewModel.allCurr.observe(viewLifecycleOwner, {
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                it
            ) as SpinnerAdapter
            binding.ddCurrency.adapter = adapter
            binding.ddCurrency.setSelection(it?.indexOf(viewModel.currency.value) ?: 0)
        })

    }

    private var chartJob: Job? = null
    private fun updateChart() {
        chartJob?.cancel()
        Log.d(TAG, "launching chartJob")
        val id = viewModel.currentCoinData.value?.id
        val curr = viewModel.currency.value
        val dur = viewModel.duration.value
        chartJob = lifecycleScope.launch {
            viewModel.getChart(id, curr, dur).collect {
                when (it.status) {
                    Status.LOADING -> {
                        binding.pbChart.visibility = View.VISIBLE
                        binding.btnRefreshGraph.visibility = View.INVISIBLE
                        binding.chart.visibility = View.INVISIBLE
                        Log.d(TAG, "graph lvding")
                    }
                    Status.SUCCESS -> {
                        binding.chart.visibility = View.VISIBLE
                        binding.pbChart.visibility = View.INVISIBLE
                        binding.btnRefreshGraph.visibility = View.INVISIBLE
                        createGraph(it.data)
                    }
                    Status.ERROR -> {
                        Log.d(TAG, it.message ?: "some error in chartJob")
                        binding.pbChart.visibility = View.INVISIBLE
                        binding.btnRefreshGraph.visibility = View.VISIBLE
                        binding.chart.visibility = View.INVISIBLE
                        if (!it.message.equals("null parameter")) Toast.makeText(
                            context,
                            "\uD83D\uDE28 Wooops" + it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }

    private fun createGraph(marketChart: MarketChart?) {

        val data = Graph.getData(marketChart)

        val color = Color.WHITE
        (data.getDataSetByIndex(0) as LineDataSet).circleHoleColor = color

        binding.chart.description.isEnabled = false

        // enable / disable grid background
//        binding.chart.setDrawGridBackground(true)
//
//        binding.chart.setGridBackgroundColor(Color.WHITE)

        // enable touch gestures
        binding.chart.setTouchEnabled(true)

        // enable scaling and dragging

        // enable scaling and dragging
        binding.chart.isDragEnabled = true
        binding.chart.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately

        // if disabled, scaling can be done on x- and y-axis separately
        binding.chart.setPinchZoom(false)
        binding.chart.isDoubleTapToZoomEnabled = false

        binding.chart.setBackgroundColor(Color.BLACK)

        // set custom chart offsets (automatic offset calculation is hereby disabled)

        // set custom chart offsets (automatic offset calculation is hereby disabled)
//        binding.chart.setViewPortOffsets(150f, 0f, 25f, 0f)

        // add data

        // add data
        binding.chart.data = data

        // get the legend (only possible after setting data)

        // get the legend (only possible after setting data)
        val l: Legend = binding.chart.legend
        l.isEnabled = false

        binding.chart.axisLeft.isEnabled = true
        binding.chart.axisLeft.spaceTop = 40f
        binding.chart.axisLeft.spaceBottom = 40f
        binding.chart.axisRight.isEnabled = false

//        binding.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE

        binding.chart.xAxis.isEnabled = true
        binding.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//        binding.chart.xAxis.labelRotationAngle = 315f

        binding.chart.xAxis.labelCount = 3
        binding.chart.xAxis.setCenterAxisLabels(true)
        binding.chart.xAxis.enableGridDashedLine(10f, 10f, 50f)

        binding.chart.xAxis.textColor = Color.WHITE
        binding.chart.axisLeft.textColor = Color.WHITE

        binding.chart.animateX(500)
        binding.chart.xAxis.valueFormatter = Formatter()
        binding.chart.marker = ChartMarker(context, R.layout.marker_view)

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
        var text = "NA"
        val coinCD = viewModel.currentCoinData.value
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
                text = "$change%"
            }
            binding.tvPerChange.text = text


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