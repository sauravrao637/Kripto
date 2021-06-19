package com.camo.kripto.ui.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camo.kripto.error.ErrorCause
import com.camo.kripto.error.ErrorInfo
import com.camo.kripto.local.model.Currency
import com.camo.kripto.repos.Repository
import com.camo.kripto.ui.presentation.PriceAlertActivity
import com.camo.kripto.utils.Resource
import com.camo.kripto.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import retrofit2.Response
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PriceAlertActivityVM @Inject constructor(
    val repository: Repository,
    val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _currency =
        MutableStateFlow(sharedPreferences.getString("pref_currency", "inr") ?: "inr")
    val currency = _currency.asStateFlow()
    private val _currentPriceAllCurr =
        MutableStateFlow<Resource<Response<Map<String, Map<String, BigDecimal>>>>>(
            Resource.loading(null)
        )
    val currentPriceAllCurr = _currentPriceAllCurr.asStateFlow()
    private val _currentPriceInCurr = MutableStateFlow<BigDecimal>(BigDecimal(0))
    val currentPriceInCurr = _currentPriceInCurr.asStateFlow()
    private val _supportedCurrencies =
        MutableStateFlow<Resource<List<String>>>(Resource.loading(null))
    val supportedCurrencies = _supportedCurrencies.asStateFlow()
    private val _lessThan = MutableStateFlow("")
    private val _moreThan = MutableStateFlow("")
    private val _isInputValid = MutableStateFlow(false)
    val isInputValid = _isInputValid.asStateFlow()
    private val _inputState =
        combine(_lessThan, _moreThan, _currency, currentPriceInCurr) { a, b, c, d ->
            InputState(a, b, c, d)
        }

    class InputState(
        val lessThan: String,
        val moreThan: String,
        val currency: String,
        val currPriceInCurr: BigDecimal
    )

    private var id: String? = null
    private var name: String? = null

    init {
        getSupportedCurr()
        inputValidation()
    }

    private var validationJob: Job? = null
    private fun inputValidation() {
        validationJob?.cancel()
        validationJob = viewModelScope.launch {
            _inputState.collectLatest {
                val lessT: BigDecimal
                val moreT: BigDecimal
                try {
                    if (it.lessThan.isEmpty() && it.moreThan.isEmpty()) {
                        _isInputValid.value = false
                    } else if (it.lessThan.isEmpty()) {
                        moreT = BigDecimal(_moreThan.value)
                        _isInputValid.value = moreT > it.currPriceInCurr
                    } else if (it.moreThan.isEmpty()) {
                        lessT = BigDecimal(it.lessThan)
                        _isInputValid.value = lessT < it.currPriceInCurr
                    } else {
                        lessT = BigDecimal(it.lessThan)
                        moreT = BigDecimal(it.moreThan)
                        _isInputValid.value =
                            lessT < it.currPriceInCurr && moreT > it.currPriceInCurr
                    }
                } catch (e: java.lang.Exception) {
                    _isInputValid.value = false
                }
            }
        }
    }

    private var getSupportedCurrJob: Job? = null
    private fun getSupportedCurr() {
        _supportedCurrencies.value = Resource.loading(data = null)
        getSupportedCurrJob = viewModelScope.launch(Dispatchers.IO) {
            val curr = ArrayList<Currency>()
            var addList = ArrayList<String>()
            curr.addAll(repository.getCurrencies())
            if (curr.isEmpty()) {
                try {
                    addList = repository.getSupportedCurr()
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

    fun setIdName(id: String, name: String) {
        this.id = id
        this.name = name
        getPrice()
    }

    var getPriceJob: Job? = null
    fun getPrice() {
        getPriceJob?.cancel()
        getPriceJob = viewModelScope.launch(Dispatchers.IO) {
            id?.let {
                name?.let { it1 ->
                    repository.getSimplePrice(it, it1).collectLatest {
                        _currentPriceAllCurr.value = it
                        updateCurrPrice()
                    }
                }
            }
        }
    }

    fun currencyChanged(s: String) {
        _currency.value = s
        updateCurrPrice()
    }

    fun updateCurrPrice() {
        val price = _currentPriceAllCurr.value.data?.body()?.get(id)?.get(_currency.value) ?: return
        _currentPriceInCurr.value = price
    }

    suspend fun insertPriceAlert(lessThan: String, moreThan: String, isTriggerOnceOnly: Boolean) {
        val id = this.id
        val name = this.name
        val curr = this.currency.value
        if (name == null || id == null) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPriceAlert(
                id = id,
                name = name,
                curr = curr,
                lessThan = lessThan,
                moreThan = moreThan,
                isTriggerOnceOnly = isTriggerOnceOnly
            )
        }
    }

    fun setMoreThan(toString: String) {
        _moreThan.value = toString
    }

    fun setLessThan(toString: String) {
        _lessThan.value = toString
    }
}