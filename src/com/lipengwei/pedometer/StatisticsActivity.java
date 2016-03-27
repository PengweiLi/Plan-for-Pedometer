package com.lipengwei.pedometer;

import java.util.Calendar;

import com.lipengwei.pedometer.database.Database;
import com.lipengwei.pedometer.ui.MyChartView;
import com.lipengwei.pedometer.utils.Util;

import android.app.Activity;
import android.os.Bundle;

public class StatisticsActivity extends Activity {
    
    private MyChartView chartView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        chartView = (MyChartView)findViewById(R.id.chartview);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateChart();
    }
    
    private void updateChart() {
        Database db = Database.getInstance(this.getApplicationContext());
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTimeInMillis(Util.getToday());
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        yesterday.add(Calendar.DAY_OF_YEAR, -6);
        int[] info = new int[7];
        int steps;
        for (int i = 0; i < 7; i++) {
            steps = db.getSteps(yesterday.getTimeInMillis());
            if (steps >=0) {
                 info[i] = steps;
            } else {
                info[i] = 0;
            }
            yesterday.add(Calendar.DAY_OF_YEAR, 1);
        }
        db.close();
        
        chartView.SetInfo(info);
    }

}
