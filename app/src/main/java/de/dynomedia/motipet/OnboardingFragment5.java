package de.dynomedia.motipet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class OnboardingFragment5 extends AppCompatActivity {

    ImageButton ib_arrow_l;
    Button bt_ok;
    boolean left;

    @Override
    public void finish() {
        super.finish();
        if (left) {
            // default animation
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_screen5);

        ib_arrow_l = findViewById(R.id.ib_arrow_l);
        ib_arrow_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = false;
                startActivity(new Intent(OnboardingFragment5.this, OnboardingFragment4.class));
                finish();
            }
        });

        bt_ok = findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = true;
                startActivity(new Intent(OnboardingFragment5.this, EggsActivity.class));
                finish();

                /** Create MotiLog database*/
                SQLiteDatabase motiLog = openOrCreateDatabase("motiLog.db", MODE_PRIVATE, null); //null == standard cursor for databases
                motiLog.execSQL("CREATE TABLE moti (name TEXT, color TEXT, day INTEGER, st INTEGER, lv INTEGER, steps INTEGER, distance DOUBLE, kcal INTEGER)");
                motiLog.execSQL("CREATE TABLE day (id TEXT, name TEXT, nr INTEGER, dailysteps INTEGER, dailydistance DOUBLE, dailykcal INTEGER, date DATE, weekday TEXT)");
            }
        });
    }

    float x1, y1, x2, y2;
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
                    left = false;
                    Intent i = new Intent(OnboardingFragment5.this, OnboardingFragment4.class);
                    startActivity(i);
                    finish();
                }
                break;
        }
        return false;
    }
}
