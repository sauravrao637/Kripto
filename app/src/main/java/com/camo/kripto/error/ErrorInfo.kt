package com.camo.kripto.error

import android.os.Parcelable
import androidx.annotation.StringRes
import com.camo.kripto.R
import com.camo.kripto.ktx.isNetworkRelated
import kotlinx.parcelize.Parcelize
import okhttp3.ResponseBody
import java.io.PrintWriter
import java.io.StringWriter

@Parcelize
class ErrorInfo(
    val stackTraces: Array<String>?,
    val errorCause: ErrorCause,
    val messageStringId: Int,
    @Transient // no need to store throwable, all data for report is in other variables
    var throwable: Throwable? = null
) : Parcelable {

    constructor(
        throwable: Throwable?,
        errorCause: ErrorCause
    ) : this(
        throwable?.let { throwableToStringList(it) },
        errorCause,
        getMessageStringId(throwable, errorCause),
        throwable
    )

    constructor(res: ResponseBody?) : this(
        null,
        getErrorCause(res),
        getMessageStringId(null, getErrorCause(res)),
        null
    )

    constructor(
        throwable: List<Throwable>,
        errorCause: ErrorCause
    ) : this(
        throwableListToStringList(throwable),
        errorCause,
        getMessageStringId(throwable.firstOrNull(), errorCause),
        throwable.firstOrNull()
    )

    companion object {
        private fun getStackTrace(throwable: Throwable): String {
            StringWriter().use { stringWriter ->
                PrintWriter(stringWriter, true).use { printWriter ->
                    throwable.printStackTrace(printWriter)
                    return stringWriter.buffer.toString()
                }
            }
        }

        private fun getErrorCause(res: ResponseBody?): ErrorCause {
            //TODO anaylyse res and return cause
            return ErrorCause.GENERAL_ERROR
        }

        fun throwableToStringList(throwable: Throwable) = arrayOf(getStackTrace(throwable))

        fun throwableListToStringList(throwable: List<Throwable>) =
            Array(throwable.size) { i -> getStackTrace(throwable[i]) }

        @StringRes
        private fun getMessageStringId(
            throwable: Throwable?,
            action: ErrorCause
        ): Int {
            return when {
                throwable != null && throwable.isNetworkRelated -> R.string.key_internet_error
                action == ErrorCause.UI_ERROR -> R.string.app_ui_crash
                action == ErrorCause.GET_MARKET_CHART -> R.string.error_unable_to_load_graph
                action == ErrorCause.PING_CG -> R.string.key_server_error
                action == ErrorCause.SYNC_FAILED -> R.string.key_server_error
                else -> R.string.general_error
            }
        }
    }
}