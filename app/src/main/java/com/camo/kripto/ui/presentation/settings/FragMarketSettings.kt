package com.camo.kripto.ui.presentation.settings

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceFragmentCompat
import com.camo.kripto.R
import com.camo.kripto.local.model.Currency
import com.camo.kripto.repos.Repository
import com.camo.kripto.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FragMarketSettings : PreferenceFragmentCompat() {

    @Inject lateinit var repo : Repository

    private var currPreference: DropDownPreference? = null
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.market_preferences, rootKey)
        currPreference = findPreference("pref_currency")
        currPreference?.isVisible = false
        getCurrencies()
    }

    private fun setCurr(array: Array<String>) {

        if (array.isNotEmpty()) {
            currPreference?.entries = array
            currPreference?.entryValues = array
            currPreference?.isVisible = true
        } else {
            Timber.d( "Empty curr array")
        }
    }

    private var loadCurrJob: Job? = null
    private fun getCurrencies() {
        loadCurrJob?.cancel()
        loadCurrJob = lifecycleScope.launch {
            val curr = ArrayList<Currency>()

            withContext(Dispatchers.IO) { repo.getCurrencies() }.let {
                curr.addAll(it)
//                if (curr.isEmpty()) {
//                    Timber.d("Room db empty :(")
//                    val res = repo.lIRcurrencies()
//                    when (res.status) {
//                        Status.SUCCESS -> getCurrencies()
//                        Status.ERROR -> Toast.makeText(context, res.message, Toast.LENGTH_LONG)
//                            .show()
//
//                        else -> Toast.makeText(
//                            context,
//                            "this wwasn't expected at all",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
            }
            val list = ArrayList<String>()
            withContext(Dispatchers.IO) {
                for (c in curr) {
                    list.add(c.id)
                }

                setCurr(list.toTypedArray())
            }
        }
    }
}