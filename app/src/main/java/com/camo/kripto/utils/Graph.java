package com.camo.kripto.utils;

import android.graphics.Color;

import com.camo.kripto.data.model.MarketChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public static LineData getData(MarketChart marketChart) {

        ArrayList<Entry> prices = new ArrayList<>();
        for(int i = 0;i<marketChart.getPrices().size();i++){
            prices.add(new Entry(dtf(marketChart.getPrices().get(i).get(0)),dtf(marketChart.getPrices().get(i).get(1))));
            //TODO for rest
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(prices, "Price");

        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        set1.setLineWidth(2f);
        set1.setCircleRadius(2f);
        set1.setCircleHoleRadius(1f);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setHighLightColor(Color.BLUE);
        set1.setDrawValues(false);

        // create a data object with the data sets
        return new LineData(set1);
    }

    private static float dtf(Double d) {
        return Float.parseFloat(String.valueOf(d));
    }
}
