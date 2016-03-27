
package com.lipengwei.pedometer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                startSettingActivity();
                return true;
            case R.id.action_Statistics:
                startStatisticsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void startStatisticsActivity() {
        Intent intent = new Intent(this,StatisticsActivity.class);
        startActivity(intent);
    }
    
    private void startSettingActivity() {
        Intent intent = new Intent(this,SettingActivity.class);
        startActivity(intent);
    }

}
