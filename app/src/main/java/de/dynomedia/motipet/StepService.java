package de.dynomedia.motipet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import java.util.Calendar;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        /** update moti steps and put into shared prefs and db*/
        int motiSteps = myPrefs.getInt("motiSteps", 0); // CHANGE VALUE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        motiSteps = motiSteps + new_dailysteps;
        myPrefs.edit().putInt("motiSteps", motiSteps).apply();
        motiLog.execSQL("UPDATE moti SET steps ='"+motiSteps+"' WHERE motiID = '1'");



        // set a time and initialize an alarm with that time
        Calendar calendar = Calendar.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    23, 59, 0);
        } else {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    23, 59, 0);
        }
        setAlarm(calendar.getTimeInMillis());
        Toast.makeText(StepService.this, "Alarm is set at 23:59", Toast.LENGTH_SHORT).show();

        // finish service
        onDestroy();
        motiLog.close();
    }

    /**
     * Sets the alarm.
     * @param time alarm time
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm(long time) {
        //getting the alarm manager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, StepAlarm.class);

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        //setting the repeating alarm that will be fired every day
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pi);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}