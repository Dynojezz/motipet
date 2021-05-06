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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Locale;

public class StepcounterActivity extends AppCompatActivity
        implements SensorEventListener {

    private static final String PREFS =
            StepcounterActivity.class.getName();

    private static final String PREFS_KEY = "last";

    private ProgressBar pb;
    private TextView steps;
    private Button reset;
    private Switch onOff;

    private ImageButton journal;
    private ImageView sync;

    private SensorManager m;
    private Sensor s;

    private int last;

    final RxPermissions rxPermissions = new RxPermissions(this);

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        rxPermissions
                .request(Manifest.permission.ACTIVITY_RECOGNITION)
                .subscribe(granted -> {
                    if (granted) {

                    } else {
                        Toast.makeText(this, "Schritte werden nicht getrackt. Kann über die Eintstellungen geändert werden.", Toast.LENGTH_LONG).show();
                    }
                });

        pb = findViewById(R.id.pb);
        steps = findViewById(R.id.tv_steps);
        reset = findViewById(R.id.reset);
        reset.setOnClickListener((event) -> {
            updateSharedPrefs(this, last);
            updateUI();
        });
        m = getSystemService(SensorManager.class);
        if (m == null) {
            finish();
        }
        s = m.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        onOff = findViewById(R.id.on_off);
        onOff.setOnCheckedChangeListener((buttonView, isChecked)
                -> updateUI());
        onOff.setChecked(s != null);
        updateUI();

        journal = findViewById(R.id.ib_journal);
        journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StepcounterActivity.this,Journal.class));
            }
        });

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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        int _steps = (int) values[0];
        last = _steps;
        SharedPreferences prefs = getSharedPreferences(
                StepcounterActivity.PREFS,
                Context.MODE_PRIVATE);
        _steps -= prefs.getInt(PREFS_KEY, 0);
        this.steps.setText(String.format(Locale.US,
                "%d", _steps));
        if (pb.getVisibility() == View.VISIBLE) {
            pb.setVisibility(View.GONE);
            this.steps.setVisibility(View.VISIBLE);
            reset.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public static void updateSharedPrefs(Context context,
                                         int last) {
        SharedPreferences prefs =
                context.getSharedPreferences(
                        StepcounterActivity.PREFS,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(StepcounterActivity.PREFS_KEY, last);
        edit.apply();
    }

    private void updateUI() {
        reset.setVisibility(View.GONE);
        onOff.setEnabled(s != null);
        if (s != null) {
            steps.setVisibility(View.VISIBLE);
            if (onOff.isChecked()) {
                m.registerListener(this, s,
                        SensorManager.SENSOR_DELAY_UI);
                pb.setVisibility(View.VISIBLE);
            } else {
                m.unregisterListener(this);
                pb.setVisibility(View.GONE);
            }
        } else {
            steps.setVisibility(View.VISIBLE);
            steps.setText(R.string.no_sensor);
            pb.setVisibility(View.GONE);
        }
    }
}
