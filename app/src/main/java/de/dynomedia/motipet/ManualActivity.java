package de.dynomedia.motipet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ManualActivity extends AppCompatActivity {

    ImageButton arrow, x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual);

        arrow = findViewById(R.id.ib_arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalActivity.setAniMode("noAni");
                startActivity(new Intent(ManualActivity.this, JournalActivity.class));
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

    }

}
