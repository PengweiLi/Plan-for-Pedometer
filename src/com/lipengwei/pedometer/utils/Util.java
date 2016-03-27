package com.lipengwei.pedometer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.lipengwei.pedometer.TimePickerFragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;

public class Util {
    
    public static final String FRAG_TAG_TIME_PICKER = "time_dialog";
    
    public static final CharSequence DEFAULT_FORMAT_12_HOUR = "h:mm a";

    public static final CharSequence DEFAULT_FORMAT_24_HOUR = "H:mm";
    private static int mId;
    
    public static long getToday() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
    
    public static void showTimeEditDialog(Activity activity,int id) {
        final FragmentManager manager = activity.getFragmentManager();
        final TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setOnTimeSetListener((TimePickerDialog.OnTimeSetListener) activity);
        timePickerFragment.show(manager, FRAG_TAG_TIME_PICKER);
        mId = id;
    }
    
    public static int getId() {
        return mId;
    }
    
    public static String getFormattedTime(Context context, Calendar time) {
        final boolean format24Requested = DateFormat.is24HourFormat(context);
        CharSequence format;
        if (format24Requested) {
            format = DEFAULT_FORMAT_24_HOUR;
        } else {
            format = DEFAULT_FORMAT_12_HOUR;
        }
        return (String) DateFormat.format(format, time);
    }
    
    public static long getStringToDate(String time) {
      SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
      Date date = new Date();
      try {
        date = sdf.parse(time);
    } catch (ParseException e) {
        e.printStackTrace();
    }
      return date.getTime();
      }
}
