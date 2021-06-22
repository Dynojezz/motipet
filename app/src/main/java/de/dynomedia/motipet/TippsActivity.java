package de.dynomedia.motipet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TippsActivity extends AppCompatActivity {

    EditText et_tipps;
    Button bt_confirm;

    ImageButton arrow, x;
    ImageView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tipps);

        arrow = findViewById(R.id.ib_arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalActivity.setAniMode("noAni");
                startActivity(new Intent(TippsActivity.this, JournalActivity.class));
                finish();
            }
        });

        info = findViewById(R.id.iv_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TippsActivity.this, ManualActivity.class));
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

        SharedPreferences myPrefs =  getSharedPreferences("myPrefs", MODE_PRIVATE);
        et_tipps = findViewById(R.id.et_tipps);
        et_tipps.setText(myPrefs.getString("tipps", ""));
        bt_confirm = findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // start editor
                SharedPreferences.Editor myEditor = myPrefs.edit();

                // put tipps into shared preferences
                myEditor.putString("tipps", et_tipps.getText().toString());

                // apply changes from editor
                myEditor.apply();

                Toast.makeText(TippsActivity.this,"Tipps gespeichert", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
