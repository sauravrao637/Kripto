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
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GlobalVM @Inject constructor(
    private val cgRepo: Repository,
    sharedPreferences: SharedPreferences
) : ViewModel() {
    var initialized = false
    private var _globalCrypto: MutableLiveData<Resource<Global>> = MutableLiveData()
    val globalCrypto = _globalCrypto
    private var _globalDefi: MutableLiveData<Resource<GlobalDefi>> = MutableLiveData()
    val globalDefi = _globalDefi
    private var _prefCurrency: MutableLiveData<String> = MutableLiveData<String>()
    val prefCurrency = _prefCurrency

    init {
        setValues(sharedPreferences.getString("pref_currency", "inr") ?: "inr")
        getGlobal()
        getGlobalDefi()
    }

    //    var globalDefi: MutableLiveData<Resource<GlobalDefi>> = MutableLiveData()
    private fun setValues(curr: String) {
        _prefCurrency.postValue(curr)
    }

    private var getGlobalJob: Job? = null
    fun getGlobal() {
        _globalCrypto.postValue(Resource.loading(data = null))
        try {
            getGlobalJob?.cancel()
            getGlobalJob = viewModelScope.launch(Dispatchers.IO) {
                Timber.d("called")
                _globalCrypto.postValue(Resource.success(data = cgRepo.getGlobal()))
            }
        } catch (e: Exception) {
            Timber.d(e)
            _globalCrypto.postValue(
                Resource.error(
                    data = null,
                    ErrorInfo(e,ErrorCause.GET_GLOBAL_DATA)
                )
            )
        }
    }

    private var getGlobalDefiJob: Job? = null
    fun getGlobalDefi() {
        _globalDefi.postValue(Resource.loading(data = null))
        try {
            getGlobalDefiJob?.cancel()
            getGlobalDefiJob = viewModelScope.launch(Dispatchers.IO) {
                _globalDefi.postValue(Resource.success(data = cgRepo.getGlobalDefi()))
            }
        } catch (e: Exception) {
            Timber.d(e)
            _globalDefi.postValue(
                Resource.error(
                    data = null,
                    ErrorInfo(e,ErrorCause.GET_GLOBAL_DEFI_DATA)
                )
            )
        }
    }
}