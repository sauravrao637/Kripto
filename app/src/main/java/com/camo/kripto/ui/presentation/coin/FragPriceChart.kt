package com.camo.kripto.ui.presentation.coin

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.camo.kripto.R
import com.camo.kripto.databinding.FragPriceChartBinding
import com.camo.kripto.local.model.Currency
import com.camo.kripto.remote.model.CoinCD
import com.camo.kripto.remote.model.MarketChart
import com.camo.kripto.repos.Repository
import com.camo.kripto.ui.viewModel.CoinActivityVM
import com.camo.kripto.utils.*
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FragPriceChart : Fragment() {

    private val viewModel by activityViewModels<CoinActivityVM>()
    private lateinit var binding: FragPriceChartBinding
    private lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var repo: Repository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragPriceChartBinding.inflate(inflater, container, false)
        binding.root.visibility = View.INVISIBLE
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        setupUI()
        setupObservers()
        return binding.root
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

        binding.ddCurrency.isVisible = false
        getCurrencies()

    }


    private fun setupObservers() {
        //observing coin
        viewModel.currentCoinData.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.root.visibility = View.VISIBLE
                coinChanged(it)
                updateChart()
            } else {

                Timber.d("coinData null")
            }

        })

        //observing duration selected
        viewModel.duration.observe(viewLifecycleOwner, {
            setPerChange(it)
            updateChart()
        })

        //observing currency selected
        viewModel.currency.observe(viewLifecycleOwner, {
            val cd = viewModel.currentCoinData.value
            if (cd != null && it != null) {
                updateUI(cd, it)
                setPerChange(viewModel.duration.value)
                updateChart()
            }
        })

        viewModel.allCurr.observe(viewLifecycleOwner){
            currencies ->
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                currencies
            ) as SpinnerAdapter
            binding.ddCurrency.adapter = adapter
            binding.ddCurrency.setSelection(currencies.indexOf(viewModel.currency.value))
            binding.ddCurrency.isVisible = true
        }

    }

    private fun setCurr(array: Array<String>) {
        if (array.isEmpty()) {
            Timber.d("Empty curr array")
        }




    }


    private var loadCurrJob: Job? = null
    private fun getCurrencies() {
        loadCurrJob?.cancel()
        loadCurrJob = lifecycleScope.launch {
            val curr = ArrayList<Currency>()
            withContext(Dispatchers.IO) { repo.getCurrencies() }.let {
                curr.addAll(it)
                if (curr.isEmpty()) {
                    val res = repo.lIRcurrencies()
                    when (res.status) {
                        Status.SUCCESS -> getCurrencies()
                        Status.ERROR -> Toast.makeText(context, res.message, Toast.LENGTH_LONG)
                            .show()

                        else -> Toast.makeText(
                            context,
                            "This wasn't expected at all",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            val list = ArrayList<String>()
            withContext(Dispatchers.IO) {
                for (c in curr) {
                    list.add(Extras.getCurrencySymbol(c.id) ?: c.id)
                }
            }
            setCurr(list.toTypedArray())
        }
    }

    private var chartJob: Job? = null
    private fun updateChart() {
        chartJob?.cancel()
        Timber.d("launching chartJob")
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
                        Timber.d("graph lvding")
                    }
                    Status.SUCCESS -> {
                        binding.chart.visibility = View.VISIBLE
                        binding.pbChart.visibility = View.INVISIBLE
                        binding.btnRefreshGraph.visibility = View.INVISIBLE
                        createGraph(it.data)
                    }
                    Status.ERROR -> {
                        Timber.d(it.message ?: "some error in chartJob")
                        binding.pbChart.visibility = View.INVISIBLE
                        binding.btnRefreshGraph.visibility = View.VISIBLE
                        binding.chart.visibility = View.INVISIBLE
//                        TODO report error
//                        if (!it.message.equals("null parameter")) Toast.makeText(
//                            context,
//                            "\uD83D\uDE28 Wooops" + it.message,
//                            Toast.LENGTH_SHORT
//                        ).show()
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
        binding.chart.setTouchEnabled(true)
        binding.chart.isDragEnabled = true
        binding.chart.setScaleEnabled(true)
        binding.chart.setPinchZoom(false)
        binding.chart.isDoubleTapToZoomEnabled = false
        binding.chart.setBackgroundColor(Color.BLACK)
        binding.chart.data = data

        val l: Legend = binding.chart.legend
        l.isEnabled = false

        binding.chart.axisLeft.isEnabled = true
        binding.chart.axisLeft.spaceTop = 40f
        binding.chart.axisLeft.spaceBottom = 40f
        binding.chart.axisRight.isEnabled = false

        binding.chart.xAxis.isEnabled = true
        binding.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

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
        if (id == binding.tv24h.id) binding.tv24h.setTextColor(Color.GREEN)
        else binding.tv24h.setTextColor(Color.WHITE)
        if (id == binding.tv7d.id) binding.tv7d.setTextColor(Color.GREEN)
        else binding.tv7d.setTextColor(Color.WHITE)
        if (id == binding.tv2w.id) binding.tv2w.setTextColor(Color.GREEN)
        else binding.tv2w.setTextColor(Color.WHITE)
        if (id == binding.tv1m.id) binding.tv1m.setTextColor(Color.GREEN)
        else binding.tv1m.setTextColor(Color.WHITE)
        if (id == binding.tv2m.id) binding.tv2m.setTextColor(Color.GREEN)
        else binding.tv2m.setTextColor(Color.WHITE)
        if (id == binding.tv200d.id) binding.tv200d.setTextColor(Color.GREEN)
        else binding.tv200d.setTextColor(Color.WHITE)
    }

    private fun updateUI(coinCD: CoinCD, curr: String) {
        binding.tv24HHigh.text = Extras.getFormattedDoubleCurr(
            coinCD.market_data.high_24h[curr],
            curr,
            suffix = ""
        )

        binding.tvAllTimeHigh.text =
            Extras.getFormattedDoubleCurr(coinCD.market_data.ath[curr], curr, suffix = "")
        binding.tvAllTimeHightOn.text = Extras.getInLocalTime(coinCD.market_data.ath_date[curr])
        binding.tvAllTimeLow.text =
            Extras.getFormattedDoubleCurr(coinCD.market_data.atl[curr], curr, suffix = "")
        binding.tvAllTimeLowOn.text = Extras.getInLocalTime(coinCD.market_data.atl_date[curr])
        binding.tv24HLow.text =
            Extras.getFormattedDoubleCurr(coinCD.market_data.low_24h[curr], curr, suffix = "")
        binding.tvCurrentPrice.text =
            Extras.getFormattedDoubleCurr(coinCD.market_data.current_price[curr], curr, suffix = "")
        binding.tvAvailableSupply.text =
            Extras.getFormattedDouble(coinCD.market_data.circulating_supply)
        binding.tvFullyDilutedValuation.text =
            Extras.getFormattedDoubleCurr(
                coinCD.market_data.fully_diluted_valuation[curr],
                curr,
                suffix = ""
            )

        binding.tvMarketCap.text =
            Extras.getFormattedDoubleCurr(coinCD.market_data.market_cap[curr], curr, suffix = "")

        binding.tvMarketCapRank.text = coinCD.market_cap_rank.toString()
        binding.tvTotalSupply.text = Extras.getFormattedDouble(coinCD.market_data.total_supply)
        binding.tvTradingVolume.text =
            Extras.getFormattedDoubleCurr(coinCD.market_data.total_volume[curr], curr, suffix = "")
    }


}