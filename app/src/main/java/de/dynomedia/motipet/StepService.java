package de.dynomedia.motipet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        //checkIn();


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
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("---------------> INFO: ", "Service Sensor Event");
        // store values (steps) from sensor
        float[] values = sensorEvent.values;
        // cast values to int
        int _serviceSteps = (int) values[0];

        // Get the shared preferences
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        // put steps into shared preferences
        SharedPreferences.Editor myEditor = myPrefs.edit();
        myEditor.putInt("serviceValue", _serviceSteps);
        myEditor.apply();

        // finish service
        onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}