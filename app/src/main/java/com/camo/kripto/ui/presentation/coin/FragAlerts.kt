package com.camo.kripto.ui.presentation.coin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.camo.kripto.databinding.FragAlertsBinding
import com.camo.kripto.local.model.PriceAlert
import com.camo.kripto.ui.adapter.AlertAdapter
import com.camo.kripto.ui.presentation.PriceAlertActivity
import com.camo.kripto.ui.viewModel.CoinActivityVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import timber.log.Timber


class FragAlerts : Fragment(), AlertAdapter.OnAlertItemListener {
    private lateinit var binding: FragAlertsBinding
    private lateinit var alertAdapter: AlertAdapter
    private val viewModel by activityViewModels<CoinActivityVM>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("calledOnCreateView")
        binding = FragAlertsBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        Timber.d("calledSetupObservers")
        lifecycleScope.launchWhenResumed {
            viewModel.priceAlerts.collectLatest {
                withContext(Dispatchers.Main) {
                    resetAlertData()
                    binding.emptyList.visibility =
                        if (it == null || it.isEmpty()) View.VISIBLE else View.GONE
                    binding.root.isRefreshing = false
                }
            }
        }
        binding.btAddPriceAlert.setOnClickListener {
            val intent = Intent(requireActivity(), PriceAlertActivity::class.java)
            intent.putExtra(CoinIdNameKeys.COIN_NAME_KEY, viewModel.getName())
            intent.putExtra(CoinIdNameKeys.COIN_ID_KEY, viewModel.getId())
            requireActivity().startActivity(intent)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.alertFilterState.collectLatest {
                resetAlertData()
            }
        }
    }

    private var filterJob: Job? = null
    private fun resetAlertData() {
        val list = viewModel.priceAlerts.value
        if (list == null) {
            alertAdapter.setData(list)
            return
        }
        val showAll = viewModel.showAllAlerts.value
        val showEnabled = viewModel.showEnabledOnly.value
        filterJob?.cancel()
        filterJob = lifecycleScope.launchWhenStarted {
            val listToReturn = mutableListOf<PriceAlert>()
            for (each in list) {
                if (showAll || each.id == viewModel.getId()) {
                    if (!showEnabled || each.enabled) {
                        listToReturn.add(each)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                alertAdapter.setData(listToReturn)
            }
        }
    }

    private fun setupUI() {
        Timber.d("calledSetupUI")
        binding.rvAlerts.layoutManager = LinearLayoutManager(context)
        alertAdapter = AlertAdapter(this)
        binding.rvAlerts.adapter = alertAdapter
        binding.root.setOnRefreshListener {
            refresh()
        }
        binding.chipAll.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setChecked(isChecked)
        }
        binding.chipEnabled.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setShowEnabledAlertsOnly(isChecked)
        }
        binding.rvAlerts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) binding.btAddPriceAlert.hide() else if (dy < 0) binding.btAddPriceAlert.show()
            }
        })
    }

    private fun refresh() {
        Timber.d("calledRefresh")
        viewModel.getPriceAlerts()
    }

    override fun onAlertToggle(position: Int, boolean: Boolean) {
        Timber.d("calledOnAlertToggle")
        val alert = alertAdapter.getItem(position) ?: return
        if (alert.enabled != boolean) viewModel.togglePriceAlert(alert.primaryKey, boolean)
    }

    override fun onAlertEdit(position: Int) {
        Timber.d("calledOnAlertEdit")
        val alert = alertAdapter.getItem(position) ?: return
        //TODO
    }

    override fun deleteAlert(position: Int) {
        Timber.d("calledDeleteAlert")
        val alert = alertAdapter.getItem(position) ?: return
        viewModel.deletePriceAlert(alert.primaryKey)
    }
}