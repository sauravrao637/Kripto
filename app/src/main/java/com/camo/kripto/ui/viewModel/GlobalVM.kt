package com.camo.kripto.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camo.kripto.data.model.Global
import com.camo.kripto.data.model.GlobalDefi
import com.camo.kripto.data.repository.CGRepo
import com.camo.kripto.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GlobalVM(private val cgRepo: CGRepo, private val curr: String) : ViewModel() {

    var title: MutableLiveData<String> = MutableLiveData("Global")
    var globalCrypto: MutableLiveData<Resource<Global>> = MutableLiveData()
    var globalDefi: MutableLiveData<Resource<GlobalDefi>> = MutableLiveData()
    var prefCurrency: MutableLiveData<String> = MutableLiveData(curr)
//    var globalDefi: MutableLiveData<Resource<GlobalDefi>> = MutableLiveData()

    fun getGlobal(): Flow<Resource<Global>> {
        return flow {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = cgRepo.getGlobal()))
            } catch (exception: Exception) {
                exception.printStackTrace()
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }

    fun getGlobalDefi(): Flow<Resource<GlobalDefi>> {
        return flow {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = cgRepo.getGlobalDefi()))
            } catch (exception: Exception) {
                exception.printStackTrace()
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    }
}