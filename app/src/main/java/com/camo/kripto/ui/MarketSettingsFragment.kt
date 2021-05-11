package com.camo.kripto.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceFragmentCompat
import com.camo.kripto.R
import com.camo.kripto.database.AppDb
import com.camo.kripto.database.model.Currency
import com.camo.kripto.database.repository.AppDbRepo
import com.camo.kripto.utils.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MarketSettingsFragment : PreferenceFragmentCompat() {



    private var repo: AppDbRepo? = null
    private var currPreference: DropDownPreference? = null
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.market_preferences, rootKey)
        currPreference = findPreference("pref_currency")
        repo = context?.let { AppDb.getAppDb(it)?.let { AppDbRepo(it) } }
        currPreference?.isVisible = false
        getCurrencies()
    }

    private fun setCurr(array: Array<String>) {

        if (array.isNotEmpty()) {
            currPreference?.entries = array
            currPreference?.entryValues = array
            currPreference?.isVisible = true
        } else {
//            TODO error handling
            Timber.d( "Empty curr array")
        }
    }


    var loadCurrJob: Job? = null
    fun getCurrencies() {
        loadCurrJob?.cancel()
        loadCurrJob = lifecycleScope.launch {
            val curr = ArrayList<Currency>()

            withContext(Dispatchers.IO) { repo?.getCurrencies() }?.let {
                curr.addAll(it)
                if (curr.isEmpty()) {
                    val res = AppDbRepo.lIRcurrencies(repo)
                    when (res.status) {
                        Status.SUCCESS -> getCurrencies()
                        Status.ERROR -> Toast.makeText(context, res.message, Toast.LENGTH_LONG)
                            .show()

                        else -> Toast.makeText(
                            context,
                            "this wwasn't expected at all",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
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