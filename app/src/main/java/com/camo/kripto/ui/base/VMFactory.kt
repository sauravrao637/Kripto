package com.camo.kripto.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.repository.CGRepo
import com.camo.kripto.database.repository.AppDbRepo
import com.camo.kripto.ui.GlobalActivity
import com.camo.kripto.ui.viewModel.CoinActivityVM
import com.camo.kripto.ui.viewModel.GlobalVM
import com.camo.kripto.ui.viewModel.MarketCapVM
import timber.log.Timber

class VMFactory(
    private val cgApiHelper: CGApiHelper,
    private val appDbRepo: AppDbRepo? = null,
    private val curr: String? = null,
    private val duration: String? = null,
    private val prefOrder: String? = null
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        Timber.d(modelClass.simpleName)

        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(CoinActivityVM::class.java)) {
            if (appDbRepo == null||curr==null) {
                Timber.d("CoinActivityVM requires more parameter")
                throw IllegalArgumentException("missing params")

            } else return CoinActivityVM(
                CGRepo(cgApiHelper), appDbRepo,curr
            ) as T
        } else if (modelClass.isAssignableFrom(MarketCapVM::class.java)) {
            if(curr == null|| duration == null||prefOrder == null){
                Timber.d("MarketCapVM requires more parameter")
                throw IllegalArgumentException("missing params")
            }
            return MarketCapVM(CGRepo(cgApiHelper),curr,duration,prefOrder) as T
        } else if (modelClass.isAssignableFrom((GlobalVM::class.java))) {
            if(curr==null){
                Timber.d("GlobalVM requires more parameter")
                throw IllegalArgumentException("missing params")
            }
            return GlobalVM(CGRepo(cgApiHelper),curr) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}