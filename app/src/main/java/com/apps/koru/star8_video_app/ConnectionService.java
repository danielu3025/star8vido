package com.apps.koru.star8_video_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import static com.apps.koru.star8_video_app.MainActivity.noInternet;

public class ConnectionService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        if(isNetworkAvailable()){
            Log.d("Network changed ", "Flag No 1");
            MainActivity.isConnection = true;
            noInternet.setVisibility(View.INVISIBLE);

        } else {
            Log.d("Network changed ", "Flag No 2");
            MainActivity.isConnection = false;
            noInternet.setVisibility(View.VISIBLE);
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
