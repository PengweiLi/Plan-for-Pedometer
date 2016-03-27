package com.lipengwei.pedometer;

import java.text.NumberFormat;
import java.util.Locale;
import com.lipengwei.pedometer.database.Database;
import com.lipengwei.pedometer.service.StepCalculation;
import com.lipengwei.pedometer.utils.Util;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class StartFragment extends Fragment{

    private TextView stepsView, totalView, averageView;

    private int todayOffset, total_start, total_days;
    private final static int MSG_SET_CURRENT_STEP = 1;
    private final static int MSG_UPDATA_UI = 2;
    public final static NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
    private static SharedPreferences saveSettingState;
    private Thread thread;
    private Button startButton;
    private Button stopButton;
    private TextView todayText;
    private TextView goalsText;
    private ImageButton im;
    
    
    Handler handler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
             super.handleMessage(msg);
             switch(msg.what) {
                 case MSG_SET_CURRENT_STEP:
                     stepsView.setText(""+(StepCalculation.CURRENT_STEPS));
                     break;
                 case MSG_UPDATA_UI:
                     updataUI();
                     break;
                 default:
                     break;
             }
        }
    };

    @SuppressWarnings("static-access")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Context context = getActivity();
        if(saveSettingState == null) {
            saveSettingState = context.getSharedPreferences(
                    SettingActivity.PEDOMETER_SETTING_PREFERENCES, context.MODE_PRIVATE);
        }
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    super.run();
                    while (true) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                         Message msg = new Message();
                         msg.what = MSG_SET_CURRENT_STEP;
                         handler.sendMessage(msg);
                    }
                }
            };
            thread.start();
        }
        Database db = Database.getInstance(getActivity().getApplicationContext());
        if (db.getSteps(Util.getToday()) == Integer.MIN_VALUE) {
             db.insertNewDay(Util.getToday(), 0);
        }
        db.close();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.my_fragment, null);
        stepsView = (TextView) v.findViewById(R.id.steps);
        totalView = (TextView) v.findViewById(R.id.total);
        averageView = (TextView) v.findViewById(R.id.average);
        startButton = (Button)v.findViewById(R.id.startbutton);
        //stopButton = (Button)v.findViewById(R.id.stopbutton);
        todayText = (TextView)v.findViewById(R.id.today);
        goalsText = (TextView)v.findViewById(R.id.goal);
        if(StepCalculation.RUNNING_FLAG) {
            startButton.setText("Stop");
        }
        startButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                if(!StepCalculation.RUNNING_FLAG) {
                    startCount();
                    startButton.setText("Stop");
                } else {
                    stopCount();
                    startButton.setText("Start");
                }
            }
        });
        return v;
    }
    
    private void startCount() {
        Context context = getActivity();
        Intent service = new Intent(context, StepCalculation.class);
        context.startService(service);
    }
    
    private void stopCount() {
        Context context = getActivity();
        Intent service = new Intent(context, StepCalculation.class);
        context.stopService(service);
        Message msg = new Message();
        msg.what = MSG_UPDATA_UI;
        handler.sendMessage(msg);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        
        goalsText.setText(String.valueOf(saveSettingState.getInt(
                SettingActivity.SET_STEP_NUMBER_VALUE, 0)));
        updataUI();
    }
    
    private void updataUI() {
        Database db = Database.getInstance(getActivity().getApplicationContext());
        todayOffset = db.getSteps(Util.getToday());
        total_start = db.getTotalWithoutToday();
        total_days = db.getDays();
        todayOffset = todayOffset >0 ? todayOffset : 0;
        todayText.setText(String.valueOf(todayOffset));
        totalView.setText(String.valueOf(total_start));
        averageView.setText(String.valueOf(total_start/total_days));
        db.close();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy(); 
    }
}

