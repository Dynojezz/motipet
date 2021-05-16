package de.dynomedia.motipet;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class JournalActivity extends AppCompatActivity {

    ImageButton x, settings, manual, evaluation, trophys, tipps, motilog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                finish();
            }
        });

        settings = findViewById(R.id.ib_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.settings);
                x = findViewById(R.id.ib_x);
                x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });

        manual = findViewById(R.id.ib_manual);
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.manual);
                x = findViewById(R.id.ib_x);
                x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });

        evaluation = findViewById(R.id.ib_evaluation);
        evaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JournalActivity.this, EvaluationActivity.class));
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
                setContentView(R.layout.tipps);
                x = findViewById(R.id.ib_x);
                x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });

        motilog = findViewById(R.id.ib_motilog);
        motilog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.motilog);
                x = findViewById(R.id.ib_x);
                x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });

    }
}
