package com.camo.kripto.ui.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.camo.kripto.Constants.MARKET_CAP_CHART_KEY
import com.camo.kripto.Constants.PRICE_CHART_KEY
import com.camo.kripto.Constants.TRADING_VOL_CHART_KEY
import com.camo.kripto.error.ErrorInfo
import com.camo.kripto.error.ErrorCause
import com.camo.kripto.local.model.Currency
import com.camo.kripto.local.model.FavCoin
import com.camo.kripto.local.model.PriceAlert
import com.camo.kripto.remote.model.CoinCD
import com.camo.kripto.remote.model.MarketChart
import com.camo.kripto.repos.Repository
import com.camo.kripto.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CoinActivityVM @Inject constructor(
    private val cgRepo: Repository,
    sharedPreferences: SharedPreferences
) : ViewModel() {
    private var id: String? = null
    private var name: String? = null

    private val _coinDataState = MutableStateFlow<Resource<CoinCD>>(Resource.loading(null))
    val coinData = _coinDataState.asStateFlow()

    private val _selectedChart = MutableStateFlow(PRICE_CHART_KEY)
    val selectedChart = _selectedChart.asStateFlow()

    private val _duration = MutableStateFlow("24h")
    val duration = _duration.asStateFlow()

    private val _currency =
        MutableStateFlow(sharedPreferences.getString("pref_currency", "inr") ?: "inr")
    val currency = _currency.asStateFlow()

    private val _supportedCurrencies =
        MutableStateFlow<Resource<List<String>>>(Resource.loading(null))
    val supportedCurrencies = _supportedCurrencies.asStateFlow()

    private val _isFav = MutableStateFlow<Boolean?>(null)
    val isFav = _isFav.asStateFlow()

    private val _marketChart = MutableStateFlow<Resource<MarketChart>>(Resource.loading(null))
    val marketChart = _marketChart.asStateFlow()

    ///////////////////////////////////////////////////////////////////////////////////////////////
//  Price Alerts
///////////////////////////////////////////////////////////////////////////////////////////////////
    private val _priceAlerts = MutableStateFlow<List<PriceAlert>?>(null)
    val priceAlerts = _priceAlerts.asStateFlow()
    private val _showAllAlerts = MutableStateFlow(true)
    val showAllAlerts = _showAllAlerts.asStateFlow()
    private val _showEnabledAlerts = MutableStateFlow(false)
    val showEnabledOnly = _showEnabledAlerts.asStateFlow()
    val alertFilterState = combine(_showAllAlerts, _showEnabledAlerts) { a, b ->
        AlertFilterState(a, b)
    }
    private val _coinDurCurrState = combine(_duration, _currency) { b, c ->
        CurrentState(b, c)
    }

    class CurrentState(val dur: String, val curr: String)

    val coinDurCurrState = _coinDurCurrState.asLiveData()

    init {
        getSupportedCurr()
        getPriceAlerts()
    }

    private var updateChartJob: Job? = null
    fun updateChart() {
        val curr = currency.value
        val days = duration.value
        Timber.d("%s %s %s", curr, id, days)
        _marketChart.value = Resource.loading(data = null)
        if (id == null) {
            _marketChart.value =
                Resource.error(data = null, ErrorInfo(null, ErrorCause.GET_MARKET_CHART))
            return
        }
        updateChartJob?.cancel()
        updateChartJob = viewModelScope.launch(Dispatchers.IO) {
            cgRepo.getMarketChart(id!!, curr, days).collect {
                _marketChart.value = it
            }
        }
    }

    private var getSupportedCurrJob: Job? = null
    private fun getSupportedCurr() {
        _supportedCurrencies.value = Resource.loading(data = null)
        getSupportedCurrJob = viewModelScope.launch(Dispatchers.IO) {
            val curr = ArrayList<Currency>()
            var addList = ArrayList<String>()
            curr.addAll(cgRepo.getCurrencies())
            if (curr.isEmpty()) {
                try {
                    addList = cgRepo.getSupportedCurr()
                } catch (e: Exception) {
                    _supportedCurrencies.value =
                        Resource.error(null, ErrorInfo(e, ErrorCause.GET_SUPPORTED_CURRENCIES))
                }
            } else {
                for (s in curr) {
                    addList.add(s.id)
                }
            }
            _supportedCurrencies.value = Resource.success(data = addList)
        }
    }

    private var crytpoCapDataJob: Job? = null
    fun getCryptoCapData() {
        crytpoCapDataJob?.cancel()
        _coinDataState.value = Resource.loading(null)
        if (id != null) {
            crytpoCapDataJob = viewModelScope.launch(Dispatchers.IO) {
                try {
                    _coinDataState.value = Resource.success(data = cgRepo.getCurrentData(id!!))
                } catch (e: Exception) {
                    Timber.d(e)
                    _coinDataState.value = Resource.error(
                        data = null,
                        ErrorInfo(e, ErrorCause.GET_CRYPTO_MARKETA_DATA)
                    )
                }
            }
        } else {
            _coinDataState.value = Resource.error(
                data = null,
                ErrorInfo(null, ErrorCause.GET_CRYPTO_MARKETA_DATA)
            )
        }
    }

    private var toggleFavJob: Job? = null
    fun toggleFav() {
        toggleFavJob?.cancel()
        val idTemp = this.id
        val nameTemp = this.name
        if (id != null && name != null) {
            var count: Int
            toggleFavJob = viewModelScope.launch(Dispatchers.IO) {
                count = cgRepo.coinCountByID(idTemp!!)
                if (count == 0) {
                    cgRepo.addFavCoin(FavCoin(idTemp, nameTemp!!))
                    _isFav.value = true
                } else {
                    cgRepo.removeFavCoin(nameTemp!!)
                    _isFav.value = false
                }
            }
        }
    }

    fun durationChanged(text: String) {
        _duration.value = text
    }

    fun currencyChanged(s: String) {
        _currency.value = s
    }

    fun setIdName(id: String, name: String) {
        if (this.id != null && this.name != null) return
        this.id = id
        this.name = name
        getCryptoCapData()
        viewModelScope.launch {
            val count = cgRepo.coinCountByID(id)
            _isFav.value = (count > 0)
        }
    }

    fun getId(): String {
        return this.id ?: "Kripto"
    }

    fun togglePriceAlert(l: Long, boolean: Boolean) {
        viewModelScope.launch {
            cgRepo.setPriceAlertEnabled(l, boolean)
        }
    }

    fun deletePriceAlert(l: Long) {
        viewModelScope.launch {
            cgRepo.removePriceAlert(l)
        }
    }

    fun getPriceAlerts() {
        viewModelScope.launch {
            cgRepo.getPriceAlerts().collect {
                _priceAlerts.value = it
            }
        }
    }

    fun getName(): String? {
        return this.name
    }

    fun setChecked(checked: Boolean) {
        _showAllAlerts.value = checked
    }

    fun setShowEnabledAlertsOnly(checked: Boolean) {
        _showEnabledAlerts.value = checked
    }

    fun setSelectedChart(i: Int) {
        when (i) {
            0 -> _selectedChart.value = PRICE_CHART_KEY
            1 -> _selectedChart.value = MARKET_CAP_CHART_KEY
            else -> _selectedChart.value = TRADING_VOL_CHART_KEY
        }
    }
}

class AlertFilterState(val showAll: Boolean, val showEnabledOnly: Boolean)
