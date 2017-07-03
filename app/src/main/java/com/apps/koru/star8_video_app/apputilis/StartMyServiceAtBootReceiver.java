package com.apps.koru.star8_video_app.apputilis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apps.koru.star8_video_app.MainActivity;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, MainActivity.class);
            context.startService(serviceIntent);
        }
    }
}
