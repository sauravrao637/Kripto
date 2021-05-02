package com.camo.kripto.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.camo.kripto.data.repository.CGRepo
import com.camo.kripto.utils.Resource
import kotlinx.coroutines.Dispatchers

class CoinsVM (private val cGRepo: CGRepo) : ViewModel() {

    fun getCoins() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = cGRepo.getCoins()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}