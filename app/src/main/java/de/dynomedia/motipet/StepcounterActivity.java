package de.dynomedia.motipet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class StepcounterActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView moti;
    private TextView steps;
    private ImageButton journal;

    private SensorManager sm;
    private Sensor s;

    final RxPermissions rxPermissions = new RxPermissions(this);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the shared preferences
        SharedPreferences myPrefs =  getSharedPreferences("myPrefs", MODE_PRIVATE);

        // Check if onboarding_complete is false
        if(!myPrefs.getBoolean("onboarding_complete",false)) {
            // Start the onboarding Activity
            Intent onboarding = new Intent(this, OnboardingFragment1.class);
            startActivity(onboarding);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            // Close the main Activity
            finish();
            return;
        }
        // Set onboarding_complete to true; REMOVE WHEN READY CODED!!!!!
        // myPrefs.edit().putBoolean("onboarding_complete",false).apply();

        setContentView(R.layout.main);

        // Set Moti-Image
        String moti_indicator = myPrefs.getString("moti", "egg1");
        Log.d("---------------> INFO: ", moti_indicator + " wird angezeigt.");
        moti = findViewById(R.id.iv_moti);
        try {
            moti.setImageResource(getResources().getIdentifier(moti_indicator,"drawable",getPackageName()));
        } catch (Exception e) {
            System.out.println("Moti image not found. Change filename.");
        }

        // initializes step-TextView
        steps = findViewById(R.id.tv_steps);

        // initializes journal-Button
        journal = findViewById(R.id.ib_journal);
        journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StepcounterActivity.this, JournalActivity.class));
            }
        });

        // setup of the step counter
        checkIn();

        // set a time and initialize an alarm with that time
        Calendar calendar = Calendar.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    19, 50, 15);
        } else {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    19, 50, 15);
        }
        setAlarm(calendar.getTimeInMillis());
    }


    /**
     * Sets the alarm.
     * @param time alarm time
     */
    private void setAlarm(long time) {
        //getting the alarm manager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, StepAlarm.class);

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        //setting the repeating alarm that will be fired every day
        am.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY, pi);
        Toast.makeText(this, "Alarm "+ time + " is set", Toast.LENGTH_SHORT).show();
    }


    /**
     * checkIn: initializes the step counter
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("CheckResult")
    private void checkIn() {
        // requests for access to stepsensor
        rxPermissions.request(Manifest.permission.ACTIVITY_RECOGNITION).subscribe(granted -> {
            if (granted) {
            } else {
                Toast.makeText(this, "Schritte werden nicht getrackt. Kann über die Eintstellungen geändert werden.", Toast.LENGTH_LONG).show();
            }
        });
        // initializes SensorManager
        sm = getSystemService(SensorManager.class);
        if (sm == null) {
            finish();
        }
        // initializes Sensor with type step counter
        s = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (s != null) {
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        } else {
            sm.unregisterListener(this);
            steps.setText(R.string.no_sensor);
        }
    }

    /**
     * Puts tracked steps into TextView on the display
     * @param sensorEvent everytime the sensor is triggered
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("---------------> INFO: ", "Sensor Event");
        // store values (steps) from sensor
        float[] values = sensorEvent.values;
        // cast values to int
        int _steps = (int) values[0];

        // Get the shared preferences
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        // subtract stored steps from tacked steps
        _steps = _steps - myPrefs.getInt("lastValue", 0);

        // put steps into TextView
        this.steps.setText(String.format(Locale.US, "%d", _steps));
    }


    /**
     * Stores the hand over value into SharedPrefs
     * @param context the context (eg. the class)
     * @param stepValue the value you want to update
     */
    public static void updateMyPrefs(Context context, int stepValue) {
        // Get the shared preferences
        SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        // initialized editor for SharedPrefs
        SharedPreferences.Editor myEditor = myPrefs.edit();
        // puts new value to SharedPrefs
        myEditor.putInt("lastValue", stepValue);
        myEditor.apply();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
