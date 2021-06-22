package de.dynomedia.motipet;

import android.annotation.SuppressLint;
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
    TextView tv_height, tv_weight, tv_conn, tv_calories;
    EditText et_height, et_weight;
    Button take, bt2;
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
                finish();
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
        et_height = findViewById(R.id.et_tipps);
        et_height.setText(myPrefs.getString("height", "173"));
        et_weight = findViewById(R.id.et_weight);
        et_weight.setText(myPrefs.getString("weight", "80.2"));
        tv_height = findViewById(R.id.tv_height);
        tv_weight = findViewById(R.id.tv_weight);
        tv_calories = findViewById(R.id.tv_calories);
        sw1 = findViewById(R.id.sw1);

        // Setup Button; set color
        bt2 = findViewById(R.id.bt2);
        tv_conn = findViewById(R.id.tv_conn);
        if(myPrefs.getBoolean("countSteps", true)) {
            bt2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone, 0, 0, 0);
            bt2.setBackgroundColor(bt2.getContext().getResources().getColor(R.color.blue));
            bt2.setTextColor(bt2.getContext().getResources().getColor(R.color.white));
            bt2.setText("Schrittzähler trennen");
            enableFields();
            tv_conn.setVisibility(View.VISIBLE);
            sw1.setFocusableInTouchMode(true);
            sw1.setClickable(true);
            sw1.setAlpha(1);
            tv_calories.setAlpha(1);
        } else {
            bt2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone_dark, 0, 0, 0);
            bt2.setBackgroundColor(bt2.getContext().getResources().getColor(R.color.lightgreen));
            bt2.setTextColor(bt2.getContext().getResources().getColor(R.color.darkgrey));
            bt2.setText("Schrittzähler verbinden");
            disableFields();
            tv_conn.setVisibility(View.INVISIBLE);
            sw1.setFocusableInTouchMode(false);
            sw1.setClickable(false);
            sw1.setAlpha(0.5f);
            tv_calories.setAlpha(0.5f);
        }
        bt2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(myPrefs.getBoolean("countSteps", true)) {
                    bt2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone_dark, 0, 0, 0);
                    bt2.setBackgroundColor(bt2.getContext().getResources().getColor(R.color.lightgreen));
                    bt2.setTextColor(bt2.getContext().getResources().getColor(R.color.darkgrey));
                    bt2.setText("Schrittzähler verbinden");
                    tv_conn.setVisibility(View.INVISIBLE);
                    disableFields();
                    myPrefs.edit().putBoolean("countSteps", false).apply();
                    sw1.setFocusableInTouchMode(false);
                    sw1.setClickable(false);
                    sw1.setAlpha(0.5f);
                    tv_calories.setAlpha(0.5f);
                } else {
                    bt2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone, 0, 0, 0);
                    bt2.setBackgroundColor(bt2.getContext().getResources().getColor(R.color.blue));
                    bt2.setTextColor(bt2.getContext().getResources().getColor(R.color.white));
                    bt2.setText("Schrittzähler trennen");
                    tv_conn.setVisibility(View.VISIBLE);
                    enableFields();
                    myPrefs.edit().putBoolean("countSteps", true).apply();
                    sw1.setFocusableInTouchMode(true);
                    sw1.setClickable(false);
                    sw1.setAlpha(1);
                    tv_calories.setAlpha(1);
                }
            }
        });

        // Setup Switch and Fields
        if(myPrefs.getBoolean("calcCalories", true) && myPrefs.getBoolean("countSteps", true)) {
            sw1.setChecked(true);
            enableFields();
        } else {
            sw1.setChecked(false);
            disableFields();
        }

        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(sw1.isChecked()) {
                   myPrefs.edit().putBoolean("calcCalories", true).apply();
                   sw1.setChecked(true);
                   enableFields();
               } else {
                   myPrefs.edit().putBoolean("calcCalories", false).apply();
                   sw1.setChecked(false);
                   disableFields();
               }
           }
        });

        /**
         * Saves height and weight.
         * Sets height group a our b.
         */
        take = findViewById(R.id.bt_otions);
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

    private void enableFields() {
        et_height.setAlpha(1);
        tv_height.setAlpha(1);
        et_height.setFocusableInTouchMode(true);
        et_height.setClickable(true);
        et_height.setCursorVisible(true);
        et_weight.setAlpha(1);
        tv_weight.setAlpha(1);
        et_weight.setFocusableInTouchMode(true);
        et_weight.setClickable(true);
        et_weight.setCursorVisible(true);
    }

    private void disableFields() {
        et_height.setAlpha(0.5f);
        tv_height.setAlpha(0.5f);
        et_height.setFocusableInTouchMode(false);
        et_height.setClickable(false);
        et_height.setCursorVisible(false);
        et_weight.setAlpha(0.5f);
        tv_weight.setAlpha(0.5f);
        et_weight.setFocusableInTouchMode(false);
        et_weight.setClickable(false);
        et_weight.setCursorVisible(false);
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
