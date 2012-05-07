package com.edofic.yodalib.examples.small;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import com.edofic.yodalib.location.ManagedLocator;

public class Location extends Activity
{
    private ManagedLocator locator;
    private TextView currentLocation;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        currentLocation = (TextView) findViewById(R.id.currentLocation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //init locator for updates every minute with initialisation if last known location is not older than a day
        locator = new ManagedLocator(this, locationUpdates, ManagedLocator.ONE_MINUTE, true, true, ManagedLocator.ONE_DAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locator.stop();
    }

    private Handler locationUpdates = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final android.location.Location location = locator.getCurrentBest();
            currentLocation.setText(location.getLongitude()+ " "+location.getLatitude());
        }
    };
}
