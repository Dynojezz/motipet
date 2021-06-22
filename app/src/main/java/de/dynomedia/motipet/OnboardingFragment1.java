package de.dynomedia.motipet;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingFragment1 extends AppCompatActivity {

    ImageButton ib_arrow_r;
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
        setContentView(R.layout.onboarding_screen1);

        // Get the shared preferences
        // SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Set onboarding_complete to true
        // preferences.edit().putBoolean("onboarding_complete",true).apply();

        ib_arrow_r = findViewById(R.id.ib_arrow_r);
        ib_arrow_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = true;
                startActivity(new Intent(OnboardingFragment1.this, OnboardingFragment2.class));
                finish();
            }
        });

    }
    float x1, y1, x2, y2;
    /**
     * Makes the view swipeable
     * @param touchevent The noticed touch event
     * @return false
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
                    left = true;
                    Intent i = new Intent(OnboardingFragment1.this, OnboardingFragment2.class);
                    startActivity(i);
                    finish();
                }
                break;
        }
        return false;
    }
}
