package de.dynomedia.motipet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
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
import java.util.Locale;

public class StepcounterActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView moti;
    private TextView steps;
    private ImageButton journal;
    private SensorManager sm;
    private Sensor s;
    private int trackedSteps;
    final RxPermissions rxPermissions = new RxPermissions(this);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the shared preferences
        SharedPreferences motiPrefs =  getSharedPreferences("motiPrefs", MODE_PRIVATE);
        // Check if onboarding_complete is false
        if(!motiPrefs.getBoolean("onboarding_complete",false)) {
            // Start the onboarding Activity
            Intent onboarding = new Intent(this, OnboardingFragment1.class);
            startActivity(onboarding);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            // Close the main Activity
            finish();
            return;
        }
        // Set onboarding_complete to true; REMOVE WHEN READY CODED!!!!!
        // motiPrefs.edit().putBoolean("onboarding_complete",false).apply();

        setContentView(R.layout.main);

        // Set Moti-Image
        String moti_indicator = motiPrefs.getString("moti", "egg1");
        System.out.println(moti_indicator + " wird angezeigt.");
        moti = findViewById(R.id.iv_moti);
        try {
            moti.setImageResource(getResources().getIdentifier(moti_indicator,"drawable",getPackageName()));
        } catch (Exception e) {
            System.out.println("Moti image not found. Change filename.");
        }

        // initializes ProgressBar, Step-TextView, Reset-Button and onOff-Switch
        steps = findViewById(R.id.tv_steps);

        // initializes Journal-Button
        journal = findViewById(R.id.ib_journal);
        journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StepcounterActivity.this, JournalActivity.class));
            }
        });

        // setup of the stepcounter
        checkIn();
    }


    /**
     * Initializes the step counter
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
     * @param sensorEvent
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // store values (steps) from sensor
        float[] values = sensorEvent.values;
        // cast values to int
        int _steps = (int) values[0];
        // ... and puts it into local var
        trackedSteps = _steps;
        // call SharedPrefs "savedSteps"
        SharedPreferences stepPrefs = getSharedPreferences("savedSteps", Context.MODE_PRIVATE);
        // subtract stored steps from tacked steps
        _steps = _steps - stepPrefs.getInt("lastValue", 0);

        /** Check if new day */
        SharedPreferences motiPrefs =  getSharedPreferences("motiPrefs", MODE_PRIVATE);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String today = myFormatter.format(now);
        String lastBoot =  motiPrefs.getString("lastBoot", today); // if no value, set today as initial value
        if(today.equals(lastBoot)) {
            
        } else {

        }
        if(motiPrefs.getBoolean("newDay", true)) {
            // update shared prefs
            updateSharedPrefs(this, trackedSteps);
            // initialized editor for SharedPrefs
            SharedPreferences.Editor myEditor = motiPrefs.edit();
            // puts new value to SharedPrefs
            myEditor.putBoolean("newDay", false);
            myEditor.apply();
        }
        System.out.println("---------------------- NEW DAY =  " + motiPrefs.getBoolean("newDay", true));
        // put steps into TextView
        this.steps.setText(String.format(Locale.US, "%d", _steps));
    }

    /**
     * Stores the hand over value into SharedPrefs
     * @param context
     * @param lastSteps
     */
    public static void updateSharedPrefs(Context context, int lastSteps) {
        // calls SharedPrefs "savedSteps"
        SharedPreferences updatePrefs = context.getSharedPreferences("savedSteps", Context.MODE_PRIVATE);
        // initialized editor for SharedPrefs
        SharedPreferences.Editor myEditor = updatePrefs.edit();
        // puts new value to SharedPrefs
        myEditor.putInt("lastValue", lastSteps);
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
