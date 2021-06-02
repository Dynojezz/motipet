package de.dynomedia.motipet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Eval_MyOVActivity extends AppCompatActivity {

    ImageButton x;

    private ArrayList<FitnessValue> resultList;
    private RecyclerView recyclerView;

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
        recyclerView = findViewById(R.id.rv_results);

        resultList = new ArrayList<>();
        setFitnessValues();
        setAdapter();
    }
    private void setAdapter() {
        RecyclerAdapter adapter = new RecyclerAdapter(resultList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setFitnessValues() {
        resultList.add(new FitnessValue("x", "123456", "3,5", "5000"));
        resultList.add(new FitnessValue("y", "1000", "1", "10"));
        resultList.add(new FitnessValue("z", "3456", "1,2", "3500"));

        String day, steps, distance, calories;
        SQLiteDatabase motiLog = openOrCreateDatabase("motiLog.db", MODE_PRIVATE, null);
        Cursor myCursor = motiLog.rawQuery("SELECT dayNR, dailysteps, dailydistance, dailycalories FROM day", null);
        myCursor.moveToFirst();
        int numberRows = myCursor.getCount();
        while(myCursor.moveToNext()) { //returns false already past the last row
            day = myCursor.getString(0);
            steps = myCursor.getString(1);
            distance = myCursor.getString(2);
            calories = myCursor.getString(3);
            resultList.add(new FitnessValue(day, steps, distance, calories));
        }
        myCursor.close();
        motiLog.close();
    }
}
