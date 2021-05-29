package com.camo.kripto.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import timber.log.Timber
import java.text.DecimalFormat

class Formatter : ValueFormatter() {

    private val format = DecimalFormat("###,##0.0")

    // override this for e.g. LineChart or ScatterChart
    override fun getPointLabel(entry: Entry?): String {
        return if (entry != null) {
            Extras.getDateTime(entry.x) + "\n" + format.format(entry.y)
        } else {
            Timber.d("entry null")
            ""
        }
    }

    // override this for custom formatting of XAxis or YAxis labels
    override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
        return Extras.getDateTime(value)
    }
    // ... override other methods for the other chart types
}