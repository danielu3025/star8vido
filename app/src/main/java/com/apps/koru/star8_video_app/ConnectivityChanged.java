package com.apps.koru.star8_video_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;

import static com.apps.koru.star8_video_app.MainActivity.noInternet;

public class ConnectivityChanged extends BroadcastReceiver {
    static boolean isConnection = false;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean is3g = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        /*final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something

            Log.d("Network Available ", "Flag No 1");
        }*/
        if(is3g || isWifi){
            Log.d("Network", "Flag No 1");
            isConnection = true;
            noInternet.setVisibility(View.INVISIBLE);


        } else {
            Log.d("Network", "Flag No 2");
            isConnection = false;
            noInternet.setVisibility(View.VISIBLE);

        }
    }
}
