package com.camo.kripto.works

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.camo.kripto.R
import com.camo.kripto.modules.CGModule
import kotlinx.coroutines.*
import timber.log.Timber

class SyncLocalWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    override suspend fun doWork(): Result {
        return coroutineScope {
            val job = async {
                someWork()
            }
            job.invokeOnCompletion { exception: Throwable? ->
                when (exception) {
                    is CancellationException -> {
                        Timber.d("FTSA cancelled")
//                        GlobalScope.launch {
//                            cleanUP()
//                        }
                    }
                    else -> {// do something else.
                    }
                }
            }
            job.await()
        }
    }

    private suspend fun cleanUP() {
        val repository = CGModule.getRepo(appContext)
        withContext(Dispatchers.IO) {
            repository.clearCoins()
            repository.clearCurrencies()
            repository.close()
        }
    }

    private suspend fun someWork(): Result {
        var isSuccess = true
        val progress = "Sync Started"
        withContext(Dispatchers.IO) {
            val repository = CGModule.getRepo(appContext)
            val count = async {
                repository.getCurrCount()
            }
            val showNotification = count.await() != 0
            setForegroundAsyncIfNotFTSA(createForegroundInfo(progress), showNotification)
            val currencyLoaded = async { repository.lIRCurrencies().data }
            val coinsLoaded = async { repository.lIRCoins().data }
//            To simulate slow network
//            delay(6000)
            val isCurrencyLoaded = currencyLoaded.await()
            val isCoinsLoaded = coinsLoaded.await()
            if (isCurrencyLoaded == true && isCoinsLoaded == true) {
                setForegroundAsyncIfNotFTSA(createForegroundInfo("Successful"), showNotification)
                //to keep the notification for 2s
                delay(2000)
            } else {
                setForegroundAsyncIfNotFTSA(createForegroundInfo("Failed"), showNotification)
                delay(2000)
                isSuccess = false
            }
            repository.close()

        }
        return if (isSuccess) Result.success()
        else Result.failure()
    }

    private fun setForegroundAsyncIfNotFTSA(
        createForegroundInfo: ForegroundInfo,
        showNotification: Boolean
    ) {
        if (!showNotification) return
        setForegroundAsync(createForegroundInfo)
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.sync_notification_title)
        val cancel = applicationContext.getString(R.string.cancel_sync)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_kripto)
            .setOngoing(true)
        // Add the cancel action to the notification which can
        // be used to cancel the worker
        if (progress == "Sync Started") notification.addAction(
            android.R.drawable.ic_delete,
            cancel,
            intent
        )

        return ForegroundInfo(1, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val context = appContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.sync_notification_channel)
            val descriptionText = context.getString(R.string.sync_notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                context.getString(R.string.notification_channel_id),
                name,
                importance
            ).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }
}