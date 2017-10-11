package com.apps.koru.star8_video_app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;

public class AppRunService extends Service {
    public AppRunService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("@@@service is runing");
        return super.onStartCommand(intent, flags, startId);
    }
}
