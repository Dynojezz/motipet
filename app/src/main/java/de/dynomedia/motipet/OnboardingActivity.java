package de.dynomedia.motipet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class OnboardingActivity extends FragmentActivity {

    private ViewPager pager;
    private TabLayout indicator;
    private Button skip;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding);

        pager = (ViewPager)findViewById(R.id.pager);
        //indicator = (TabLayout)findViewById(R.id.indicator);
        skip = (Button)findViewById(R.id.skip);
        next = (Button)findViewById(R.id.next);

        /**
         * Displays the onboarding screens to pager. Switches between Fragment 1 to 3.
         */
        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return 3; //number of onboarding screens
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    //case 0 : return new OnboardingFragment1();
                    //case 1 : return new OnboardingFragment2();
                    //case 2 : return new OnboardingFragment3();
                    default: return null;
                }
            }
        };
        pager.setAdapter(adapter);
        /**
         * Links the TabLayout to the ViewPager. The individual tabs in the TabLayout are automatically populated with the page titles from the PagerAdapter.
         */
        //indicator.setupWithViewPager(pager);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem() == 2) { // The last screen
                    finishOnboarding();
                } else {
                    pager.setCurrentItem(
                            pager.getCurrentItem() + 1,
                            true
                    );
                }
            }
        });
        /**
         * Sets individual styles for specific pages
         */
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(position == 2){
                    skip.setVisibility(View.GONE);
                    next.setText("Done");
                } else {
                    skip.setVisibility(View.VISIBLE);
                    next.setText("Next");
                }
            }
        });
    }

    /**
     * Finished the onboarding tour.
     */
    private void finishOnboarding() {
        // Get the shared preferences
        SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Set onboarding_complete to true
        preferences.edit().putBoolean("onboarding_complete",true).apply();

        // Launch the main Activity, called StepcounterActivity
        Intent main = new Intent(this, StepcounterActivity.class);
        startActivity(main);

        // Close the OnboardingActivity
        finish();
    }
}
