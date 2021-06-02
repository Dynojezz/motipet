package de.dynomedia.motipet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepService extends Service implements SensorEventListener {

    //creating a mediaplayer object
    private MediaPlayer player;

    private SensorManager sm;
    private Sensor s;

    // If it is needed to bind the service with an activity, this method is called.
    // The service can result back something to the activity after binding.
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //getting systems default ringtone
        player = MediaPlayer.create(this,
                Settings.System.DEFAULT_RINGTONE_URI);
        //setting loop play to true
        //this will make the ringtone continuously playing
        player.setLooping(true);

        //staring the player
        player.start();

        //we have some options for service
        //start sticky means service will be explicity started and stopped


        // setup of the step counter
        checkIn();


        return START_STICKY;
    }

    /**
     * checkIn: initializes the step counter
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("CheckResult")
    private void checkIn() {
        // initializes SensorManager
        sm = getSystemService(SensorManager.class);
        if (sm == null) {
            Log.d("---------------> INFO", "Error beim SensorManager");
        }
        // initializes Sensor with type step counter
        s = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (s != null) {
            sm.registerListener((SensorEventListener) this, s, SensorManager.SENSOR_DELAY_UI);
        } else {
            sm.unregisterListener((SensorEventListener) this);
            Log.d("---------------> INFO", "Kein Sensor gefunden");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopping the player when service is destroyed
        player.stop();
        //stopping the step listener when service is destroyed
        sm.unregisterListener((SensorEventListener) this);
        //close databese when service is destroyed
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("---------------> INFO: ", "Service Sensor Event");
        // store values (steps) from sensor
        float[] values = sensorEvent.values;
        // cast values to int
        int _serviceSteps = (int) values[0];
        // var for the steps of today
        int _dailySteps;

        // Get the shared preferences
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        // put service-stepvalue into shared preferences
        SharedPreferences.Editor myEditor = myPrefs.edit();
        myEditor.putInt("serviceValue", _serviceSteps);
        myEditor.apply();

        // subtract stored steps from tacked steps to get daily steps
        _dailySteps = _serviceSteps - myPrefs.getInt("lastValue", 0);

        // call updateMyPrefs() to set daily step to 0
        StepcounterActivity.updateMyPrefs(this, _serviceSteps);

        Log.d("---------------> INFO", _serviceSteps + " Schritte wurden gespeichert");


        /**
         * store daily steps to motiDB
         */
        SQLiteDatabase motiLog = openOrCreateDatabase("motiLog.db", MODE_PRIVATE, null); //null == standard cursor for databases

        int motiID = myPrefs.getInt("motiID", 1);
        int new_dayNR = myPrefs.getInt("dayNR", 0) + 1;
        myPrefs.edit().putInt("dayNR", new_dayNR).apply();
        int new_dailysteps = _dailySteps;
        String new_dailydistance = SettingsActivity.getDistance(this, _dailySteps);
        String new_dailycalories = SettingsActivity.getCalories(this, _dailySteps);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMANY);
        String new_date = formatter.format(new Date());
        int weekday = new Date().getDay();

        motiLog.execSQL("INSERT INTO day (motiID, dayNR, dailysteps, dailydistance, dailycalories, date, weekday) " +
                "VALUES ('"+motiID+"', '"+new_dayNR+"', '"+new_dailysteps+"', '"+new_dailydistance+"', '"+new_dailycalories+"', '"+new_date+"', '"+weekday+"')");

        // finish service
        onDestroy();
        motiLog.close();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}