package com.camo.kripto.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.camo.kripto.local.model.CoinIdName
import com.camo.kripto.remote.model.CoinMarket
import com.camo.kripto.remote.model.ExchangeRates
import com.camo.kripto.remote.model.Exchanges
import com.camo.kripto.remote.model.News
import com.camo.kripto.repos.Repository
import com.camo.kripto.ui.pager.CryptocurrenciesMarketCapPS
import com.camo.kripto.ui.pager.ExchangesPS
import com.camo.kripto.ui.pager.NewsPS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import com.camo.kripto.utils.Resource as ResourceUtil

@HiltViewModel
class MarketCapVM @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    private val _exchangeRates =
        MutableStateFlow<ResourceUtil<Response<ExchangeRates>>>(ResourceUtil.loading(null))
    var exchangeRates = _exchangeRates.asStateFlow()
    var intialized = false
    var arr: Array<String>? = null
    var prefCurrency: MutableLiveData<String> = MutableLiveData<String>()
    var orderby: MutableLiveData<String> = MutableLiveData<String>()
    var duration: MutableLiveData<String> = MutableLiveData<String>()
    var currentFrag: Int? = null

    init {
        getExchangeRates()
    }

    private var exchageRatesJob: Job? = null
    fun getExchangeRates() {
        exchageRatesJob?.cancel()
        exchageRatesJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.getExchangeRates().collect {
                    _exchangeRates.value = it
                }
            }
        }
    }

    fun setValues(
        prefCurr: String,
        dur: String,
        prefOrder: String
    ) {
        orderby.postValue(prefOrder)
        duration.postValue(dur)
        prefCurrency.postValue(prefCurr)
    }

    fun getMarketCap(
        currency: String?,
        order: String?,
        dur: String?,
        coins: List<CoinIdName>?
    ): Flow<PagingData<CoinMarket.CoinMarketItem>> {
        return Pager(
            PagingConfig(pageSize = 25)
        ) {
            CryptocurrenciesMarketCapPS(repo, currency, order, dur, coins)
        }.flow.cachedIn(viewModelScope)
    }

    fun getExchanges(): Flow<PagingData<Exchanges.ExchangesItem>> {
        return Pager(
            PagingConfig(pageSize = 25)
        ) {
            ExchangesPS(repo)
        }.flow.cachedIn(viewModelScope)
    }

    fun toggleDuration() {
        if (arr != null) {
            var i = arr!!.indexOf(duration.value)
            i = (i + 1) % 7
            duration.postValue(arr!![i])
        }
    }

    fun unFav(coin: CoinMarket.CoinMarketItem?) {
        if (coin == null) return
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeFavCoin(coin.id)
        }
    }

    fun getNews(category: String): Flow<PagingData<News.StatusUpdate>> {
        return Pager(
            PagingConfig(pageSize = 25)
        ) {
            NewsPS(repo.cgApiHelper,category)
        }.flow.cachedIn(viewModelScope)
    }

    suspend fun isFavCoinsEmpty(): Boolean {
        return repo.getFavCoins().isEmpty()
    }
}
