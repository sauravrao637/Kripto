package com.camo.kripto.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.camo.kripto.data.model.Global
import com.camo.kripto.databinding.FragGlobalBinding
import com.camo.kripto.ui.viewModel.MarketCapVM
import com.camo.kripto.utils.Status
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class FragGlobal : Fragment() {
    companion object {
        private val TAG = FragGlobal::class.simpleName
    }

    private lateinit var binding: FragGlobalBinding
    private lateinit var viewModel: MarketCapVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragGlobalBinding.inflate(LayoutInflater.from(context), container, false)
        binding.root.visibility = View.VISIBLE

        setupVM()
        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupVM() {
        viewModel = ViewModelProviders.of(
            requireActivity()
        ).get(MarketCapVM::class.java)
    }

    private fun setupUI() {
        getGlobal()

    }

    private fun setupObservers() {
        viewModel.global.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.ERROR -> {
                    binding.pbFragGlobal.visibility = View.GONE
                    Log.d(TAG, it.message ?: "some error")
                }
                Status.LOADING -> {
                    binding.pbFragGlobal.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.pbFragGlobal.visibility = View.GONE
                    if (it.data != null) updateView(it.data)
                }
            }
        }
        )
    }

    fun updateView(global: Global) {
        val curr = viewModel.prefCurrency.value ?: "inr"
        binding.tvFragGloablActiveCryptocurrencies.text =
            global.data.active_cryptocurrencies.toString()
        binding.tvFragGlobalEndedIcos.text = global.data.ended_icos.toString()
        binding.tvFragGlobalMarketCap.text = global.data.total_market_cap[curr].toString()+" "+curr
        binding.tvFragGlobalMarkets.text = global.data.markets.toString()
        binding.tvFragGlobalOngoingIcos.text = global.data.ongoing_icos.toString()
        binding.tvFragGlobalUpcomingIcos.text = global.data.upcoming_icos.toString()
        binding.tvFragGlobalVolume.text = global.data.total_volume[curr].toString()+" "+curr

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
        chart.centerText = generateCenterSpannableText()

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


        chart.animateY(100, Easing.EaseInOutQuad)

        val l: Legend = chart.legend

        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.xEntrySpace = 10f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE)
//        chart.setEntryLabelTypeface(tfRegular)
        chart.setEntryLabelTextSize(14f)
    }

    private fun generateCenterSpannableText(): SpannableString? {
        val s = SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda")
        s.setSpan(RelativeSizeSpan(1.7f), 0, 14, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 14, s.length - 15, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 14, s.length - 15, 0)
        s.setSpan(RelativeSizeSpan(.8f), 14, s.length - 15, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 14, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 14, s.length, 0)
        return s
    }

    private var globalJob: Job? = null
    fun getGlobal() {
        globalJob?.cancel()
        globalJob = lifecycleScope.launch {
            viewModel.getGlobal().collect {
                viewModel.global.postValue(it)
            }
        }
    }


    private fun setData(data: Map<String, Double>) {
        val chart = binding.pieChart
        val entries: ArrayList<PieEntry> = ArrayList()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        for (entry in data.entries) {

            entries.add(
                PieEntry(
                    (entry.value).toFloat(),
                    entry.key
                )
            )
        }
        val dataSet = PieDataSet(entries, "MarketCap Percentage")
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
//        data.setValueTypeface(tfLight)
        chart.data = data

        // undo all highlights
        chart.highlightValues(null)
        chart.invalidate()
    }

}