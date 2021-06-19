package com.camo.kripto.error

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.camo.kripto.R
import com.camo.kripto.ktx.isInterruptedCaused
import com.camo.kripto.ktx.isNetworkRelated
import timber.log.Timber

class ErrorPanelHelper(
    rootView: View,
    val onRetry: () -> Unit
) {
    private val context: Context = rootView.context!!
    private val errorPanelRoot: View = rootView.findViewById(R.id.error_panel)
    private val errorTextView: TextView = errorPanelRoot.findViewById(R.id.error_message_view)
    private val errorButtonAction: Button = errorPanelRoot.findViewById(R.id.error_button_action)
    private val errorButtonRetry: Button = errorPanelRoot.findViewById(R.id.error_button_retry)

    fun showError(errorInfo: ErrorInfo?) {
        if (errorInfo == null) {
            showTextError("Error")
            return
        }
        if (errorInfo.throwable != null && errorInfo.throwable!!.isInterruptedCaused) {
            Timber.d(errorInfo.throwable)
            return
        }
        errorButtonRetry.setOnClickListener {
            onRetry()
            Timber.d("clicked retry")
        }
        //TODO user can report error
        errorButtonAction.isVisible = false
//        errorButtonAction.setText(R.string.error_snackbar_action)
//        errorButtonAction.setOnClickListener {
//            ErrorActivity.reportError(context, errorInfo)
//        }

        // hide retry button by default, then show only if necessary
        errorButtonRetry.isVisible = false
        errorTextView.setText(
            when (errorInfo.errorCause) {
                ErrorCause.EMPTY_COIN_ID_NAME ->{
                    errorButtonRetry.isVisible = false
                    R.string.empty_coin_id_name
                }
                else -> {
                    errorButtonRetry.isVisible = true
                    if (errorInfo.throwable != null && errorInfo.throwable!!.isNetworkRelated) {
                        R.string.key_internet_error
                    } else {
                        R.string.error_snackbar_message
                    }
                }
            }
        )
    }

    fun showTextError(errorString: String) {
        errorButtonAction.isVisible = false
        errorButtonRetry.isVisible = false
        errorTextView.text = errorString
    }

    fun hide() {
        errorButtonAction.setOnClickListener(null)
    }

    fun isVisible(): Boolean {
        return errorPanelRoot.isVisible
    }

    fun dispose() {
        errorButtonAction.setOnClickListener(null)
        errorButtonRetry.setOnClickListener(null)
    }

    companion object {
        val TAG: String = ErrorPanelHelper::class.simpleName!!
    }

}