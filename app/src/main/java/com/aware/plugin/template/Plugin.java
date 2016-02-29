package com.aware.plugin.template;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.aware.Accelerometer;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.providers.Accelerometer_Provider;
import com.aware.utils.Aware_Plugin;

public class Plugin extends Aware_Plugin {
    public static double total_acceleration = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        TAG = "AWARE::"+getResources().getString(R.string.app_name);
        DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

        //Initialize our plugin's settings
        if( Aware.getSetting(this, Settings.STATUS_PLUGIN_TEMPLATE).length() == 0 ) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, true);
        }

        //Activate programmatically any sensors/plugins you need here
        //e.g., Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER,true);
        //NOTE: if using plugin with dashboard, you can specify the sensors you'll use there.
        Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, true);
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 600000000);

        //Any active plugin/sensor shares its overall context using broadcasts
        CONTEXT_PRODUCER = new ContextProducer() {
            @Override
            public void onContext() {
                //Broadcast your context here
            }
        };

        //Add permissions you need (Support for Android M) e.g.,
        //REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //To sync data to the server, you'll need to set this variables from your ContentProvider
        //DATABASE_TABLES = Provider.DATABASE_TABLES
        //TABLES_FIELDS = Provider.TABLES_FIELDS
        //CONTEXT_URIS = new Uri[]{ Provider.Table_Data.CONTENT_URI }

        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER);
        registerReceiver(accelerometerStatusListener, broadcastFilter);


        //Activate plugin
        Aware.startPlugin(this, "com.aware.plugin.template");
    }

    //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Check if the user has toggled the debug messages
        DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, false);

        //Deactivate any sensors/plugins you activated here
        //e.g., Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, false);
        Aware.setSetting(this, Aware_Preferences.STATUS_BATTERY, false);
        //Stop plugin
        Aware.stopPlugin(this, "com.aware.plugin.template");
    }

    BroadcastReceiver accelerometerStatusListener = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "" + intent.getExtras().get(Accelerometer.EXTRA_DATA));
            ContentValues values = (ContentValues)intent.getExtras().get(Accelerometer.EXTRA_DATA);
            total_acceleration = Math.sqrt(Math.pow((double)values.getAsFloat(Accelerometer_Provider.Accelerometer_Data.VALUES_0), 2) + Math.pow((double)values.getAsFloat(Accelerometer_Provider.Accelerometer_Data.VALUES_1), 2) + Math.pow((double)values.getAsFloat(Accelerometer_Provider.Accelerometer_Data.VALUES_2), 2));
            Log.d(TAG, ""+ total_acceleration);
        }
    };
}
