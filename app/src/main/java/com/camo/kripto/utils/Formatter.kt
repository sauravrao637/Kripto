package com.camo.kripto.utils

import android.util.Log
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DateFormat.getDateTimeInstance
import java.text.DecimalFormat
import java.util.*

class Formatter : ValueFormatter() {

    private val format = DecimalFormat("###,##0.0")

    // override this for e.g. LineChart or ScatterChart
    override fun getPointLabel(entry: Entry?): String? {
        return if (entry != null) {
            getDateTime(entry.x) + "\n" + format.format(entry.y)
        } else {
            Log.d(TAG,"entry null")
            ""
        }
    }

    // override this for custom formatting of XAxis or YAxis labels
    override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
        return getDateTime(value)
    }

    // ... override other methods for the other chart types
    companion object {
        private val TAG = Formatter::class.simpleName
        fun getDateTime(s: Float): String? {
            return try {
                val sdf = getDateTimeInstance()
                val netDate = Date((s).toLong())
                //            Log.d("testing", netDate.toString())
                sdf.format(netDate)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
                e.toString()
            }
        }
    }
}