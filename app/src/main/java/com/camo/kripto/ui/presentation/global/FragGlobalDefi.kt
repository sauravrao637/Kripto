package com.camo.kripto.ui.presentation.global

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.camo.kripto.remote.model.GlobalDefi
import com.camo.kripto.databinding.FragGlobalDefiBinding
import com.camo.kripto.ui.viewModel.GlobalVM
import com.camo.kripto.utils.Extras
import com.camo.kripto.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
@AndroidEntryPoint
class FragGlobalDefi : Fragment() {


    private lateinit var binding: FragGlobalDefiBinding
    private val viewModel by activityViewModels<GlobalVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragGlobalDefiBinding.inflate(LayoutInflater.from(context), container, false)
        binding.root.visibility = View.VISIBLE

        setupVM()
        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupVM() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val curr = sharedPreferences.getString("pref_currency", "inr") ?: "inr"

    }

    private fun setupUI() {
        getGlobalDefi()
    }

    private fun setupObservers() {
        viewModel.globalDefi.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.ERROR -> {
                    binding.pbFragGlobalDefi.visibility = View.GONE
                    Timber.d(it.message ?: "some error")
                }
                Status.LOADING -> {
                    binding.pbFragGlobalDefi.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.pbFragGlobalDefi.visibility = View.GONE
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

    private fun refresh() {
        getGlobalDefi()
    }

    private fun updateView(globalDefi: GlobalDefi) {
        binding.tvFragGlobalDefiMarketCap.text = Extras.getFormattedDoubleCurr(globalDefi.data.defi_market_cap.toDouble(),"usd")
        binding.tvFragGlobalDefiEthMarketCap.text = Extras.getFormattedDoubleCurr(globalDefi.data.eth_market_cap.toDouble(),"usd")
        binding.tvFragGlobalDefiDefitoeth.text = Extras.getFormattedDouble(globalDefi.data.defi_to_eth_ratio.toDouble())
        binding.tvFragGlobalDefiTv24h.text = Extras.getFormattedDoubleCurr(globalDefi.data.trading_volume_24h.toDouble(),"usd")
        binding.tvFragGlobalDefiDefidominance.text = Extras.getFormattedDouble(globalDefi.data.defi_dominance.toDouble())
        binding.tvFragGlobalDefiTopcoin.text = globalDefi.data.top_coin_name
        binding.tvFragGlobalDefiTopcoindom.text =
            Extras.getFormattedDouble(globalDefi.data.top_coin_defi_dominance)

    }


    private var globalDefiJob: Job? = null
    private fun getGlobalDefi() {
        globalDefiJob?.cancel()
        globalDefiJob = lifecycleScope.launch {
            viewModel.getGlobalDefi().collect {
                viewModel.globalDefi.postValue(it)
            }
        }
    }


}