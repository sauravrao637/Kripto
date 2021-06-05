package com.camo.kripto.ui.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.camo.kripto.error.ErrorInfo
import com.camo.kripto.error.ErrorCause
import com.camo.kripto.local.model.Currency
import com.camo.kripto.local.model.FavCoin
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

    private val _coinDataState = MutableStateFlow<Resource<CoinCD>>(Resource.loading(null))
    val coinData = _coinDataState.asStateFlow()

    private val _duration = MutableStateFlow("24h")
    val duration = _duration.asStateFlow()

    private val _currency =
        MutableStateFlow(sharedPreferences.getString("pref_currency", "inr") ?: "inr")
    val currency = _currency.asStateFlow()

    private val _supportedCurrencies = MutableLiveData<Resource<List<String>>>()
    val supportedCurrencies: LiveData<Resource<List<String>>> get() = _supportedCurrencies

    private val _isFav = MutableStateFlow<Boolean?>(null)
    val isFav = _isFav.asStateFlow()

    private val _marketChart = MutableStateFlow<Resource<MarketChart>>(Resource.loading(null))
    val marketChart = _marketChart.asStateFlow()

    private val _coinDurCurrState = combine(_duration, _currency) { b, c ->
        CurrentState(b, c)
    }

    class CurrentState(val dur: String, val curr: String)

    val coinDurCurrState = _coinDurCurrState.asLiveData()

    init {
        getSupportedCurr()
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
        _supportedCurrencies.postValue(Resource.loading(data = null))
        getSupportedCurrJob = viewModelScope.launch(Dispatchers.IO) {
            val curr = ArrayList<Currency>()
            var addList = ArrayList<String>()
            curr.addAll(cgRepo.getCurrencies())
            if (curr.isEmpty()) {
                try {
                    addList = cgRepo.getSupportedCurr()
                } catch (e: Exception) {
                    _supportedCurrencies.postValue(
                        Resource.error(
                            null,
                            ErrorInfo(e,ErrorCause.GET_SUPPORTED_CURRENCIES)
                        )
                    )
                }
            } else {
                for (s in curr) {
                    addList.add(s.id)
                }
            }
            _supportedCurrencies.postValue(
                Resource.success(data = addList)
            )
        }
    }

    private var crytpoCapDataJob: Job? = null
    fun getCryptoCapData() {
        crytpoCapDataJob?.cancel()
        if (id != null) {
            crytpoCapDataJob = viewModelScope.launch(Dispatchers.IO) {
                try {
                    _coinDataState.value = Resource.success(data = cgRepo.getCurrentData(id!!))
                } catch (e: Exception) {
                    Timber.d(e)
                    _coinDataState.value = Resource.error(
                        data = null,
                        ErrorInfo(e, ErrorCause.GET_CRPTO_MARKETCAP_DATA)
                    )
                }
            }
        } else {
            _coinDataState.value = Resource.error(
                data = null,
                ErrorInfo(null,ErrorCause.GET_CRPTO_MARKETCAP_DATA)
            )
        }
    }

    private var toggleFavJob: Job? = null
    fun toggleFav(id: String, name: String) {
        toggleFavJob?.cancel()
        var count: Int
        toggleFavJob = viewModelScope.launch(Dispatchers.IO) {
            count = cgRepo.coinCountByID(id)
            if (count == 0) {
                cgRepo.addFavCoin(FavCoin(id, name))
                _isFav.value = true
            } else {
                cgRepo.removeFavCoin(id)
                _isFav.value = false
            }
        }
    }

    fun durationChanged(text: String) {
        _duration.value = text
    }

    fun currencyChanged(s: String) {
        _currency.value = s
    }

    fun setID(id: String?) {
        this.id = id
        if (id != null) {
            viewModelScope.launch {
                val count = cgRepo.coinCountByID(id)
                _isFav.value = (count > 0)
            }
        }
    }
}
