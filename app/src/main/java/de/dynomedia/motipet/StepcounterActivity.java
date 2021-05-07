package de.dynomedia.motipet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Locale;

public class StepcounterActivity extends AppCompatActivity
        implements SensorEventListener {

    private ProgressBar pb;
    private TextView steps;
    private Button reset;
    private Switch onOff;
    private ImageButton journal;
    private ImageView sync;
    private SensorManager sm;
    private Sensor s;
    private int lastSteps;
    final RxPermissions rxPermissions = new RxPermissions(this);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

        // initializes ProgressBar, Step-TextView, Reset-Button and onOff-Switch
        pb = findViewById(R.id.pb);
        steps = findViewById(R.id.tv_steps);
        reset = findViewById(R.id.reset);
        reset.setOnClickListener((event) -> {
            updateSharedPrefs(this, lastSteps);
            updateUI();
        });
        onOff = findViewById(R.id.on_off);
        onOff.setOnCheckedChangeListener((buttonView, isChecked)
                -> updateUI());
        onOff.setChecked(s != null);
        updateUI();

        // initializes Journal-Button
        journal = findViewById(R.id.ib_journal);
        journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StepcounterActivity.this, JournalActivity.class));
            }
        });

        // initializes Journal-Button
        sync = findViewById(R.id.iv_sync);
        sync.setOnClickListener((event) -> {
            updateUI();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateUI();
    }

    /**
     * Updates SharedPrefs with new Steps and puts value in textview.
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //stores values (steps) from sensor
        float[] values = sensorEvent.values;
        // casts values to int
        int _steps = (int) values[0];
        // ... and puts it into local var
        lastSteps = _steps;
        // calls SharedPrefs "savedSteps"
        SharedPreferences myPrefs = getSharedPreferences("savedSteps", Context.MODE_PRIVATE);
        // subtracts previous steps from current steps
        _steps = _steps - myPrefs.getInt("lastValue", 0);
        // puts steps into TextView
        this.steps.setText(String.format(Locale.US, "%d", _steps));
        // makes ProgressBar invisible and TextViev + Button visible
        if (pb.getVisibility() == View.VISIBLE) {
            pb.setVisibility(View.GONE);
            this.steps.setVisibility(View.VISIBLE);
            reset.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /**
     *
     * @param context
     * @param lastSteps
     */
    public static void updateSharedPrefs(Context context, int lastSteps) {
        // calls SharedPrefs "savedSteps"
        SharedPreferences myPrefs = context.getSharedPreferences("savedSteps", Context.MODE_PRIVATE);
        // initialized editor for SharedPrefs
        SharedPreferences.Editor myEditor = myPrefs.edit();
        //
        myEditor.putInt("lastValue", lastSteps);
        myEditor.apply();
    }

    private void updateUI() {
        reset.setVisibility(View.GONE);
        onOff.setEnabled(s != null);
        if (s != null) {
            steps.setVisibility(View.VISIBLE);
            if (onOff.isChecked()) {
                sm.registerListener(this, s,
                        SensorManager.SENSOR_DELAY_UI);
                pb.setVisibility(View.VISIBLE);
            } else {
                sm.unregisterListener(this);
                pb.setVisibility(View.GONE);
            }
        } else {
            steps.setVisibility(View.VISIBLE);
            steps.setText(R.string.no_sensor);
            pb.setVisibility(View.GONE);
        }
    }
}
