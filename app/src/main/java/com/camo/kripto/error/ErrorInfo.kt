package com.camo.kripto.error

import android.os.Parcelable
import androidx.annotation.StringRes
import com.camo.kripto.R
import com.camo.kripto.ktx.isNetworkRelated
import kotlinx.android.parcel.Parcelize
import java.io.PrintWriter
import java.io.StringWriter

@Parcelize
class ErrorInfo(
    val stackTraces: Array<String>,
    val userAction: UserAction,
    val messageStringId: Int,
    @Transient // no need to store throwable, all data for report is in other variables
    var throwable: Throwable? = null
) : Parcelable {

    private constructor(
        throwable: Throwable,
        userAction: UserAction
    ) : this(
        throwableToStringList(throwable),
        userAction,
        getMessageStringId(throwable, userAction),
        throwable
    )

    private constructor(
        throwable: List<Throwable>,
        userAction: UserAction
    ) : this(
        throwableListToStringList(throwable),
        userAction,
        getMessageStringId(throwable.firstOrNull(), userAction),
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

        fun throwableToStringList(throwable: Throwable) = arrayOf(getStackTrace(throwable))

        fun throwableListToStringList(throwable: List<Throwable>) =
            Array(throwable.size) { i -> getStackTrace(throwable[i]) }

        @StringRes
        private fun getMessageStringId(
            throwable: Throwable?,
            action: UserAction
        ): Int {
            return when {
                throwable != null && throwable.isNetworkRelated -> R.string.key_internet_error
                action == UserAction.UI_ERROR -> R.string.app_ui_crash
                action == UserAction.REQUESTED_GRAPH -> R.string.error_unable_to_load_graph
                else -> R.string.general_error
            }
        }
    }
}