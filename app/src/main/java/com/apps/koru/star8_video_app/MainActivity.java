package com.apps.koru.star8_video_app;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;
import android.widget.MediaController;


import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    MediaController mediaController;
    File videosRoot;
    public static VideoView mainVideoView;
    public static PlayList mainPlayList;

    public static FirebaseDatabase  database = FirebaseDatabase.getInstance();
    private boolean pause = false;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("function","onResume");
/*
        mainPlayList.downloadPlaylist("testPlaylist");
*/
        if (pause) {
            mainVideoView.seekTo(videoStopPosition);
            mainVideoView.start();
            Log.d("function","video resumed");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("function","onStart");
        if (!pause) {
            mainPlayList.downloadPlaylist("testPlaylist");
            Log.d("function","video started");
        }
    }
}
