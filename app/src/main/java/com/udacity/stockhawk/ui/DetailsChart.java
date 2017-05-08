package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

public class DetailsChart extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_chart);

        Intent intent = getIntent();
        final String symbol = intent.getStringExtra(Intent.EXTRA_TEXT);

        new AsyncTask<String, Void, List<HistoricalQuote>>() {
            private final static String TAG = "ASYNC_TASK";

            @Override
            protected List<HistoricalQuote> doInBackground(String... params) {
                String symbol = params[0];

                try {
                    Stock stock = YahooFinance.get(symbol);
                    List<HistoricalQuote> history = stock.getHistory();
                    return history;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<HistoricalQuote> list) {
                super.onPostExecute(list);
                Log.e(TAG, "updated: " + list.toString());

                ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

                int index = 0;

                for(HistoricalQuote quote : list){
                    entries.add(new BarEntry(index, quote.getVolume(), quote.getDate().toString()));
                    index++;
                }

                BarDataSet dataSet = new BarDataSet(entries, getString(R.string.chart_description_label));

                BarChart chart = new BarChart(getApplicationContext());
                setContentView(chart);

                BarData barData = new BarData(dataSet);
                chart.setData(barData);


                //color template1
                //        ColorTemplate.LIBERTY_COLORS;
                //        ColorTemplate.COLORFUL_COLORS;
                //        ColorTemplate.JOYFUL_COLORS;
                //        ColorTemplate.PASTEL_COLORS;
                //        ColorTemplate.VORDIPLOM_COLORS;

                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                chart.animateY(2000);
            }
        }.execute(symbol);




    }
}
