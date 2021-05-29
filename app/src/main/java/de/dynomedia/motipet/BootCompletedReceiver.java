package de.dynomedia.motipet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

    /**
     * Sets SharedPrefs to 0 (equal to step counter value after reboot)
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            StepcounterActivity.updateMyPrefs(context, 0);
        }
    }
}
