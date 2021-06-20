package de.dynomedia.motipet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class SettingsActivity extends AppCompatActivity {

    ImageButton arrow, info, x;
    EditText et_height, et_weight;
    Button take;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        arrow = findViewById(R.id.ib_arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalActivity.setAniMode("noAni");
                startActivity(new Intent(SettingsActivity.this, JournalActivity.class));
                finish();
            }
        });

        info = findViewById(R.id.iv_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ManualActivity.class));
            }
        });

        x = findViewById(R.id.ib_x);
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Setup EditTexts
        SharedPreferences myPrefs =  getSharedPreferences("myPrefs", MODE_PRIVATE);
        et_height = findViewById(R.id.et_height);
        et_height.setText(myPrefs.getString("height", "173"));
        et_weight = findViewById(R.id.et_weight);
        et_weight.setText(myPrefs.getString("weight", "80.2"));

        /**
         * Saves height and weight.
         * Sets height group a our b.
         */
        take = findViewById(R.id.bt_ok);
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start editor
                SharedPreferences.Editor myEditor = myPrefs.edit();

                // put height and weight into shared preferences
                myEditor.putString("height", et_height.getText().toString());
                myEditor.putString("weight", et_weight.getText().toString());

                // put height-group into shared preferences
                if(Integer.parseInt(et_height.getText().toString()) >= 170) {
                    myEditor.putString("heightGroup", "b");
                } else{
                    myEditor.putString("heightGroup", "a");
                }

                // apply changes from editor
                myEditor.apply();

            }
        });
    }

    /**
     *
     * @param context
     * @param steps
     * @return
     */
    protected static String getDistance(Context context, int steps) {
        SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        // format for rounded decimal-numbers
        DecimalFormat df = new DecimalFormat("0.0");
        String distance;
        // check which height group (average height germany == 173 cm)
        if (myPrefs.getString("heightGroup", "b").equals("b")) {
            // 1 step == 0.7 m == 0.0007 km (70cm) in average
            distance = df.format(steps * 0.0007);
            Log.d("---------------> INFO", "Distance for group b: " + distance);
        }
            else {
            // 1 step == 0.6 m == 0.0006 km (60cm) in average
            distance = df.format(steps * 0.0006);
            Log.d("---------------> INFO", "Distance for group a: " + distance);
            }
            return distance;
        }

    /**
     *
     * @param context
     * @param steps
     * @return
     */
    protected static String getCalories(Context context, int steps) {
        SharedPreferences myPrefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        // format for rounded decimal-numbers
        DecimalFormat df = new DecimalFormat("0");
        String calories;
        float weight = Float.parseFloat(myPrefs.getString("weight", "80.2"));
        // check which height group (average height germany == 173 cm)
        if (myPrefs.getString("heightGroup", "b").equals("b")) {
            // for height-group b: weight x 0.046666 = burned calories per 100 steps
            calories = df.format(((weight * 0.046666)*steps)/100);
            Log.d("---------------> INFO", "Calories for group b: " + calories);
        } else {
            // for height-group a: weight x 0.04 = burned calories per 100 steps
            calories = df.format(((weight * 0.04)*steps)/100);
            Log.d("---------------> INFO", "Calories for group a: " + calories);
        }
        return calories;
    }
}
