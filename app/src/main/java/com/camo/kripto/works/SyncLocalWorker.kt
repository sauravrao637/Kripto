package com.camo.kripto.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.camo.kripto.modules.CGModule
import com.camo.kripto.repos.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import timber.log.Timber
class SyncLocalWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    lateinit var repository: Repository
    override suspend fun doWork(): Result {
        var isSuccess: Boolean = true
        withContext(Dispatchers.IO){
            repository = CGModule.getRepo(appContext)
            val currencyLoaded = async{ repository.lIRcurrencies().data }
            val coinsLoaded = async{repository.lIRCoins().data}
            if (currencyLoaded.await() == true && coinsLoaded.await() == true) {
                Timber.d("yup success")
            } else {
                Timber.d("Im somrry")
                isSuccess = false
            }
        }
        return if(isSuccess) Result.success()
        else Result.failure()
    }

}