package de.dynomedia.motipet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/**
 * This class stores the tracked steps each day at 0:00.
 * We need a Broadcast Receiver to fire the alarm when our app is not running.
 */

//class extending the Broadcast Receiver
public class StepAlarm extends BroadcastReceiver {

    //the method will be fired when the alarm is triggerred
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("----------------- ALARM! ---------------- ALARM!");
        SharedPreferences stepPrefs = context.getSharedPreferences("savedSteps", Context.MODE_PRIVATE);
        // initialized editor for SharedPrefs
        SharedPreferences.Editor myEditor = stepPrefs.edit();
        // puts new value to SharedPrefs
        myEditor.putInt("lastValue", -10000);
        myEditor.apply();
        System.out.println("----------------- ALARM! ---------------- ALARM! Neuer Wert: " + stepPrefs.getInt("lastValue", -10000));
        Log.d("MyAlarm", "Alarm just fired");
    }

}
