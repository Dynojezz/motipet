package de.dynomedia.motipet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class EggsActivity extends AppCompatActivity {

    float x1, y1, x2, y2;
    String motiPattern;
    ImageView iv_egg1, iv_egg2, iv_egg3, iv_egg4, iv_egg5, iv_egg6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eggs);

        iv_egg1 = findViewById(R.id.iv_egg1);
        iv_egg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the shared preferences
                SharedPreferences preferences = getSharedPreferences("motiPrefs", MODE_PRIVATE);
                // set egg type
                preferences.edit().putString("moti","egg1").apply();
                // set color pattern
                motiPattern = "pattern2";
                // make new database entry
                pushToMotiLog("egg1");
                // finish onboarding
                finishOnboarding();
            }
        });

        iv_egg2 = findViewById(R.id.iv_egg2);
        iv_egg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("motiPrefs", MODE_PRIVATE);
                preferences.edit().putString("moti","egg2").apply();
                motiPattern = "pattern2";
                pushToMotiLog("egg2");
                finishOnboarding();
            }
        });

        iv_egg3 = findViewById(R.id.iv_egg3);
        iv_egg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("motiPrefs", MODE_PRIVATE);
                preferences.edit().putString("moti","egg3").apply();
                motiPattern = "pattern3";
                pushToMotiLog("egg3");
                finishOnboarding();
            }
        });

        iv_egg4 = findViewById(R.id.iv_egg4);
        iv_egg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("motiPrefs", MODE_PRIVATE);
                preferences.edit().putString("moti","egg4").apply();
                motiPattern = "pattern4";
                pushToMotiLog("egg4");
                finishOnboarding();
            }
        });

        iv_egg5 = findViewById(R.id.iv_egg5);
        iv_egg5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("motiPrefs", MODE_PRIVATE);
                preferences.edit().putString("moti","egg5").apply();
                motiPattern = "pattern5";
                pushToMotiLog("egg5");
                finishOnboarding();
            }
        });

        iv_egg6 = findViewById(R.id.iv_egg6);
        iv_egg6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("motiPrefs", MODE_PRIVATE);
                preferences.edit().putString("moti","egg6").apply();
                motiPattern = "pattern6";
                pushToMotiLog("egg6");
                finishOnboarding();
            }
        });
    }

    private void pushToMotiLog(String motiIndicator) {
        SQLiteDatabase motiLog = openOrCreateDatabase("motiLog.db", MODE_PRIVATE, null); //null == standard cursor for databases
        if(motiPattern.equals("pattern1")) {
            motiLog.execSQL("INSERT INTO moti VALUES('Moti Ei', 'pattern1', '1', '0', '0', '0', '0.0', '0')");
            System.out.println("Moti mit pattern1 gespeichert.");

        } else if (motiPattern.equals("pattern2")) {
            motiLog.execSQL("INSERT INTO moti VALUES('Moti Ei', 'pattern2', '1', '0', '0', '0', '0.0', '0')");
            System.out.println("Moti mit pattern2 gespeichert.");

        } else if (motiPattern.equals("pattern3")) {
            motiLog.execSQL("INSERT INTO moti VALUES('Moti Ei', 'pattern3', '1', '0', '0', '0', '0.0', '0')");
            System.out.println("Moti mit pattern3 gespeichert.");

        } else if (motiPattern.equals("pattern4")) {
            motiLog.execSQL("INSERT INTO moti VALUES('Moti Ei', 'pattern4', '1', '0', '0', '0', '0.0', '0')");
            System.out.println("Moti mit pattern4 gespeichert.");

        } else if (motiPattern.equals("pattern5")) {
            motiLog.execSQL("INSERT INTO moti VALUES('Moti Ei', 'pattern5', '1', '0', '0', '0', '0.0', '0')");
            System.out.println("Moti mit pattern5 gespeichert.");

        } else {
            motiLog.execSQL("INSERT INTO moti VALUES('Moti Ei', 'pattern6', '1', '0', '0', '0', '0.0', '0')");
            System.out.println("Moti mit pattern6 gespeichert.");

        }
    }


    /**
     *
     * @param touchevent
     * @return
     */
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchevent.getX();
                y2 = touchevent.getY();
                // swipe forward
                if (x1 > x2) {
                }
                // swipe back
                else if (x1 < x2) {
                    Intent i = new Intent(EggsActivity.this, OnboardingFragment5.class);
                    startActivity(i);
                    finish();
                }
                break;
        }
        return false;
    }

    /**
     *
     */
    private void finishOnboarding() {
        // Get the shared preferences
        SharedPreferences preferences = getSharedPreferences("motiPrefs", MODE_PRIVATE);

        // Set onboarding_complete to true
        preferences.edit().putBoolean("onboarding_complete",true).apply();

        // Launch the main Activity, called StepcounterActivity
        Intent main = new Intent(this, StepcounterActivity.class);
        startActivity(main);

        // Close the OnboardingActivity
        finish();
    }
}
