package com.camo.kripto.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.repository.CGrepo
import com.camo.kripto.ui.viewModel.CoinsVM

class VMFactory(private val cgApiHelper: CGApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoinsVM::class.java)) {
            return CoinsVM(CGrepo(cgApiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}