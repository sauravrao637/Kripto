package com.camo.kripto.utils

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import com.camo.kripto.R
import com.camo.kripto.utils.Formatter
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF




class ChartMarker(context: Context?, layoutResource: Int) : MarkerView(context, layoutResource) {

        public val tv = findViewById<TextView>(R.id.tv_marker)


    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e != null) {

            val s = e.getY().toString()+"\n"+Formatter.getDateTime(e.x)
            tv.text = s
            tv.setTextColor(Color.RED)
        }
        super.refreshContent(e, highlight)
    }


    private var mOffset: MPPointF? = null

    override fun getOffset(): MPPointF? {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }
        return mOffset
    }
}