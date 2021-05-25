package de.dynomedia.motipet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class OnboardingFragment3 extends AppCompatActivity {

    ImageButton ib_arrow_l, ib_arrow_r;
    boolean left;

    @Override
    public void finish() {
        super.finish();
        if (left) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_screen3);

        ib_arrow_l = findViewById(R.id.ib_arrow_l);
        ib_arrow_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = false;
                startActivity(new Intent(OnboardingFragment3.this, OnboardingFragment2.class));
                finish();
            }
        });

        ib_arrow_r = findViewById(R.id.ib_arrow_r);
        ib_arrow_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = true;
                startActivity(new Intent(OnboardingFragment3.this, OnboardingFragment4.class));
                finish();
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
                    left = true;
                    Intent i = new Intent(OnboardingFragment3.this, OnboardingFragment4.class);
                    startActivity(i);
                    finish();
                // swipe back
                } else if (x1 < x2) {
                    left = false;
                    Intent i = new Intent(OnboardingFragment3.this, OnboardingFragment2.class);
                    startActivity(i);
                    finish();
                }
                break;
        }
        return false;
    }
}
