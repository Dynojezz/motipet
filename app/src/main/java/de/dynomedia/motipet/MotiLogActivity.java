package de.dynomedia.motipet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MotiLogActivity extends AppCompatActivity {

    ImageButton x, arrow;
    ImageView info;
    TextView tv_moti_day, tv_moti_st, tv_moti_lv, tv_moti_distance, tv_moti_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motilog);

        DisplayMetrics myDM = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(myDM);

        int width = myDM.widthPixels;
        int height = myDM.heightPixels;

        getWindow().setLayout((int)(width*.99), (int)(height*.99));

        arrow = findViewById(R.id.ib_arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalActivity.setAniMode("noAni");
                startActivity(new Intent(MotiLogActivity.this, EvaluationActivity.class));
                finish();
            }
        });

        info = findViewById(R.id.iv_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MotiLogActivity.this, ManualActivity.class));
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

        tv_moti_day = findViewById(R.id.tv_moti_day);
        tv_moti_st = findViewById(R.id.tv_moti_st);
        tv_moti_lv = findViewById(R.id.tv_moti_lv);
        tv_moti_distance = findViewById(R.id.tv_moti_distance);
        tv_moti_name = findViewById(R.id.tv_moti_name);
        int steps = 0;
        int lv = 0;

        SQLiteDatabase motiLog = openOrCreateDatabase("motiLog.db", MODE_PRIVATE, null);
        Cursor myCursor = motiLog.rawQuery("SELECT steps FROM moti WHERE motiID = '1'", null);    // CHANGE ID!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        myCursor.moveToFirst();
        if (myCursor.getCount() == 1) {
            steps = myCursor.getInt(0);
        }
        myCursor.close();

        // put moti lv to textview
        Cursor cursorMoti2 = motiLog.rawQuery("SELECT lv FROM moti WHERE motiID = '1'", null);  // CHANGE ID!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        cursorMoti2.moveToFirst();
        if (cursorMoti2.getCount() == 1) {
            lv = cursorMoti2.getInt(0);
        }
        tv_moti_lv.setText("Lv " + lv);

        // close db
        motiLog.close();
        tv_moti_distance.setText(SettingsActivity.getDistance(this, lv*1000) + " KM");

        /** Calc St from Lv*/
        if(lv >= 200) {
            tv_moti_st.setText("St " + 5);
        } else if (lv >= 100) {
            tv_moti_st.setText("St " + 4);
        } else if (lv >= 50) {
            tv_moti_st.setText("St " + 3);
        } else if (lv >= 15) {
            tv_moti_st.setText("St " + 2);
        } else if (lv >= 1) {
            tv_moti_st.setText("St " + 1);
        } else if (lv >= 0) {
            tv_moti_st.setText("St " + 0);
        }

        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        int current_dayNR = myPrefs.getInt("dayNR", 0) + 1;
        tv_moti_day.setText("Tag " + current_dayNR);
        String currentName = myPrefs.getString("moti_name", "Moti");
        tv_moti_name.setText(currentName);
    }
}
