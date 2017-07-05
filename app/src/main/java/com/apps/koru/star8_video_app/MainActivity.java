package com.apps.koru.star8_video_app;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;


import com.apps.koru.star8_video_app.apputils.PlayOffline;
import com.apps.koru.star8_video_app.downloadclass.DeleteFilesHandler;
import com.apps.koru.star8_video_app.downloadclass.FireBaseDbListener;
import com.apps.koru.star8_video_app.downloadclass.FireBaseVideoDownloader;
import com.apps.koru.star8_video_app.downloadclass.MissFileFinder;
import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.events.MissVideosEvent;
import com.apps.koru.star8_video_app.objects.Model;
import com.apps.koru.star8_video_app.objects.PlayList;
import com.apps.koru.star8_video_app.objects.VideoPlayer;
import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    Model appModel = Model.getInstance();
    Button info ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("funtion called:","onCreate");
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view AFTER ABOVE sequence (to avoid crash)
        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(getApplication());
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main2);

        appModel.initModel(this);
        appModel.videoView = (VideoView)findViewById(R.id.videoView2);
        appModel.videoView.setVideoPath("android.resource://"+getPackageName()+"/"+ R.raw.adx);
        appModel.videoView.start();
        //appModel.infoBt = (Button)findViewById(R.id.infoBt);
        info = (Button)findViewById(R.id.infoBt);

        PlayList playList = new PlayList(this);
        DeleteFilesHandler deleteFilesHandler = new DeleteFilesHandler();
        VideoPlayer player= new VideoPlayer(this);
        FireBaseVideoDownloader fireBaseVideoDownloader = new FireBaseVideoDownloader();
        MissFileFinder missFileFinder = new MissFileFinder();
        FireBaseDbListener fireBaseDbListener = new FireBaseDbListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("function","onDestroy");
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("function","onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("function","onPause");
        appModel.pause = true;
        appModel.videoStopPosition = appModel.videoView.getCurrentPosition();
        appModel.videoView.pause();
        /*try {

            dispatcher.cancel("Connection_check");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("function", "onResume");
        /*try {
            dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
            Job myJob = dispatcher.newJobBuilder()
                    .setService(ConnectionService.class) // the JobService that will be called
                    .setTag("Connection_check")        // uniquely identifies the job
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(15, 30))
                    .build();


            dispatcher.mustSchedule(myJob);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*if (!appModel.pause && isNetworkAvailable()) {
            mainPlayList.downloadPlaylist("videos");
            *//*mainPlayList.downloadPlaylist("testPlaylist");*//*
            Log.d("function", "video started");
        } else if(!pause && !isNetworkAvailable()){
            noInternet.setVisibility(View.VISIBLE);
            mainPlayList.loadThePlayList();
        }*/
        if (!appModel.pause && !isNetworkAvailable()) {
            PlayOffline playOffline = new PlayOffline(this);
            playOffline.loadThePlayList();
        }
        if (appModel.pause) {
            appModel.videoView.seekTo(appModel.videoStopPosition);
            appModel.videoView.start();
            Log.d("function","video resumed");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Subscribe
    public void onEvent(InfoEvent event) {
        if (event.getMessage() == "vis"){
            info.setVisibility(View.VISIBLE);

        }
        else if (event.getMessage() == "invis"){
            info.setVisibility(View.INVISIBLE);
        }
        else {
            info.setText(event.getMessage());
        }

    }
}

