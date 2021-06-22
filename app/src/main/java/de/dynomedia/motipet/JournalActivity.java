package de.dynomedia.motipet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JournalActivity extends AppCompatActivity {

    ImageButton x, settings, manual, evaluation, trophys, tipps, motilog;
    TextView tv_quote;

    public static String aniMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (aniMode == "noAni") {
            setTheme(R.style.Theme_MotiPet_NewPage);
        } else {
            setTheme(R.style.Theme_MotiPet_Journal);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal);

        DisplayMetrics myDM = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(myDM);

        int width = myDM.widthPixels;
        int height = myDM.heightPixels;

        getWindow().setLayout((int)(width*.99), (int)(height*.99));

        x = findViewById(R.id.ib_x);
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aniMode = "ani";
                finish();
            }
        });

        settings = findViewById(R.id.ib_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.style.Theme_MotiPet_NewPage);
                startActivity(new Intent(JournalActivity.this, SettingsActivity.class));
                Handler handler = new Handler();
                finish();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 70);
            }
        });

        manual = findViewById(R.id.ib_manual);
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.style.Theme_MotiPet_NewPage);
                startActivity(new Intent(JournalActivity.this, ManualActivity.class));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 70);
            }
        });

        evaluation = findViewById(R.id.ib_evaluation);
        evaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.style.Theme_MotiPet_NewPage);
                startActivity(new Intent(JournalActivity.this, EvaluationActivity.class));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 70);
            }
        });
        trophys = findViewById(R.id.ib_trophys);
        trophys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        tipps = findViewById(R.id.ib_tipps);
        tipps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.style.Theme_MotiPet_NewPage);
                startActivity(new Intent(JournalActivity.this, TippsActivity.class));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 70);
            }
        });

        motilog = findViewById(R.id.ib_motilog);
        motilog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.style.Theme_MotiPet_NewPage);
                startActivity(new Intent(JournalActivity.this, MotiLogActivity.class));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 70);
            }
        });

        tv_quote = findViewById(R.id.tv_quote);

        /** Set new quote */
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        int counter = myPrefs.getInt("quoteNumber", 0);

        if (counter < 75) {
            tv_quote.setText(Quotes.getQuote(counter));
            counter = counter + 1;
            myPrefs.edit().putInt("quoteNumber", counter).apply();
        } else {
            myPrefs.edit().putInt("quoteNumber", 0).apply();
        }
    }

    public static void setAniMode (String mode) {
        aniMode = mode;
    }
}
