package com.camo.kripto.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.repository.CGRepo
import com.camo.kripto.ui.viewModel.CoinActivityVM
import com.camo.kripto.ui.viewModel.CoinsVM
import com.camo.kripto.ui.viewModel.MarketCapVM

class VMFactory(private val cgApiHelper: CGApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoinsVM::class.java)
        ) {
            return CoinsVM(CGRepo(cgApiHelper)) as T
        } else if (modelClass.isAssignableFrom(CoinActivityVM::class.java)) return CoinActivityVM(
            CGRepo(cgApiHelper)
        ) as T
        throw IllegalArgumentException("Unknown class name")
    }

}