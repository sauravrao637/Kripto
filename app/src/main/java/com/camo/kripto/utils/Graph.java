package com.camo.kripto.utils;

import android.graphics.Color;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Graph {
    public static LineData getData(List<List<BigDecimal>> list,String label) {
        ArrayList<Entry> listToReturn = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listToReturn.add(new Entry(dtf(list.get(i).get(0)),dtf(list.get(i).get(1))));
        }

        LineDataSet set1 = new LineDataSet(listToReturn, label);
        set1.setLineWidth(0.5f);
        set1.setColor(Color.WHITE);
        set1.setHighLightColor(Color.WHITE);
        set1.setDrawValues(false);
        set1.setDrawCircles(false);
        set1.setDrawCircleHole(false);

        return new LineData(set1);
    }

    public static float dtf(BigDecimal d) {
        return Float.parseFloat(String.valueOf(d));
    }

}
//if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {
//        return Instant.ofEpochSecond(1510500494)
//        .atZone(ZoneId.systemDefault())
//        .toLocalDateTime()
//        }