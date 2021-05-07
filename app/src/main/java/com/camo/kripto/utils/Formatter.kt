package com.camo.kripto.utils

import android.util.Log
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DateFormat.getDateTimeInstance
import java.text.DecimalFormat
import java.util.*

class  Formatter:ValueFormatter() {
    private val format = DecimalFormat("###,##0.0")
    // override this for e.g. LineChart or ScatterChart
    override fun getPointLabel(entry: Entry?): String? {
    if (entry != null) {
        return getDateTime(entry.x)+"\n"+format.format(entry.y)
    }
        else return ""
    }

    // override this for custom formatting of XAxis or YAxis labels
    override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
        return getDateTime(value)
    }
    // ... override other methods for the other chart types
     companion object {
        fun getDateTime(s: Float): String? {
            if (s == null) return ""
            try {
                val sdf = getDateTimeInstance()
                val netDate = Date((s).toLong())
//            Log.d("testing", netDate.toString())
                return sdf.format(netDate)
            } catch (e: Exception) {
                return e.toString()
            }
        }
    }
}