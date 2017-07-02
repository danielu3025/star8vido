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


import com.apps.koru.star8_video_app.downloadclass.FireBaseDbLisner;
import com.apps.koru.star8_video_app.downloadclass.FireBaseVideoDownloader;
import com.apps.koru.star8_video_app.downloadclass.MissFileFinder;
import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    Model appModel = Model.getInstance();

    public static VideoView videoView;
    public static Button infoBt;
    //public static ImageView downloadIcon;
    //public static ImageView noInternet;
    //public static TextView noConnectionText;
    public static boolean isConnection = false;
    public static MyObj obj = new MyObj(false);

    VideoPlayer player;

    FirebaseJobDispatcher dispatcher;
    public static FirebaseDatabase  database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("funtion called:","onCreate");


        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view AFTER ABOVE sequence (to avoid crash)
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main2);
        Model.getInstance().database = database;
        appModel.initModel(this);
        videoView = (VideoView)findViewById(R.id.videoView2);
        infoBt = (Button)findViewById(R.id.infoBt);
        //infoBt.setVisibility(View.INVISIBLE);

        PlayList playList = new PlayList(this);
        player= new VideoPlayer(this);
        FireBaseVideoDownloader fireBaseVideoDownloader = new FireBaseVideoDownloader();
        MissFileFinder missFileFinder = new MissFileFinder();
        FireBaseDbLisner fireBaseDbLisner = new FireBaseDbLisner();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d("function","onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("function","onPause");
        appModel.pause = true;
        appModel.videoStopPosition = videoView.getCurrentPosition();
        videoView.pause();
        try {

            dispatcher.cancel("Connection_check");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            player.loadThePlayList();;
        }
        if (appModel.pause) {
            videoView.seekTo(appModel.videoStopPosition);
            videoView.start();
            Log.d("function","video resumed");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

