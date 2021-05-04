package com.camo.kripto.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.camo.kripto.data.api.CGApiHelper
import com.camo.kripto.data.api.RetrofitBuilder
import com.camo.kripto.data.model.CoinCD
import com.camo.kripto.data.repository.CGRepo
import com.camo.kripto.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CoinActivityVM(private val cgRepo: CGRepo) : ViewModel() {
    private val TAG= CoinActivityVM::class.simpleName
    val CD = MutableLiveData<CoinCD>()
    var title =  MutableLiveData<String>()
    var duration = MutableLiveData<String>()
    var currency = MutableLiveData<String>()
    var allCurr = MutableLiveData<List<String>>()


    init {
        duration.postValue("24h")
    }
    fun getCurrentData(id: String): Flow<Resource<CoinCD>> {
        return  flow {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = cgRepo.getCurrentData(id)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun getSupportedCurr() : Flow<Resource<List<String>>> {
        return flow {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = cgRepo.getSupportedCurr()))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
                Log.d(TAG, exception.toString())
            }
        }
    }

}
