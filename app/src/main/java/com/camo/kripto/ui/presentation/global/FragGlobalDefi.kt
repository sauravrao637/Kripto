package com.camo.kripto.ui.presentation.global

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.camo.kripto.databinding.FragGlobalDefiBinding
import com.camo.kripto.error.ErrorInfo
import com.camo.kripto.error.ErrorPanelHelper
import com.camo.kripto.remote.model.GlobalDefi
import com.camo.kripto.ui.viewModel.GlobalVM
import com.camo.kripto.utils.Extras
import com.camo.kripto.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.math.BigDecimal

@AndroidEntryPoint
class FragGlobalDefi : Fragment() {

    private lateinit var binding: FragGlobalDefiBinding
    private val viewModel by activityViewModels<GlobalVM>()
    private lateinit var errorPanelHelper: ErrorPanelHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragGlobalDefiBinding.inflate(LayoutInflater.from(context), container, false)
        errorPanelHelper = ErrorPanelHelper(binding.root,::refresh)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.globalDefi.collectLatest {
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
                            binding.pbFragGlobalDefi.visibility = View.GONE
                            binding.groupFragGlobalDefi.visibility = View.VISIBLE
                            binding.errorPanel.root.visibility = View.GONE
                            updateView(it.data)
                            errorPanelHelper.hide()
                            errorPanelHelper.dispose()
                        } else {
                            showErrorUI(it.errorInfo)
                            Timber.d("null Defi data on success!!")
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
        binding.pbFragGlobalDefi.visibility = View.VISIBLE
        binding.groupFragGlobalDefi.visibility = View.GONE
        binding.errorPanel.root.visibility = View.GONE
    }

    private fun showErrorUI(errorInfo: ErrorInfo?) {
        errorPanelHelper.showError(errorInfo)
        binding.pbFragGlobalDefi.visibility = View.GONE
        binding.groupFragGlobalDefi.visibility = View.GONE
        binding.errorPanel.root.visibility = View.VISIBLE
    }

    private fun refresh() {
        viewModel.getGlobalDefi()
    }

    private fun updateView(globalDefi: GlobalDefi) {
        binding.tvFragGlobalDefiMarketCap.text =
            Extras.getFormattedDoubleCurr(BigDecimal(globalDefi.data.defi_market_cap), "usd")
        binding.tvFragGlobalDefiEthMarketCap.text =
            Extras.getFormattedDoubleCurr(BigDecimal(globalDefi.data.eth_market_cap), "usd")
        binding.tvFragGlobalDefiDefitoeth.text =
            Extras.getFormattedDouble(BigDecimal(globalDefi.data.defi_to_eth_ratio))
        binding.tvFragGlobalDefiTv24h.text =
            Extras.getFormattedDoubleCurr(BigDecimal(globalDefi.data.trading_volume_24h), "usd")
        binding.tvFragGlobalDefiDefidominance.text =
            Extras.getFormattedDouble(BigDecimal(globalDefi.data.defi_dominance))
        binding.tvFragGlobalDefiTopcoin.text = globalDefi.data.top_coin_name
        binding.tvFragGlobalDefiTopcoindom.text =
            Extras.getFormattedDouble(globalDefi.data.top_coin_defi_dominance)
    }
}