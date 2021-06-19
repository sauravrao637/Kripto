package com.camo.kripto.works

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.camo.kripto.R
import com.camo.kripto.modules.CGModule
import com.camo.kripto.utils.Extras
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import kotlin.text.StringBuilder

class PriceAlertWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
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

    private suspend fun someWork(): Result {
        var isSuccess = true
        withContext(Dispatchers.IO) {
            val repository = CGModule.getRepo(context = context)
            val triggered = repository.getTriggered()
            Timber.d("triggered %d", triggered?.size ?: 0)
            if (triggered == null) {
                isSuccess = false
            } else {
                if (triggered.isNotEmpty()) {
                    if (triggered.size == 1) {
                        if (triggered[0].second.isNotEmpty()) {
                            val s = StringBuilder()
                            s.append(triggered[0].first.name)
                            s.append(" reached ")
                            s.append(
                                Extras.getFormattedDoubleCurr(
                                    triggered[0].second[triggered[0].first.curr],
                                    triggered[0].first.curr,
                                    "",
                                    ""
                                )
                            )
                            Timber.d(s.toString())
                            createForegroundInfo(
                                "Price triggered for ${triggered[0].first.name}",
                                s.toString()
                            )
                        } else {
                            Timber.d("No currencies!")
                        }
                    } else {
                        if (triggered[0].second.isNotEmpty()) {
                            val s = StringBuilder()
                            for (i in triggered) {
                                s.append(i.first.name)
                                s.append(" reached ")
                                s.append(
                                    Extras.getFormattedDoubleCurr(
                                        i.second[i.first.curr],
                                        i.first.curr,
                                        "",
                                        ""
                                    )
                                )
                                s.append("\n")
                            }
                            Timber.d(s.toString())
                            createForegroundInfo(
                                "Price triggered for ${
                                    triggered[0].first.name + "&" + triggered.size.minus(
                                        1
                                    )
                                } others", s.toString()
                            )
                        } else {
                            Timber.d("No currencies!")
                        }
                    }
                }
            }
            repository.close()
        }
        return if (isSuccess) Result.success()
        else Result.failure()
    }

    //TODO improve notifications
    private fun createForegroundInfo(
        smallTextString: String,
        bigTextString: String
    ): ForegroundInfo {
        val id = applicationContext.getString(R.string.price_alert_notification_channel_id)
        val title = applicationContext.getString(R.string.alert)

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(smallTextString)
            .setSmallIcon(R.drawable.ic_kripto)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigTextString)
            )
            .setOngoing(false)
            .setAutoCancel(true)
        notificationManager.notify(2, notification.build())
        return ForegroundInfo(2, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.price_alert_notification)
            val descriptionText =
                applicationContext.getString(R.string.price_alert_notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                context.getString(R.string.price_alert_notification_channel_id),
                name,
                importance
            ).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val TAG = "price_alert_worker"
        const val NAME = "price_alert_worker"
    }
}