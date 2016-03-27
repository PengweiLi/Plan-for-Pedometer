package com.lipengwei.pedometer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;


public class WelcomeActivity extends Activity
{
    private boolean ifFirstTimeStart;

    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(android.content.Context context, Intent intent)
        {
            finish();
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        IntentFilter filter = new IntentFilter();
        filter.addAction("close");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Handler handler = new Handler();
        int delayTimes;
        if (!ifFirstTimeStart)
        {
            delayTimes = 2000;
        } else
        {
            delayTimes = 0;
        }

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            }
        }, delayTimes);
    }
}
