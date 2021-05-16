package de.dynomedia.motipet;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class EvaluationActivity extends AppCompatActivity {

    ImageButton x, harald;
    Button bt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluation);

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

        bt2 = findViewById(R.id.bt2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EvaluationActivity.this, MyOVActivity.class));
            }
        });

    }
}
