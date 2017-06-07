package com.apps.koru.star8_video_app;


import android.app.job.JobInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;


import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobTrigger;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    public static VideoView mainVideoView;
    public static PlayList mainPlayList;
    public static ImageView downloadIcon;
    public static ImageView noInternet;
    public static Button noConnectionOk;
    public static TextView noConnectionText;
    public static boolean isConnection = false;

    FirebaseJobDispatcher dispatcher;
    public static FirebaseDatabase  database = FirebaseDatabase.getInstance();
    private boolean pause = false;
    private boolean firstRun = true;
    private int videoStopPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("funtion called:","onResume");
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view AFTER ABOVE sequence (to avoid crash)
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mainPlayList = new PlayList(this);
        mainVideoView = (VideoView) findViewById(R.id.videoView);
        downloadIcon = (ImageView)findViewById(R.id.downloadImg);
        downloadIcon.setVisibility(View.INVISIBLE);
        noInternet = (ImageView)findViewById(R.id.noConnection);
        noInternet.setVisibility(View.INVISIBLE);
        noConnectionOk = (Button) findViewById(R.id.noConnectionOk);
        noConnectionOk.setVisibility(View.GONE);
        noConnectionText = (TextView) findViewById(R.id.noConnectionText);
        noConnectionText.setVisibility(View.GONE);
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
        pause = true;
        videoStopPosition = mainVideoView.getCurrentPosition();
        mainVideoView.pause();
        try {

            dispatcher.cancel("Connection_check");
        } catch (Exception e) {
            e.printStackTrace();
        }    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

            dispatcher.cancel("Connection_check");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Log.d("function", "onResume");
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
        }

        if (!pause && isNetworkAvailable()) {
            mainPlayList.downloadPlaylist("testPlaylist");
            Log.d("function", "video started");
        } else if(!pause && !isNetworkAvailable()){
            noInternet.setVisibility(View.VISIBLE);
            mainPlayList.loadThePlayList();
        } else if (pause) {
            mainVideoView.seekTo(videoStopPosition);
            mainVideoView.start();
            Log.d("function","video resumed");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void checkConnectivity(View view) {
        if(isNetworkAvailable()) {
            MainActivity.noConnectionText.setVisibility(View.GONE);
            MainActivity.noConnectionOk.setVisibility(View.GONE);
            mainPlayList.downloadPlaylist("testPlaylist");
        } else {
            mainPlayList.loadThePlayList();
        }
    }
}

