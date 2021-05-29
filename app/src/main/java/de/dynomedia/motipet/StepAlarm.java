package de.dynomedia.motipet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

/**
 * This class stores the tracked steps each day at 0:00.
 * We need a Broadcast Receiver to fire the alarm when our app is not running.
 */

//class extending the Broadcast Receiver
public class StepAlarm extends BroadcastReceiver {

    private boolean stepsDetected = false;

    //the method will be fired when the alarm is triggerred
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onReceive(Context context, Intent intent) {
        // set a time and initialize an alarm with that time
        Calendar calendar = Calendar.getInstance();
        Log.d("---------------> INFO: ", "Alarm fired at: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
        context.startForegroundService(new Intent(context, StepService.class));
    }
}
