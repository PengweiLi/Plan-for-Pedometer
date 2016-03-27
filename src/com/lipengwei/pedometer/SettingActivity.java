package com.lipengwei.pedometer;

import java.util.Calendar;

import com.lipengwei.pedometer.service.LockService;
import com.lipengwei.pedometer.utils.Util;

import android.app.Activity;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnTimeSetListener{
    
    private EditText startTime;
    private EditText endTime;
    private EditText stepsEdit;
    private Switch swichOnorOff;
    private boolean mOnorOff;
    private Intent mService;
    public static SharedPreferences saveSettingState;
    public static final String PEDOMETER_SETTING_PREFERENCES = "pedometer_steeting_preferences";
    public static String START_TIME_VALUE = "start_time_value";
    public static String END_TIME_VALUE = "end_time_value";
    public static String SET_STEP_NUMBER_VALUE = "set_step_number_value";
    public static String SET_SWITCH_ONOFF = "set_switch_onoff";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if(saveSettingState == null) {
            saveSettingState = this.getSharedPreferences(PEDOMETER_SETTING_PREFERENCES, MODE_PRIVATE);
        }
        mService = new Intent(this, LockService.class);
        swichOnorOff = (Switch)findViewById(R.id.onoff);
        swichOnorOff.setOnCheckedChangeListener(onOffListener);
        startTime = (EditText)findViewById(R.id.time_start_edit);
        startTime.setInputType(InputType.TYPE_NULL);
        startTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Util.showTimeEditDialog(SettingActivity.this,R.id.time_start_edit);
            }
        });

        endTime = (EditText)findViewById(R.id.time_end_edit);
        endTime.setInputType(InputType.TYPE_NULL);
        endTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Util.showTimeEditDialog(SettingActivity.this,R.id.time_end_edit);
            }
        });
        
        stepsEdit = (EditText)findViewById(R.id.steps_edit);

        startTime.setText(saveSettingState.getString(START_TIME_VALUE, "12:00"));
        endTime.setText(saveSettingState.getString(END_TIME_VALUE, "18:00"));
        stepsEdit.setText(String.valueOf(saveSettingState.getInt(SET_STEP_NUMBER_VALUE, 0)));
        swichOnorOff.setChecked((saveSettingState.getBoolean(SET_SWITCH_ONOFF, false)));
    }
    
    final CompoundButton.OnCheckedChangeListener onOffListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton,
                        boolean checked) {
                    mOnorOff = checked;
                    if(mOnorOff) {
                        if (checkMessage()) {
                            saveValue();
                            setTextEditable(checked);
                            startS();
                        } else {
                            swichOnorOff.setChecked(false);
                        }
                    } else {
                        saveValue();
                        setTextEditable(checked);
                        stopS();
                    }
                }
           };
     private void setTextEditable(boolean checked) {
         startTime.setEnabled(!checked);
         endTime.setEnabled(!checked);
         stepsEdit.setEnabled(!checked);
     }
     private boolean checkMessage() {

         if(Util.getStringToDate(startTime.getText().toString()) >= 
                 Util.getStringToDate(endTime.getText().toString())) {
             Toast.makeText(this, "Start time must before end time",
                     Toast.LENGTH_SHORT).show();
             return false;
         }
         if((TextUtils.isEmpty(stepsEdit.getText())) || 
                 (Integer.parseInt(stepsEdit.getText().toString()) <= 0)) {
             Toast.makeText(this, "Steps Number must be more than 0",
                     Toast.LENGTH_SHORT).show();
             return false;
         }
         return true;
     }
     

    @Override
    public void onTimeSet(TimePicker timepick, int hours, int minutes) {
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        String time = Util.getFormattedTime(getApplication(), calendar);
        if(Util.getId() == R.id.time_start_edit) {
            startTime.setText(time);
        } else {
            endTime.setText(time);
        }
    }

    private void startS() {
        if(mService != null) {
            startService(mService);
        }
    }
    
    private void stopS() {
        if(mService != null) {
            stopService(mService);
        }
    }
    
    private void saveValue() {
        SharedPreferences.Editor editor = saveSettingState.edit();
        editor.putString(START_TIME_VALUE, (startTime.getText()).toString());
        editor.putString(END_TIME_VALUE, (endTime.getText()).toString());
        editor.putInt(SET_STEP_NUMBER_VALUE, Integer.parseInt((stepsEdit.getText()).toString()));
        editor.putBoolean(SET_SWITCH_ONOFF, mOnorOff);
        editor.commit();
    }
}
