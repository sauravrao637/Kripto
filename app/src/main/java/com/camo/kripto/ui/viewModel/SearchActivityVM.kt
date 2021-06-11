package com.camo.kripto.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camo.kripto.error.ErrorCause
import com.camo.kripto.error.ErrorInfo
import com.camo.kripto.remote.model.Trending
import com.camo.kripto.repos.Repository
import com.camo.kripto.utils.Resource
import com.camo.kripto.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchActivityVM @Inject constructor(val repository: Repository) : ViewModel() {
    private val _trending = MutableStateFlow<Resource<Response<Trending>>>(Resource.loading(null))
    val trending = _trending.asStateFlow()
    init {
        getTrending()
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

}