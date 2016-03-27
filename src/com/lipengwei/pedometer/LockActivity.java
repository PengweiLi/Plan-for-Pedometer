
package com.lipengwei.pedometer;

import com.lipengwei.pedometer.service.LockService;
import com.lipengwei.pedometer.service.StepCalculation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

public class LockActivity extends Activity {

    private TextView planSteps;
    private TextView timeQuantum;
    private MyCircleView restOfSteps;
    private static SharedPreferences saveSettingState;
    private Thread thread;
    private int mSteps;
    private int planNum;
    
    Handler handler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
             super.handleMessage(msg);
             restOfSteps.setProgress(StepCalculation.CURRENT_STEPS -mSteps);
             if((planNum-(StepCalculation.CURRENT_STEPS -mSteps)) <=0) {
                 SharedPreferences.Editor editor = saveSettingState.edit();
                 editor.putBoolean(SettingActivity.SET_SWITCH_ONOFF, false);
                 editor.commit();
                 Intent service = new Intent(LockActivity.this, LockService.class);
                 LockActivity.this.stopService(service);
                 LockActivity.this.finish();
             }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        setContentView(R.layout.activity_lock);
        if(saveSettingState == null) {
            saveSettingState = this.getSharedPreferences(
                    SettingActivity.PEDOMETER_SETTING_PREFERENCES, MODE_PRIVATE);
        }
        planNum = saveSettingState.getInt(SettingActivity.SET_STEP_NUMBER_VALUE, 0);
        planSteps = (TextView)findViewById(R.id.plannum);
        timeQuantum = (TextView)findViewById(R.id.plantime);
        restOfSteps = (MyCircleView)findViewById(R.id.remanentstepsnumber);
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (true) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                         Message msg = new Message();
                         handler.sendMessage(msg);
                    }
                }
            };
            thread.start();
        }
        restOfSteps.setProgress(0);
        startSer();
    }
    
    private void startSer() {
        if(StepCalculation.RUNNING_FLAG) {
            mSteps = StepCalculation.CURRENT_STEPS;
        } else {
            Intent service = new Intent(this,StepCalculation.class);
            this.startService(service);
        }
    }
    
    private void stopSer() {
        Intent service = new Intent(this,StepCalculation.class);
        this.stopService(service);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        String timeText = saveSettingState.getString(SettingActivity.START_TIME_VALUE, "12:00") +
                "-" + saveSettingState.getString(SettingActivity.END_TIME_VALUE, "18:00");
        planSteps.setText(String.valueOf(planNum));
        restOfSteps.setTotalProgress(planNum);
        timeQuantum.setText(timeText);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        int key = event.getKeyCode();  
        switch (key)  
        {  
        case KeyEvent.KEYCODE_BACK:       
        case KeyEvent.KEYCODE_VOLUME_DOWN:  
        case KeyEvent.KEYCODE_VOLUME_UP:  
            return true;              
        }  
        return super.onKeyDown(keyCode, event);

    }
    
    @Override
    public void onDestroy() {
         super.onDestroy();
         stopSer();
    }
}
