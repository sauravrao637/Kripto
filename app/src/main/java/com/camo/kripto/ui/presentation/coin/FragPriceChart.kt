package com.camo.kripto.ui.presentation.coin

import android.app.ActionBar
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.camo.kripto.R
import com.camo.kripto.databinding.FragPriceChartBinding
import com.camo.kripto.error.ErrorPanelHelper
import com.camo.kripto.remote.model.MarketChart
import com.camo.kripto.ui.viewModel.CoinActivityVM
import com.camo.kripto.utils.*
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal

@AndroidEntryPoint
class FragPriceChart : Fragment() {

    private val viewModel by activityViewModels<CoinActivityVM>()
    private lateinit var binding: FragPriceChartBinding
    private lateinit var actionBar: ActionBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragPriceChartBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupUI() {
        binding.radioGroupChartDuration.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_24h -> viewModel.durationChanged("24h")
                R.id.rb_7d -> viewModel.durationChanged("7d")
                R.id.rb_1m -> viewModel.durationChanged("30d")
                R.id.rb_2w -> viewModel.durationChanged("14d")
                R.id.rb_2m -> viewModel.durationChanged("60d")
                R.id.rb_200d -> viewModel.durationChanged("200d")
            }
        }
        binding.radioGroupChartDuration.isSelected = true
        when (viewModel.duration.value) {
            "24h" -> binding.radioGroupChartDuration.check(R.id.rb_24h)
            "7d" -> binding.radioGroupChartDuration.check(R.id.rb_7d)
            "30d" -> binding.radioGroupChartDuration.check(R.id.rb_1m)
            "14d" -> binding.radioGroupChartDuration.check(R.id.rb_2w)
            "60d" -> binding.radioGroupChartDuration.check(R.id.rb_2m)
            "200d" -> binding.radioGroupChartDuration.check(R.id.rb_200d)
        }

    }

    private fun setupObservers() {
        viewModel.coinDurCurrState.observe(viewLifecycleOwner, {
            updateCurrencyDurationDependentUI()
        })
        lifecycleScope.launchWhenStarted {
            viewModel.coinData.collectLatest {
                when (it.status) {
                    Status.SUCCESS -> {
                        populateStaticUI()
                    }
                    else -> {
                        //handled by activity
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.supportedCurrencies.collectLatest { res ->
                when (res.status) {
                    Status.SUCCESS -> {
                        res.data?.toTypedArray()?.let { setCurr(it) }
                        binding.ddCurrency.visibility = View.VISIBLE
                    }
                    else -> binding.ddCurrency.visibility = View.INVISIBLE
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.marketChart.collectLatest {
                val errorPanel = ErrorPanelHelper(binding.root, ::refreshChart)
                when (it.status) {
                    Status.LOADING -> {
                        binding.pbChart.visibility = View.VISIBLE
                        binding.chart.visibility = View.INVISIBLE
                        binding.errorPanel.root.visibility = View.INVISIBLE
                        errorPanel.hide()
                        errorPanel.dispose()
                    }
                    Status.ERROR -> {
                        binding.pbChart.visibility = View.GONE
                        binding.chart.visibility = View.INVISIBLE
                        binding.errorPanel.root.visibility = View.VISIBLE
                        errorPanel.showError(it.errorInfo)
                    }
                    Status.SUCCESS -> {
                        createGraph(it.data)
                        binding.chart.visibility = View.VISIBLE
                        binding.pbChart.visibility = View.GONE
                        binding.errorPanel.root.visibility = View.INVISIBLE
                        errorPanel.hide()
                        errorPanel.dispose()
                    }
                }
            }
        }
    }

    private fun refreshChart() {
        viewModel.updateChart()
    }

    private fun setCurr(array: Array<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            array
        ) as SpinnerAdapter
        binding.ddCurrency.adapter = adapter
        binding.ddCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                viewModel.currencyChanged(adapter.getItem(position).toString())
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
        binding.ddCurrency.setSelection(array.indexOf(viewModel.currency.value))
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

    private fun updateCurrencyDurationDependentUI() {
        var text = "NA"
        val it = viewModel.duration.value
        val coinCD = viewModel.coinData.value.data
        val curr = viewModel.currency.value
        var change: BigDecimal? = null
        refreshChart()
        if (coinCD != null) {
            when (it) {
                "24h" -> {
                    change =
                        coinCD.market_data.price_change_percentage_24h_in_currency[curr]
                }
                "7d" -> {
                    change =
                        coinCD.market_data.price_change_percentage_7d_in_currency[curr]
                }
                "14d" -> {
                    change =
                        coinCD.market_data.price_change_percentage_14d_in_currency[curr]
                }
                "30d" -> {
                    change =
                        coinCD.market_data.price_change_percentage_30d_in_currency[curr]
                }
                "60d" -> {
                    change =
                        coinCD.market_data.price_change_percentage_60d_in_currency[curr]
                }
                "200d" -> {
                    change =
                        coinCD.market_data.price_change_percentage_200d_in_currency[curr]
                }
            }
            if (change != null) {
                if (change >= BigDecimal(0)) {
                    binding.tvPerChange.setTextColor(Color.GREEN)
                } else {
                    binding.tvPerChange.setTextColor(Color.RED)
                }
                text = "$change%"
            }
            binding.tvPerChange.text = text

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
                Extras.getFormattedDoubleCurr(
                    coinCD.market_data.current_price[curr],
                    curr,
                    suffix = ""
                )
            binding.tvFullyDilutedValuation.text =
                Extras.getFormattedDoubleCurr(
                    coinCD.market_data.fully_diluted_valuation[curr],
                    curr,
                    suffix = ""
                )
            binding.tvMarketCap.text =
                Extras.getFormattedDoubleCurr(
                    coinCD.market_data.market_cap[curr],
                    curr,
                    suffix = ""
                )
            binding.tvTradingVolume.text =
                Extras.getFormattedDoubleCurr(
                    coinCD.market_data.total_volume[curr],
                    curr,
                    suffix = ""
                )
        }
    }

    private fun populateStaticUI() {
        val coinCD = viewModel.coinData.value.data
        if (coinCD != null) {
            binding.tvAvailableSupply.text =
                Extras.getFormattedDouble(coinCD.market_data.circulating_supply)
            binding.tvMarketCapRank.text = coinCD.market_cap_rank.toString()
            binding.tvTotalSupply.text = Extras.getFormattedDouble(coinCD.market_data.total_supply)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.tvAboutCoin.text = Html.fromHtml(
                    coinCD.description["en"],
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                binding.tvAboutCoin.text = Html.fromHtml(coinCD.description["en"])
            }
            Linkify.addLinks(binding.tvAboutCoin, Linkify.PHONE_NUMBERS or Linkify.WEB_URLS)
            updateCurrencyDurationDependentUI()
        }
    }
}