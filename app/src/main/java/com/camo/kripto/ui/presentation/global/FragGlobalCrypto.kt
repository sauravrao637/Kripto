package com.camo.kripto.ui.presentation.global

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.camo.kripto.databinding.FragGlobalCryptoBinding
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
//Fragment for global data for cryptocurrency
class FragGlobalCrypto : Fragment() {

    private lateinit var binding: FragGlobalCryptoBinding
    private val viewModel by activityViewModels<GlobalVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragGlobalCryptoBinding.inflate(LayoutInflater.from(context), container, false)
        binding.root.visibility = View.VISIBLE

        setupVM()
        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupVM() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.getString("pref_currency", "inr") ?: "inr"
    }

    private fun setupUI() {
        getGlobal()
    }

    private fun setupObservers() {
        viewModel.globalCrypto.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.ERROR -> {
                    binding.pbFragGlobal.visibility = View.GONE
                    Timber.d(it.message ?: "some error")
                }
                Status.LOADING -> {
                    binding.pbFragGlobal.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.pbFragGlobal.visibility = View.GONE
                    if (it.data != null) updateView(it.data)
                }
            }
        })

        viewModel.refreshed.observe(viewLifecycleOwner,{
            if(it){
                refresh()
            }
        })
    }

    private fun updateView(global: Global) {
        val curr = viewModel.prefCurrency.value ?: "inr"
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

        updateChart()
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

    private fun refresh(){
        getGlobal()
    }

    private var globalJob: Job? = null
    private fun getGlobal() {
        globalJob?.cancel()
        globalJob = lifecycleScope.launch {
            viewModel.getGlobal().collect {
                viewModel.globalCrypto.postValue(it)
            }
        }
    }


    private fun setData(graphData: Map<String, Double>) {
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
        val dataSet = PieDataSet(entries,"")
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