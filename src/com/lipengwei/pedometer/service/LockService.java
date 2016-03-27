package com.lipengwei.pedometer.service;

import java.util.Calendar;

import com.lipengwei.pedometer.LockActivity;
import com.lipengwei.pedometer.SettingActivity;
import com.lipengwei.pedometer.utils.Util;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class LockService extends Service {
    
    public static SharedPreferences saveSettingState;
    private Intent intent;
    @Override  
    public IBinder onBind(Intent arg0) {  
        return null;
    }  
  

    @SuppressWarnings("static-access")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return this.START_REDELIVER_INTENT;
    }

    public void onCreate() {
        super.onCreate();
        if(saveSettingState == null) {
            saveSettingState = this.getSharedPreferences(
                    SettingActivity.PEDOMETER_SETTING_PREFERENCES, MODE_PRIVATE);
        }
        IntentFilter mScreenOnFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);  
        LockService.this.registerReceiver(mScreenOnReceiver, mScreenOnFilter);
    }
  
    public void onDestroy() {  
        super.onDestroy();  
        this.unregisterReceiver(mScreenOnReceiver); 
    }  
    private BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver() {  
        @Override  
        public void onReceive(Context context, Intent intent) {
            Log.i("Lipengwei","onReceiver");
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);  
                //mKeyguardLock = mKeyguardManager.newKeyguardLock("");  
                //mKeyguardLock.disableKeyguard();
                if(checkDuringTime()) {
                    startLockActivity(context);
                }
            }  
        }
    }; 
    
    private boolean checkDuringTime() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        long startTime = Util.getStringToDate(
                saveSettingState.getString(SettingActivity.START_TIME_VALUE, "12:00"));
        Log.i("Lipengwei","startTime = "+ startTime);
        long endTime = Util.getStringToDate(
                saveSettingState.getString(SettingActivity.END_TIME_VALUE, "12:00"));
        Log.i("Lipengwei","endTime = "+ endTime);
        long now = (hour - 8) *3600 * 1000 + minute * 60 * 1000;
        boolean during = (now >= startTime) && (now <= endTime);
        Log.i("Lipengwei","now = "+ now);
        Log.i("Lipengwei","checkDuringTime() = "+ during);
        
        return during;
    }
    
    private void startLockActivity(Context context) {
        
        if(intent == null) {
            intent = new Intent(context,LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
