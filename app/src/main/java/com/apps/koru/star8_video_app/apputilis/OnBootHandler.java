package com.apps.koru.star8_video_app.apputilis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apps.koru.star8_video_app.MainActivity;

public class OnBootHandler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
