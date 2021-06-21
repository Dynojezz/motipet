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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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

    private ImageView iv_moti, sync, iv_progressbar, iv_note, iv_distance, iv_calories;
    private TextView tv_day, tv_steps, tv_distance, tv_calories, tv_name, tv_lv, tv_st, tv_info;
    private Button bt_ok;
    private ImageButton ib_journal;
    private EditText et_name;

    private SensorManager sm;
    private Sensor s;

    final RxPermissions rxPermissions = new RxPermissions(this);

    String name;
    int moti_steps, lv, st, current_steps;
    boolean playMusic = false;

    // Animation
    Animation animUpDown;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //start service and play music
        startService(new Intent(StepcounterActivity.this, SoundService.class));

        // Get the shared preferences
        SharedPreferences myPrefs =  getSharedPreferences("myPrefs", MODE_PRIVATE);

        /** Check if onboarding_complete is false */
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

        // initializes journal-Button
        ib_journal = findViewById(R.id.ib_journal);
        ib_journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic = true;
                startActivity(new Intent(StepcounterActivity.this, JournalActivity.class));
            }
        });


        // initializes views
        tv_day = findViewById(R.id.tv_day);
        tv_steps = findViewById(R.id.tv_steps);
        tv_distance = findViewById(R.id.tv_distance);
        iv_distance = findViewById(R.id.iv_distance);
        tv_calories = findViewById(R.id.tv_calories);
        iv_calories = findViewById(R.id.iv_calories);
        iv_moti = findViewById(R.id.iv_moti);
        tv_name = findViewById(R.id.tv_name);
        tv_lv = findViewById(R.id.tv_lv);
        tv_st = findViewById(R.id.tv_st);
        iv_progressbar = findViewById(R.id.iv_progressbar);
        et_name = findViewById(R.id.et_name);

        if(myPrefs.getBoolean("info_note",true)) {
            iv_note = findViewById(R.id.iv_note);
            iv_note.setVisibility(View.VISIBLE);
            tv_info = findViewById(R.id.tv_info);
            tv_info.setVisibility(View.VISIBLE);
            bt_ok = findViewById(R.id.bt_ok);
            bt_ok.setVisibility(View.VISIBLE);
            bt_ok.setOnClickListener(new View.OnClickListener() {
                int counter = 1;

                @Override
                public void onClick(View v) {
                    if (counter == 1) {
                        tv_info.setText("Zwischendurch kannst du immer mal unten auf den Fortschrittsbalken schauen. Sobald dieser voll ist, entwickelt sich dein Moti weiter!");
                        counter++;
                    } else if (counter == 2) {
                        tv_info.setText("");
                        tv_info.setVisibility(View.GONE);
                        bt_ok.setVisibility(View.GONE);
                        iv_note.setVisibility(View.GONE);
                        myPrefs.edit().putBoolean("info_note", false).apply();
                    }
                }
            });
        }

        // load the animation
        animUpDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_down);
        animUpDown.setRepeatCount(Animation.INFINITE);

        // JUST FOR TEST
        sync = findViewById(R.id.iv_sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // starts the service manually
                Calendar calendar = Calendar.getInstance();
                startService(new Intent(StepcounterActivity.this, StepService.class));
                Log.d("---------------> INFO", "Manually service start at " + calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                        calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
                updateView();
            }
        });

        // load values to the view
        updateView();

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
     * Loads the saved values into the view.
     */
    private void updateView() {
        // Get the shared preferences
        SharedPreferences myPrefs =  getSharedPreferences("myPrefs", MODE_PRIVATE);


        /** Load Moti Img*/
        updateMoti();

        /** Load Moti Name*/
        SQLiteDatabase motiLog = openOrCreateDatabase("motiLog.db", MODE_PRIVATE, null);
        Cursor cursorName = motiLog.rawQuery("SELECT * FROM moti WHERE motiID = '1'", null);    // CHANGE ID!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        cursorName.moveToFirst();
        if (cursorName.getCount() == 1) {
            name = cursorName.getString(1);
        }
        tv_name.setText(name);
        cursorName.close();
        // close db
        motiLog.close();
        iv_moti.startAnimation(animUpDown);
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
            myPrefs.edit().putBoolean("firstStart", false).apply();
        }
        // subtract stored steps from tacked steps
        _steps = _steps - myPrefs.getInt("lastValue", 0);
        current_steps = _steps;

        // put steps into TextView
        this.tv_steps.setText(String.format(Locale.GERMANY, "%d", _steps));
        // put distance into TextView
        if(myPrefs.getBoolean("calcCalories", true)) {
            this.tv_distance.setText(SettingsActivity.getDistance(this, _steps));
            // put distance into TextView
            this.tv_calories.setText(SettingsActivity.getCalories(this, _steps));
        } else {
            this.tv_distance.setVisibility(View.INVISIBLE);
            this.iv_distance.setVisibility(View.INVISIBLE);
            this.tv_calories.setVisibility(View.INVISIBLE);
            this.iv_calories.setVisibility(View.INVISIBLE);
        }

        updateProgressBar();
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
        playMusic = false;
        super.onResume();
        // load values to the view
        updateView();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    /**
     * Loads the saved values into the view.
     */
    private void updateProgressBar() {
        SQLiteDatabase motiLog = openOrCreateDatabase("motiLog.db", MODE_PRIVATE, null);
        /** Calc Moti Lv from steps*/

        motiLog.execSQL("UPDATE moti SET steps ='1000' WHERE motiID = '1'"); // JUST FOR TEST!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        //myPrefs.edit().putBoolean("set_name", true).apply(); // JUST FOR TEST!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        Cursor cursorMoti = motiLog.rawQuery("SELECT steps FROM moti WHERE motiID = '1'", null);  // CHANGE ID!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        cursorMoti.moveToFirst();
        if (cursorMoti.getCount() == 1) {
            lv = cursorMoti.getInt(0) ;
            lv = (lv + current_steps) /1000;
        }
        cursorMoti.close();
        motiLog.execSQL("UPDATE moti SET lv ='"+lv+"' WHERE motiID = '1'"); // CHANGE ID!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // put moti lv to textview
        Cursor cursorMoti2 = motiLog.rawQuery("SELECT lv FROM moti WHERE motiID = '1'", null);  // CHANGE ID!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        cursorMoti2.moveToFirst();
        if (cursorMoti2.getCount() == 1) {
            lv = cursorMoti2.getInt(0);
        }
        tv_lv.setText("Lv " + lv);
        cursorMoti2.close();

        /** Open name note*/
        if (lv == 1) {
            setName();
        }

        /** Calc St from Lv*/
        if(lv >= 200) {
            tv_st.setText("St " + 5);
        } else if (lv >= 100) {
            tv_st.setText("St " + 4);
        } else if (lv >= 50) {
            tv_st.setText("St " + 3);
        } else if (lv >= 15) {
            tv_st.setText("St " + 2);
        } else if (lv >= 1) {
            tv_st.setText("St " + 1);
        } else if (lv >= 0) {
            tv_st.setText("St " + 0);
        }
        /** Update Moti*/
        updateMoti();

        /** Setup progress bar*/
        Cursor cursorMoti3 = motiLog.rawQuery("SELECT steps FROM moti WHERE motiID = '1'", null);  // CHANGE ID!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        cursorMoti3.moveToFirst();
        if (cursorMoti3.getCount() == 1) {
            moti_steps = cursorMoti3.getInt(0) + current_steps;
        }
        cursorMoti3.close();
        // close db
        motiLog.close();

        /** Check if envolve*/
        //if(moti_steps == 1000 || moti_steps == 15000 || moti_steps == 50000 || moti_steps == 100000 || moti_steps == 200000) {
        //    updateMoti();
        //}

        if(lv >= 200) {
            iv_progressbar.setImageResource(getResources().getIdentifier("pb_10","drawable",getPackageName()));
        } else if (lv >= 100) {
            //St 4; update each 10.000 steps
            if (moti_steps >= 200000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_10","drawable",getPackageName()));
            } else if (moti_steps >= 190000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_09","drawable",getPackageName()));
            } else if (moti_steps >= 180000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_08","drawable",getPackageName()));
            } else if (moti_steps >= 170000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_07","drawable",getPackageName()));
            } else if (moti_steps >= 160000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_06","drawable",getPackageName()));
            } else if (moti_steps >= 150000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_05","drawable",getPackageName()));
            } else if (moti_steps >= 140000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_04","drawable",getPackageName()));
            } else if (moti_steps >= 130000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_03","drawable",getPackageName()));
            } else if (moti_steps >= 120000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_02","drawable",getPackageName()));
            } else if (moti_steps >= 110000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_01","drawable",getPackageName()));
            } else if (moti_steps >= 100000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_00","drawable",getPackageName()));
            }
        } else if (lv >= 50) {
            //St 3; update each 5.000 steps
            if (moti_steps >= 100000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_10","drawable",getPackageName()));
            } else if (moti_steps >= 95000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_09","drawable",getPackageName()));
            } else if (moti_steps >= 90000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_08","drawable",getPackageName()));
            } else if (moti_steps >= 85000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_07","drawable",getPackageName()));
            } else if (moti_steps >= 80000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_06","drawable",getPackageName()));
            } else if (moti_steps >= 75000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_05","drawable",getPackageName()));
            } else if (moti_steps >= 70000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_04","drawable",getPackageName()));
            } else if (moti_steps >= 65000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_03","drawable",getPackageName()));
            } else if (moti_steps >= 60000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_02","drawable",getPackageName()));
            } else if (moti_steps >= 55000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_01","drawable",getPackageName()));
            } else if (moti_steps >= 50000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_00","drawable",getPackageName()));
            }
        } else if (lv >= 15) {
            //St 2; update each 3.500 steps
            if (moti_steps >= 50000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_10","drawable",getPackageName()));
            } else if (moti_steps >= 46500) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_09","drawable",getPackageName()));
            } else if (moti_steps >= 43000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_08","drawable",getPackageName()));
            } else if (moti_steps >= 39500) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_07","drawable",getPackageName()));
            } else if (moti_steps >= 36000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_06","drawable",getPackageName()));
            } else if (moti_steps >= 32500) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_05","drawable",getPackageName()));
            } else if (moti_steps >= 29000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_04","drawable",getPackageName()));
            } else if (moti_steps >= 25500) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_03","drawable",getPackageName()));
            } else if (moti_steps >= 22000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_02","drawable",getPackageName()));
            } else if (moti_steps >= 18500) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_01","drawable",getPackageName()));
            } else if (moti_steps >= 15000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_00","drawable",getPackageName()));
            }
        } else if (lv >= 1) {
            //St 1; update each 1.400 steps
            if (moti_steps >= 15000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_10","drawable",getPackageName()));
            } else if (moti_steps >= 13600) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_09","drawable",getPackageName()));
            } else if (moti_steps >= 12200) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_08","drawable",getPackageName()));
            } else if (moti_steps >= 10800) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_07","drawable",getPackageName()));
            } else if (moti_steps >= 9400) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_06","drawable",getPackageName()));
            } else if (moti_steps >= 8000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_05","drawable",getPackageName()));
            } else if (moti_steps >= 6600) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_04","drawable",getPackageName()));
            } else if (moti_steps >= 5200) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_03","drawable",getPackageName()));
            } else if (moti_steps >= 3800) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_02","drawable",getPackageName()));
            } else if (moti_steps >= 2400) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_01","drawable",getPackageName()));
            } else if (moti_steps >= 1000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_00","drawable",getPackageName()));
            }
        } else if (lv >= 0) {
            //St 0; update each 100 steps
            if (moti_steps >= 1000) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_10","drawable",getPackageName()));
            } else if (moti_steps >= 900) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_09","drawable",getPackageName()));
            } else if (moti_steps >= 800) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_08","drawable",getPackageName()));
            } else if (moti_steps >= 700) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_07","drawable",getPackageName()));
            } else if (moti_steps >= 600) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_06","drawable",getPackageName()));
            } else if (moti_steps >= 500) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_05","drawable",getPackageName()));
            } else if (moti_steps >= 400) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_04","drawable",getPackageName()));
            } else if (moti_steps >= 300) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_03","drawable",getPackageName()));
            } else if (moti_steps >= 200) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_02","drawable",getPackageName()));
            } else if (moti_steps >= 100) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_01","drawable",getPackageName()));
            } else if (moti_steps >= 0) {
                iv_progressbar.setImageResource(getResources().getIdentifier("pb_00","drawable",getPackageName()));
            }
        }
    }

    private void updateMoti() {

        /** Load Moti Image */
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String moti_indicator = "egg";
        if(lv >= 200) {
            moti_indicator = "adult" + myPrefs.getInt("pattern", 1);
        } else if (lv >= 100) {
            moti_indicator = "teen" + myPrefs.getInt("pattern", 1);
            iv_moti.setPadding(50,150,180,30);
        } else if (lv >= 50) {
            moti_indicator = "child" + myPrefs.getInt("pattern", 1);
            iv_moti.setPadding(100,150,100,30);
        } else if (lv >= 15) {
            moti_indicator = "toddler" + myPrefs.getInt("pattern", 1);
            iv_moti.setPadding(200,150,200,30);
        } else if (lv >= 1) {
            moti_indicator = "baby" + myPrefs.getInt("pattern", 1);
            iv_moti.setPadding(200,30,200,30);
        } else if (lv >= 0) {
            moti_indicator = "egg" + myPrefs.getInt("pattern", 1);
            iv_moti.setPadding(100,30,100,30);
        }
        iv_moti.setImageResource(getResources().getIdentifier(moti_indicator,"drawable",getPackageName()));

        /** Load Day Nr.*/
        int current_dayNR = myPrefs.getInt("dayNR", 0) + 1;
        tv_day.setText("Tag " + current_dayNR);

    }

    private void setName() {
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        if(myPrefs.getBoolean("set_name",true)) {
            iv_note = findViewById(R.id.iv_note);
            iv_note.setVisibility(View.VISIBLE);
            tv_info = findViewById(R.id.tv_info);
            tv_info.setText("Herzlichen Glückwunsch, dein Moti ist geschlüpft! Wie möchtest du es nennen?");
            tv_info.setVisibility(View.VISIBLE);
            et_name.findViewById(R.id.et_name);
            et_name.setVisibility(View.VISIBLE);
            bt_ok = findViewById(R.id.bt_ok);
            bt_ok.setVisibility(View.VISIBLE);
            bt_ok.setOnClickListener(new View.OnClickListener() {
                int counter = 1;
                String moti_name = "Peterchen";
                @Override
                public void onClick(View v) {
                    if (counter == 1) {
                        moti_name = et_name.getText().toString();
                        SQLiteDatabase motiLog = openOrCreateDatabase("motiLog.db", MODE_PRIVATE, null);
                        motiLog.execSQL("UPDATE moti SET name ='"+moti_name+"' WHERE motiID = '1'");    // CHANGE ID!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        tv_info.setText("Hallo " + moti_name + " :) Schon neugierig, wie dein Moti sich entwickeln wird? Dann heißt es fleißig schritte sammeln!");
                        et_name.setVisibility(View.GONE);
                        updateView();
                        counter++;
                        motiLog.close();
                    } else if (counter == 2) {
                        tv_info.setText("Bei 1000 Schritten steigt dein Moti 1 Level (Lv) auf. Hast du ein bestimmtes Level erreicht, entwickelt sich dein Moti zum nächsten Stadium (St).");
                        counter++;
                    } else if (counter == 3) {
                        tv_info.setText("Für eine genauere Erklärung, Tipps, App-Einstellungen und zur Anzeige deiner Fitness Auswertungen schau mal im Journal vorbei :)");
                        counter++;
                    } else if (counter == 4) {
                        tv_info.setText("");
                        tv_info.setVisibility(View.GONE);
                        bt_ok.setVisibility(View.GONE);
                        iv_note.setVisibility(View.GONE);
                        myPrefs.edit().putBoolean("set_name", false).apply();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        //stop service and stop music
        stopService(new Intent(StepcounterActivity.this, SoundService.class));
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        //stop service and stop music
        if (! playMusic) {
            stopService(new Intent(StepcounterActivity.this, SoundService.class));
        }
        super.onPause();
    }
}
