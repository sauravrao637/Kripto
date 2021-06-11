package com.camo.kripto.ui.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.camo.kripto.R
import com.camo.kripto.databinding.FragMoreBinding
import com.camo.kripto.error.ErrorInfo
import com.camo.kripto.error.ErrorPanelHelper
import com.camo.kripto.ktx.afterTextChanged
import com.camo.kripto.ui.adapter.ExchangeRatesAdapter
import com.camo.kripto.ui.presentation.global.GlobalActivity
import com.camo.kripto.ui.presentation.settings.SettingsActivity
import com.camo.kripto.ui.presentation.about.AboutActivity
import com.camo.kripto.ui.viewModel.MarketCapVM
import com.camo.kripto.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception
import java.math.BigInteger

@AndroidEntryPoint
class FragMore : Fragment() {

    private lateinit var binding: FragMoreBinding
    private lateinit var exchangeRatesAdapter: ExchangeRatesAdapter
    private val viewModel: MarketCapVM by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragMoreBinding.inflate(LayoutInflater.from(context))
        (activity as MainActivity).supportActionBar?.title =
            context?.resources?.getString(R.string.more)
        setupUI()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }
    private fun refreshExchangeRates(){
        viewModel.getExchangeRates()
    }
    private fun setupObservers() {
        binding.tvValueToCalc.afterTextChanged { s: String -> valueToCalcChanged(s) }
        lifecycleScope.launchWhenStarted {
            viewModel.exchangeRates.collectLatest {
                val errorPanelHelper = ErrorPanelHelper(binding.root,::refreshExchangeRates)
                when (it.status) {
                    Status.SUCCESS -> {
                        withContext(Dispatchers.IO) {
                            val entries = it.data?.body()?.rates?.entries
                            if (entries != null) {
                                val currencies = mutableListOf<String>()
                                for (i in entries) {
                                    currencies.add(i.key)
                                }
                                withContext(Dispatchers.Main) {
                                    binding.ddCurrencySelector.adapter = context?.let { it1 ->
                                        ArrayAdapter(
                                            it1,
                                            android.R.layout.simple_spinner_item,
                                            currencies
                                        )
                                    }
                                }
                            }
                        }
                        exchangeRatesAdapter.setData(it.data)
                        binding.ddCurrencySelector.visibility = View.VISIBLE
                        binding.rvExchangeRates.visibility = View.VISIBLE
                        binding.tvValueToCalc.visibility = View.VISIBLE
                        binding.pbExchangeRates.visibility = View.GONE
                        binding.errorPanel.root.visibility = View.GONE
                        errorPanelHelper.dispose()
                        errorPanelHelper.hide()
                    }
                    Status.LOADING ->{
                        binding.ddCurrencySelector.visibility = View.GONE
                        binding.rvExchangeRates.visibility = View.GONE
                        binding.tvValueToCalc.visibility = View.GONE
                        binding.pbExchangeRates.visibility = View.VISIBLE
                        binding.errorPanel.root.visibility = View.GONE
                        errorPanelHelper.dispose()
                        errorPanelHelper.hide()
                    }
                    Status.ERROR->{
                        binding.ddCurrencySelector.visibility = View.GONE
                        binding.rvExchangeRates.visibility = View.GONE
                        binding.tvValueToCalc.visibility = View.GONE
                        binding.pbExchangeRates.visibility = View.GONE
                        binding.errorPanel.root.visibility = View.VISIBLE
                        errorPanelHelper.showError(ErrorInfo(it.data?.errorBody()))
                    }
                }
            }
        }
    }

    private fun valueToCalcChanged(s: String) {
        var d = 1.0
        try {
            d = s.toDouble()
        } catch (e: Exception) {
            //ignored huehuehue
        }
        exchangeRatesAdapter.setMultiplierValue(d)
    }

    private fun setupUI() {
        binding.bSettings.setOnClickListener {
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding.bAbout.setOnClickListener {
            val intent = Intent(requireActivity(), AboutActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding.bGlobal.setOnClickListener {
            val intent = Intent(requireActivity(), GlobalActivity::class.java)
            requireActivity().startActivity(intent)
        }
        exchangeRatesAdapter = ExchangeRatesAdapter()
        binding.rvExchangeRates.layoutManager = LinearLayoutManager(context)
        binding.ddCurrencySelector.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    exchangeRatesAdapter.currencyChanged(parent?.getItemAtPosition(position).toString())
                }
            }
        binding.rvExchangeRates.adapter = exchangeRatesAdapter

    }
}