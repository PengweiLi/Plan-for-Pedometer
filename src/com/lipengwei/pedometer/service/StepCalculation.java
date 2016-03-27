
package com.lipengwei.pedometer.service;

import com.lipengwei.pedometer.database.Database;
import com.lipengwei.pedometer.utils.Util;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class StepCalculation extends Service implements SensorEventListener {

    public static Boolean RUNNING_FLAG = false;
    public static int CURRENT_STEPS = 0;
    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;
    private static long end = 0;
    private static long start = 0;

    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = {
            new float[3 * 2], new float[3 * 2]
    };
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        reRegisterSensor();
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        Database db = Database.getInstance(getApplicationContext());
        if (db.getSteps(Util.getToday()) == Integer.MIN_VALUE) {
             db.insertNewDay(Util.getToday(), CURRENT_STEPS);
        }
        db.close();
    }
    
    @SuppressWarnings("static-access")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        RUNNING_FLAG = true;
        return this.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Database db = Database.getInstance(getApplicationContext());
        db.updateSteps(Util.getToday(), CURRENT_STEPS);
        db.close();
        CURRENT_STEPS = 0;
        RUNNING_FLAG = false;
        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
         
        checkStep(event);

    }

    public void checkStep(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float vSum = 0;
            for (int i = 0; i < 3; i++) {
                final float v = mYOffset + event.values[i] * mScale[1];
                vSum += v;
            }
            int k = 0;
            float v = vSum / 3;

            float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
            if (direction == -mLastDirections[k]) {
                // Direction changed
                int extType = (direction > 0 ? 0 : 1); // minumum or
                // maximum?
                mLastExtremes[extType][k] = mLastValues[k];
                float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                if (diff > 5) {
                    boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                    boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                    boolean isNotContra = (mLastMatch != 1 - extType);

                    if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                        end = System.currentTimeMillis();
                        if (end - start > 500) {
                            CURRENT_STEPS++;
                            mLastMatch = extType;
                            start = end;
                        }
                    } else {
                        mLastMatch = -1;
                    }
                }
                mLastDiff[k] = diff;
            }
            mLastDirections[k] = direction;
            mLastValues[k] = v;
            
            
        }
    }

    private void reRegisterSensor() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        try {
            sm.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // enable batching with delay of max 5 min
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

}
