package com.camo.kripto.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camo.kripto.remote.model.Global
import com.camo.kripto.remote.model.GlobalDefi
import com.camo.kripto.repos.Repository
import com.camo.kripto.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class GlobalVM @Inject constructor(private val cgRepo: Repository) : ViewModel() {

    var initialized = false
    var title: MutableLiveData<String> = MutableLiveData("Global")
    var globalCrypto: MutableLiveData<Resource<Global>> = MutableLiveData()
    var globalDefi: MutableLiveData<Resource<GlobalDefi>> = MutableLiveData()
    var prefCurrency: MutableLiveData<String> = MutableLiveData<String>()
    var refreshed: MutableLiveData<Boolean> = MutableLiveData(false)

    //    var globalDefi: MutableLiveData<Resource<GlobalDefi>> = MutableLiveData()
    fun setValues(curr: String?) {
        prefCurrency.postValue(curr)
    }

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