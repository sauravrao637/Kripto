package com.camo.kripto.ui.presentation.global

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.camo.kripto.databinding.FragGlobalDefiBinding
import com.camo.kripto.remote.model.GlobalDefi
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.globalDefi.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.ERROR -> {
                    showErrorUI()
                    Timber.d(it.message ?: "some error")
                }
                Status.LOADING -> {
                    showLoadingUI()
                }
                Status.SUCCESS -> {
                    if (it.data != null) {
                        binding.pbFragGlobalDefi.visibility = View.GONE
                        binding.groupFragGlobalDefi.visibility = View.VISIBLE
                        binding.errorPanel.root.visibility = View.GONE
                        updateView(it.data)
                    } else {
                        showErrorUI()
                        Timber.d("null Defi data on success!!")
                    }
                }
            }
        })

        binding.root.setOnRefreshListener {
            refresh()
            binding.root.isRefreshing = false
        }
    }

    private fun showLoadingUI() {
        binding.pbFragGlobalDefi.visibility = View.VISIBLE
        binding.groupFragGlobalDefi.visibility = View.GONE
        binding.errorPanel.root.visibility = View.GONE
    }

    private fun showErrorUI() {
        binding.pbFragGlobalDefi.visibility = View.GONE
        binding.groupFragGlobalDefi.visibility = View.GONE
        binding.errorPanel.root.visibility = View.VISIBLE
        //TODO show error using errorPanelHelper
    }

    private fun refresh() {
        viewModel.getGlobalDefi()
    }

    private fun updateView(globalDefi: GlobalDefi) {
        binding.tvFragGlobalDefiMarketCap.text =
            Extras.getFormattedDoubleCurr(globalDefi.data.defi_market_cap.toDouble(), "usd")
        binding.tvFragGlobalDefiEthMarketCap.text =
            Extras.getFormattedDoubleCurr(globalDefi.data.eth_market_cap.toDouble(), "usd")
        binding.tvFragGlobalDefiDefitoeth.text =
            Extras.getFormattedDouble(globalDefi.data.defi_to_eth_ratio.toDouble())
        binding.tvFragGlobalDefiTv24h.text =
            Extras.getFormattedDoubleCurr(globalDefi.data.trading_volume_24h.toDouble(), "usd")
        binding.tvFragGlobalDefiDefidominance.text =
            Extras.getFormattedDouble(globalDefi.data.defi_dominance.toDouble())
        binding.tvFragGlobalDefiTopcoin.text = globalDefi.data.top_coin_name
        binding.tvFragGlobalDefiTopcoindom.text =
            Extras.getFormattedDouble(globalDefi.data.top_coin_defi_dominance)
    }
}