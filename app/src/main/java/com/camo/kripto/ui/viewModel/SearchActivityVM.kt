package com.camo.kripto.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camo.kripto.local.model.Coin
import com.camo.kripto.remote.model.Trending
import com.camo.kripto.repos.Repository
import com.camo.kripto.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchActivityVM @Inject constructor(val repository: Repository) : ViewModel() {
    private val _trending = MutableStateFlow<Resource<Response<Trending>>>(Resource.loading(null))
    val trending = _trending.asStateFlow()
    private val _searchCoinsList =
        MutableStateFlow<Resource<List<Coin>>>(Resource.loading(null))
    val searchCoinList = _searchCoinsList.asStateFlow()

    init {
        getTrending()
        searchStringChanged("")
    }

    var trendingJob: Job? = null
    fun getTrending() {
        trendingJob?.cancel()
        _trending.value = Resource.loading(data = null)
        trendingJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.getTrending().collect {
                    _trending.value = it
                }
            }
        }
    }

    var filterJob: Job? = null
    fun searchStringChanged(newText: String?) {
        filterJob?.cancel()
        _searchCoinsList.value = Resource.loading(null)
        filterJob = viewModelScope.launch {
            delay(100)
            withContext(Dispatchers.IO) {
                val list = repository.getCoinFilterByName(newText ?: "")
                _searchCoinsList.value = Resource.success(list)
            }
        }
    }

    suspend fun getCount(id: String): Int {
        return repository.coinCountByID(id)
    }
}