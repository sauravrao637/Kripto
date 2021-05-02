package com.camo.kripto.ui

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.liveData
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceFragmentCompat
import com.camo.kripto.R
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.utils.Resource
import com.camo.kripto.utils.Status
import kotlinx.coroutines.Dispatchers

class MarketSettingsFragment : PreferenceFragmentCompat() {
    private val TAG = MarketSettingsFragment::class.simpleName
    private var currPreference: DropDownPreference?=null
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.market_preferences, rootKey)
        currPreference = findPreference("pref_currency")

        setUpObserver()
    }

    private fun setUpObserver() {
        getSupportedCurr().observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { currencies ->setCurr(currencies) }
                    }
                    Status.ERROR -> {
                        Log.d(TAG,it.message.toString())

                    }
                    Status.LOADING -> {
                        Log.d(TAG,it.message.toString())
                    }
                }
            }
        })
    }

    private fun setCurr(currencies: ArrayList<String>) {
        Log.d(TAG,currencies.toString())
        var array = arrayOf(String())
        array = currencies.toArray(array)
        currPreference?.entries = array
        currPreference?.entryValues = array

    }

    private fun getSupportedCurr() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = CGApiHelper(RetrofitBuilder.CG_SERVICE).getSupportedCurr()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            Log.d(TAG,exception.toString())
        }
    }
}