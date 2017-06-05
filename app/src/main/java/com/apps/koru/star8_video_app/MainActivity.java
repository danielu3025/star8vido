package com.apps.koru.star8_video_app;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.VideoView;
import android.widget.MediaController;


import com.crashlytics.android.Crashlytics;
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
    public static ImageView downloadingImg;

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
        downloadingImg = (ImageView)findViewById(R.id.downloadImg);
        downloadingImg.setVisibility(View.INVISIBLE);

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