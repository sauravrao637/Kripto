package com.camo.kripto.ui.presentation.global

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.camo.kripto.databinding.FragGlobalCryptoBinding
import com.camo.kripto.error.ErrorInfo
import com.camo.kripto.error.ErrorPanelHelper
import com.camo.kripto.remote.model.Global
import com.camo.kripto.ui.viewModel.GlobalVM
import com.camo.kripto.utils.Extras
import com.camo.kripto.utils.Status
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.math.BigDecimal

@AndroidEntryPoint
//Fragment for global data for cryptocurrency
class FragGlobalCrypto : Fragment() {

    private lateinit var binding: FragGlobalCryptoBinding
    private val viewModel by activityViewModels<GlobalVM>()
    private lateinit var errorPanelHelper: ErrorPanelHelper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragGlobalCryptoBinding.inflate(LayoutInflater.from(context), container, false)
        updateChart()
        errorPanelHelper = ErrorPanelHelper(binding.root,::refresh)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenResumed {
                viewModel.globalCrypto.collectLatest {
                    when (it.status) {
                        Status.ERROR -> {
                            showErrorUI(it.errorInfo)
                            Timber.d(it.errorInfo?.messageStringId.toString())
                        }
                        Status.LOADING -> {
                            showLoadingUI()
                            errorPanelHelper.hide()
                            errorPanelHelper.dispose()
                        }
                        Status.SUCCESS -> {
                            if (it.data != null) {
                                updateView(it.data)
                                binding.pbFragGlobal.visibility = View.GONE
                                binding.groupFragGlobalCrypto.visibility = View.VISIBLE
                                binding.errorPanel.root.visibility = View.GONE
                                errorPanelHelper.hide()
                                errorPanelHelper.dispose()
                            }else{
                                showErrorUI(null)
                            }

                        }
                    }
                }
        }

        binding.root.setOnRefreshListener {
            refresh()
            binding.root.isRefreshing = false
        }
    }

    private fun showLoadingUI() {
        binding.pbFragGlobal.visibility = View.VISIBLE
        binding.groupFragGlobalCrypto.visibility = View.GONE
        binding.errorPanel.root.visibility = View.GONE
    }

    private fun showErrorUI(errorInfo: ErrorInfo?) {
        errorPanelHelper.showError(errorInfo)
        binding.errorPanel.root.visibility = View.VISIBLE
        binding.groupFragGlobalCrypto.visibility = View.GONE
        binding.pbFragGlobal.visibility = View.GONE
    }

    private fun updateView(global: Global) {
        val curr = viewModel.prefCurrency.value
        binding.tvFragGloablActiveCryptocurrencies.text =
            global.data.active_cryptocurrencies.toString()
        binding.tvFragGlobalEndedIcos.text = global.data.ended_icos.toString()
        binding.tvFragGlobalMarketCap.text = Extras.getFormattedDoubleCurr(
            global.data.total_market_cap[curr],
            curr,
            suffix = ""
        )
        binding.tvFragGlobalMarkets.text = global.data.markets.toString()
        binding.tvFragGlobalOngoingIcos.text = global.data.ongoing_icos.toString()
        binding.tvFragGlobalUpcomingIcos.text = global.data.upcoming_icos.toString()
        binding.tvFragGlobalVolume.text = Extras.getFormattedDoubleCurr(
            global.data.total_volume[curr],
            curr,
            suffix = ""
        )
        setData(global.data.market_cap_percentage)
    }

    private fun updateChart() {
        val chart = binding.pieChart
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5F, 10F, 5F, 5F)

        chart.dragDecelerationFrictionCoef = 0.95f

//        chart.setCenterTextTypeface(tfLight)

        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.TRANSPARENT)

        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)

        chart.holeRadius = 50f
        chart.transparentCircleRadius = 55f

        chart.setDrawCenterText(true)
        chart.centerText = "Market Cap %"
        chart.setCenterTextColor(Color.WHITE)

        chart.rotationAngle = 0F
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true

        chart.animateY(1000, Easing.EaseInOutQuad)

        val l: Legend = chart.legend

        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.xEntrySpace = 0f
        l.yEntrySpace = 10f
        l.yOffset = 0f

        // entry label styling

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE)
//        chart.setEntryLabelTypeface(tfRegular)
        chart.setEntryLabelTextSize(14f)
    }

    private fun refresh() {
        viewModel.getGlobal()
    }

    private fun setData(graphData: Map<String, BigDecimal>) {
        val chart = binding.pieChart
        val entries: ArrayList<PieEntry> = ArrayList()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        for (entry in graphData.entries) {

            entries.add(
                PieEntry(
                    (entry.value).toFloat(),
                    entry.key
                )
            )
        }
        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(true)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 10f

        // add a lot of colors
        val colors: ArrayList<Int> = ArrayList()

        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
//        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
//        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
        dataSet.selectionShift = 10f
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.WHITE)
        chart.data = data
        chart.highlightValues(null)
        chart.invalidate()
    }
}