package com.camo.kripto.utils

import android.content.Context
import android.content.pm.PackageManager
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Extras {

    fun getAppVersion(context: Context): String {
        var version = ""
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            version = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }

    fun getFormattedDouble(double: Double?): String {
        if (double == null) return "NA"
        return if (kotlin.math.abs(double) < 1) String.format("%, .3f", double)
        else String.format("%,.0f", double)
    }

    fun getFormattedPerChange(double: Double): String {
        var prefix = ""
        if (double > 0) prefix = "+"
        return prefix + getFormattedDouble(double) + "%"
    }

    fun getFormattedDoubleCurr(
        double: Double?,
        curr: String,
        prefix: String = "",
        suffix: String = ""
    ): String {
        if (double == null) return "NA"
        var text = getFormattedDouble(double)
        text = getCurrencySymbol(curr) + text
        return prefix + text + suffix
    }

    fun getCurrencySymbol(currencyCode: String?): String? {
        if (currencyCode == null) return ""
        return try {
            val currency: Currency = Currency.getInstance(currencyCode.toUpperCase(Locale.ROOT))
            currency.symbol
        } catch (e: Exception) {
            currencyCode
        }
    }

    fun getDateTime(s: Float): String? {
        return try {
            val sdf = DateFormat.getDateTimeInstance()
            val netDate = Date((s).toLong())
            //            Log.d("testing", netDate.toString())
            sdf.timeZone = TimeZone.getDefault()
            sdf.format(netDate)
        } catch (e: Exception) {
            Timber.d(e.toString())
            e.toString()
        }
    }

    fun getInLocalTime(s: String?): String {
//            "yyyy-MM-dd'T'HH:mm:sssZ"
        if (s == null) return ""
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val formatter = DateFormat.getDateTimeInstance()
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(parser.parse(s))
    }

}