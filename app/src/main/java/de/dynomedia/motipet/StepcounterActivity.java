package de.dynomedia.motipet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.Calendar;
import java.util.Locale;

public class StepcounterActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView moti, sync;
    private TextView tv_steps, tv_distance, tv_calories, tv_name;
    private ImageButton journal;

    private SensorManager sm;
    private Sensor s;

    final RxPermissions rxPermissions = new RxPermissions(this);

    String name;

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
        Log.d("---------------> INFO", moti_indicator + " wird angezeigt.");
        moti = findViewById(R.id.iv_moti);
        try {
            moti.setImageResource(getResources().getIdentifier(moti_indicator,"drawable",getPackageName()));
        } catch (Exception e) {
            System.out.println("Moti image not found. Change filename.");
        }




        // initializes TextView for steps, distance, calories and name
        tv_steps = findViewById(R.id.tv_steps);
        tv_distance = findViewById(R.id.tv_distance);
        tv_calories = findViewById(R.id.tv_calories);
        tv_name = findViewById(R.id.tv_name);

        SQLiteDatabase motiLog = openOrCreateDatabase("motiLog.db", MODE_PRIVATE, null);
        Cursor myCursor = motiLog.rawQuery("SELECT * FROM moti WHERE motiID = '1'", null);
        myCursor.moveToFirst();
        if (myCursor.getCount() == 1) {
            name = myCursor.getString(1);
        }
        tv_name.setText(name);
        myCursor.close();
        motiLog.close();

        sync = findViewById(R.id.iv_sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase motiLog = openOrCreateDatabase("motiLog.db", MODE_PRIVATE, null);
                Cursor myCursor = motiLog.rawQuery("SELECT * FROM day WHERE motiID = '1'", null);
                myCursor.moveToFirst();
                if (myCursor.getCount() == 1) {
                    name = myCursor.getString(2);
                }
                tv_name.setText(name);
                myCursor.close();
                motiLog.close();
            }
        });





        // initializes journal-Button
        journal = findViewById(R.id.ib_journal);
        journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StepcounterActivity.this, JournalActivity.class));//              !!!!!!!!!!!!!!!!!!!

                //startMyService();
            }
        });

        // setup of the step counter
        checkIn();

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
            tv_steps.setText(R.string.no_sensor);
        }
    }

    /**
     * Puts tracked steps into TextView on the display
     * @param sensorEvent everytime the sensor is triggered
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("---------------> INFO", "Sensor Event");
        // store values (steps) from sensor
        float[] values = sensorEvent.values;
        // cast values to int
        int _steps = (int) values[0];

        // Get the shared preferences
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        // sets steps to 0 at first start
        if (myPrefs.getBoolean("firstStart", true)) {
            updateMyPrefs(this, _steps);
        }
        // subtract stored steps from tacked steps
        _steps = _steps - myPrefs.getInt("lastValue", 0);

        // put steps into TextView
        this.tv_steps.setText(String.format(Locale.US, "%d", _steps));
        // put distance into TextView
        this.tv_distance.setText(SettingsActivity.getDistance(this, _steps));
        // put distance into TextView
        this.tv_calories.setText(SettingsActivity.getCalories(this, _steps));
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
