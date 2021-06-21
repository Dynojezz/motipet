package de.dynomedia.motipet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class SettingsActivity extends AppCompatActivity {

    ImageButton arrow, info, x;
    TextView tv_height, tv_weight;
    EditText et_height, et_weight;
    Button take;
    Switch sw1;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        et_height = findViewById(R.id.et_name);
        et_height.setText(myPrefs.getString("height", "173"));
        et_weight = findViewById(R.id.et_weight);
        et_weight.setText(myPrefs.getString("weight", "80.2"));
        tv_height = findViewById(R.id.tv_height);
        tv_weight = findViewById(R.id.tv_weight);

        sw1 = findViewById(R.id.sw1);
        if(myPrefs.getBoolean("calcCalories", true)) {
            sw1.setChecked(true);
            et_height.setAlpha(1);
            tv_height.setAlpha(1);
            et_height.setFocusable(View.FOCUSABLE);
            et_height.setClickable(true);
            et_height.setCursorVisible(true);
            et_weight.setAlpha(1);
            tv_weight.setAlpha(1);
            et_weight.setFocusable(View.FOCUSABLE);
            et_weight.setClickable(true);
            et_weight.setCursorVisible(true);
        } else {
            sw1.setChecked(false);
            et_height.setAlpha(0.5f);
            tv_height.setAlpha(0.5f);
            et_height.setFocusable(View.NOT_FOCUSABLE);
            et_height.setClickable(false);
            et_height.setCursorVisible(false);
            et_weight.setAlpha(0.5f);
            tv_weight.setAlpha(0.5f);
            et_weight.setFocusable(View.NOT_FOCUSABLE);
            et_weight.setClickable(false);
            et_weight.setCursorVisible(false);
        }

        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(sw1.isChecked()) {
                   myPrefs.edit().putBoolean("calcCalories", true).apply();
                   sw1.setChecked(true);
                   et_height.setAlpha(1);
                   tv_height.setAlpha(1);
                   et_height.setFocusable(View.FOCUSABLE);
                   et_height.setClickable(true);
                   et_height.setCursorVisible(true);
                   et_weight.setAlpha(1);
                   tv_weight.setAlpha(1);
                   et_weight.setFocusable(View.FOCUSABLE);
                   et_weight.setClickable(true);
                   et_weight.setCursorVisible(true);
               } else {
                   myPrefs.edit().putBoolean("calcCalories", false).apply();
                   sw1.setChecked(false);
                   et_height.setAlpha(0.5f);
                   tv_height.setAlpha(0.5f);
                   et_height.setFocusable(View.NOT_FOCUSABLE);
                   et_height.setClickable(false);
                   et_height.setCursorVisible(false);
                   et_weight.setAlpha(0.5f);
                   tv_weight.setAlpha(0.5f);
                   et_weight.setFocusable(View.NOT_FOCUSABLE);
                   et_weight.setClickable(false);
                   et_weight.setCursorVisible(false);
               }
           }
        });

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

                Toast.makeText(SettingsActivity.this,"Werte aktualisiert", Toast.LENGTH_SHORT).show();
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
