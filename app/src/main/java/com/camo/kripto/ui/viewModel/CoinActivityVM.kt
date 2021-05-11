package com.camo.kripto.ui.viewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.camo.kripto.data.model.CoinCD
import com.camo.kripto.data.model.MarketChart
import com.camo.kripto.data.repository.CGRepo
import com.camo.kripto.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class CoinActivityVM(private val cgRepo: CGRepo) : ViewModel() {

    val currentCoinData = MutableLiveData<CoinCD>()
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
                exception.printStackTrace()
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }
    }
    fun getChart(id:String?,curr:String?,days:String?): Flow<Resource<MarketChart>>{
        return if(id==null||curr==null||days==null) {
            flow {
                emit(Resource.error(data = null,message = "null parameter"))
            }
        } else flow {
            emit(Resource.loading(data = null))
            try{
                Timber.d("%s %s %s",curr,id,days)
                emit(Resource.success(data = cgRepo.getMarketChart(id,curr,days)))
            }catch (exception: java.lang.Exception){
                emit(Resource.error(data = null,message = exception.message?:"Error Occurred"))
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
                Timber.d( exception.toString())
            }
        }
    }

}
