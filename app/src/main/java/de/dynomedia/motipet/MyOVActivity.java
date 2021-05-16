package de.dynomedia.motipet;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MyOVActivity extends AppCompatActivity {

    ImageButton x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myoverview);

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
    }
}
