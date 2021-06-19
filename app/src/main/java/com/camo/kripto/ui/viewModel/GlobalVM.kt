package com.camo.kripto.ui.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camo.kripto.error.ErrorInfo
import com.camo.kripto.error.ErrorCause
import com.camo.kripto.remote.model.Global
import com.camo.kripto.remote.model.GlobalDefi
import com.camo.kripto.repos.Repository
import com.camo.kripto.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GlobalVM @Inject constructor(
    private val cgRepo: Repository,
    sharedPreferences: SharedPreferences
) : ViewModel() {
    var initialized = false
    private val _globalCrypto = MutableStateFlow<Resource<Global>>(Resource.loading(null))
    val globalCrypto = _globalCrypto.asStateFlow()
    private val _globalDefi = MutableStateFlow<Resource<GlobalDefi>>(Resource.loading(null))
    val globalDefi = _globalDefi.asStateFlow()
    private val _prefCurrency = MutableStateFlow("inr")
    val prefCurrency = _prefCurrency.asStateFlow()

    init {
        setValues(sharedPreferences.getString("pref_currency", "inr") ?: "inr")
        getGlobal()
        getGlobalDefi()
    }

    private fun setValues(curr: String) {
        Timber.d("setting curr $curr")
        _prefCurrency.value = curr
    }

    private var getGlobalJob: Job? = null
    fun getGlobal() {
        Timber.d("getting global")
        getGlobalJob?.cancel()
        _globalCrypto.value = Resource.loading(data = null)
        getGlobalJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = cgRepo.getGlobal()
                Timber.d(res.toString())
                _globalCrypto.value = Resource.success(data = res)
            } catch (e: Exception) {
                Timber.d(e)
                _globalCrypto.value =
                    Resource.error(data = null, ErrorInfo(e, ErrorCause.GET_GLOBAL_DATA))
            }
        }
    }

    private var getGlobalDefiJob: Job? = null
    fun getGlobalDefi() {
        Timber.d("getting global defi")
        _globalDefi.value = Resource.loading(data = null)
        getGlobalDefiJob?.cancel()
        getGlobalDefiJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = cgRepo.getGlobalDefi()
                Timber.d(res.toString())
                _globalDefi.value = Resource.success(data = res)
            } catch (e: Exception) {
                Timber.d(e)
                _globalDefi.value =
                    Resource.error(data = null, ErrorInfo(e, ErrorCause.GET_GLOBAL_DEFI_DATA))
            }
        }
    }
}