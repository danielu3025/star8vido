package com.apps.koru.star8_video_app;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;


import com.apps.koru.star8_video_app.apputils.InstallationHandler;
import com.apps.koru.star8_video_app.apputils.OfflinePlayList;
import com.apps.koru.star8_video_app.apputils.PlayOffline;
import com.apps.koru.star8_video_app.downloadclass.DeleteFilesHandler;
import com.apps.koru.star8_video_app.downloadclass.FirebaseSelector;
import com.apps.koru.star8_video_app.downloadclass.FireBaseDbListener;
import com.apps.koru.star8_video_app.downloadclass.FireBaseVideoDownloader;
import com.apps.koru.star8_video_app.downloadclass.MissFileFinder;
import com.apps.koru.star8_video_app.events.AcseesEvent;
import com.apps.koru.star8_video_app.events.DeleteVideosEvent;
import com.apps.koru.star8_video_app.events.GetOfflinePlayListEvent;
import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.events.SaveThePlayListEvent;
import com.apps.koru.star8_video_app.events.VideoViewEvent;
import com.apps.koru.star8_video_app.objects.Model;
import com.apps.koru.star8_video_app.objects.PlayList;
import com.apps.koru.star8_video_app.objects.VideoPlayer;
import com.apps.koru.star8_video_app.sharedutils.AsyncHandler;
import com.apps.koru.star8_video_app.sharedutils.UiHandler;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.sql.Timestamp;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    Model appModel = Model.getInstance();
    Button info ;
    int onTrack =0;
    private SharedPreferences sharedPreferences;
    VideoView videoView ;

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private TelephonyManager mTelephonyManager;
    private InstallationHandler installationHandler;


    @RequiresApi(api = Build.VERSION_CODES.M)
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
        }
        LeakCanary.install(getApplication());
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main2);

        appModel.initModel(this);
        appModel.mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = appModel.mAuth.getCurrentUser();

        OfflinePlayList offlinePlayList = new OfflinePlayList();
        PlayOffline playOffline = new PlayOffline();
        DeleteFilesHandler deleteFilesHandler = new DeleteFilesHandler();
        PlayList playList = new PlayList();

        FirebaseSelector firebaseSelector = new FirebaseSelector();

        /*if (appModel.osId.equals("")){
            getOsId();
        }*/

        info = (Button) findViewById(R.id.infoBt);
        info.setTransformationMethod(null);

        if(!appModel.pause) {
            videoView = (VideoView) findViewById(R.id.videoView2);
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.adx);
            videoView.start();
            EventBus.getDefault().post(new GetOfflinePlayListEvent("delete", this));
        }


        installationHandler = appModel.installationHandler;

        if (installationHandler == null){
            installationHandler = new InstallationHandler(this);
        }
        try {
            appModel.tvCode = InstallationHandler.readInstallationFile(installationHandler.getInstallation());
            System.out.println( "tv code: " + appModel.tvCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        videoView.pause();
        savePlayListPosition();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!isNetworkAvailable()){
            EventBus.getDefault().post(new InfoEvent("vis"));
            EventBus.getDefault().post(new InfoEvent("Turn on internet Connection!"));
        }
        Log.d("function", "onResume");
        /*if (!appModel.pause && !isNetworkAvailable()) {
            EventBus.getDefault().post(new GetOfflinePlayListEvent("offline",this));
        }*/
        if (appModel.pause) {
            loadPlayListPosition();
        }
    }

    @Override
    public void onBackPressed() {
        // cant back  moahahaha
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Subscribe
    public void onEvent(AcseesEvent event) {
        if (event.getMessage().equals("ok")){
            VideoPlayer player= new VideoPlayer();
            FireBaseVideoDownloader fireBaseVideoDownloader = new FireBaseVideoDownloader();
            MissFileFinder missFileFinder = new MissFileFinder();
            FireBaseDbListener fireBaseDbListener = new FireBaseDbListener();
        }
        else {
            EventBus.getDefault().post(new InfoEvent("vis"));
            EventBus.getDefault().post(new InfoEvent(event.getMessage()));

        }
    }

    @Subscribe
    public void onEvent(InfoEvent event) {

        switch (event.getMessage()) {
            case "vis":
                info.setVisibility(View.VISIBLE);

                break;
            case "invis":
                info.setVisibility(View.INVISIBLE);
                break;
            default:
                info.setText(event.getMessage());
                break;
        }
    }
    @Subscribe
    public void onEvent(VideoViewEvent event) {
        if(!appModel.playing) {
            videoView.stopPlayback();
            onTrack = 0;
            videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
            System.out.println("!!!!!!! " + appModel.osId);

            System.out.println("Playing:>> " + onTrack + ": " + appModel.uriPlayList.get(onTrack));
            logEvets("video_played", String.valueOf(appModel.uriPlayList.get(onTrack)));

            videoView.start();
            EventBus.getDefault().post(new SaveThePlayListEvent("save"));
        }

        appModel.playingVideosStarted = true;

        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.d("Error", " - playing video error");
            if (onTrack >=0) {
                if (onTrack != appModel.uriPlayList.size()) {
                    if (onTrack<appModel.uriPlayList.size()){
                        onTrack ++;
                    }
                    videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
                } else {
                    videoView.setVideoURI(appModel.uriPlayList.get(0));
                }
            } else if (onTrack>=appModel.uriPlayList.size()){
                videoView.setVideoURI(appModel.uriPlayList.get(0));
            }
            videoView.start();
            return true;
        });

        videoView.setOnCompletionListener(mp -> {
            if (appModel.needToRefrash){
                Log.d("**playing"," playlist has Updated");
                EventBus.getDefault().post(new DeleteVideosEvent(appModel.dbList,"del"));
                EventBus.getDefault().post(new SaveThePlayListEvent("save"));
                appModel.needToRefrash = false;
            }

            if (onTrack < appModel.uriPlayList.size()-1) {
                onTrack++;
            }
            else{
                onTrack = 0;
            }
            videoView.setVideoURI(appModel.uriPlayList.get(onTrack));

            System.out.println("Playing:>> " + onTrack +": " + appModel.uriPlayList.get(onTrack)) ;

            logEvets("video_played",String.valueOf(appModel.uriPlayList.get(onTrack)));

            videoView.start();
        });
    }

    @Subscribe
    public void onEvent(SaveThePlayListEvent event) {
        AsyncHandler.post(() -> {
            sharedPreferences = this.getSharedPreferences("play_list", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("size", appModel.uriPlayList.size());

            for(int i=0;i<appModel.uriPlayList.size();i++)
            {
                editor.putString("db_" + i, String.valueOf(appModel.dbList.get(i)));
                editor.putString("video_" + i, String.valueOf(appModel.uriPlayList.get(i)));
            }

            editor.apply();
            Log.d("**saving"," playlist saved");
        });
    }

    private void logEvets(String eventName, String itemName){
        //firebase
        Bundle params = new Bundle();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        params.putString(FirebaseAnalytics.Param.ITEM_NAME,getFileName(itemName));
        appModel.mFirebaseAnalytics.logEvent(eventName, params);
        //fabric
        Answers.getInstance().logCustom(new CustomEvent("mainPlayList").putCustomAttribute("played",getFileName(itemName)));
    }

    private String getFileName(String path){
        String txt =  path;
        String lastWord = txt.substring(txt.lastIndexOf("/")+1);
        return  lastWord;
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getOsId();
        }
    }

    private void getOsId() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        appModel.osId = (Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID));
    }*/
    private void savePlayListPosition(){
        AsyncHandler.post(() -> {
            sharedPreferences = this.getSharedPreferences("play_list_position", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("onTrack", onTrack);
            editor.putInt("position",videoView.getCurrentPosition());
            editor.putBoolean("pause",true);
            editor.apply();
            runOnUiThread(()-> {
                appModel.pause=true;
                Log.d("**saving"," playlist position saved");

            });
        });
    }

    private void loadPlayListPosition(){
        AsyncHandler.post(() -> {
            sharedPreferences = this.getSharedPreferences("play_list_position", Context.MODE_PRIVATE);
            UiHandler.post(() -> {
                onTrack = sharedPreferences.getInt("onTrack", 0);
                appModel.videoStopPosition = sharedPreferences.getInt("position", 0);
                appModel.pause = sharedPreferences.getBoolean("pause", false);
                Log.e("**loading", " is finished");


                if(appModel.uriPlayList.size()>0 && appModel.pause)  {
                    videoView = (VideoView) findViewById(R.id.videoView2);
                    videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
                    videoView.seekTo(appModel.videoStopPosition);
                    videoView.start();
                    appModel.playing = true;
                    EventBus.getDefault().post(new VideoViewEvent());
                } else {
                    System.out.println("something went wrong");
                }
                Log.d("function", "video resumed");

            });
        });
    }
}