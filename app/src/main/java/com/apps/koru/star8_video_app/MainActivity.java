package com.apps.koru.star8_video_app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
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
import com.apps.koru.star8_video_app.events.AccessEvent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

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

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            Log.d("boot","im here!!");
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Log.d("shit happens", "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }

        super.onCreate(savedInstanceState);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
        }
        LeakCanary.install(getApplication());
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main2);

        appModel.initModel(this.getApplicationContext());
        appModel.mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = appModel.mAuth.getCurrentUser();

        OfflinePlayList offlinePlayList = new OfflinePlayList();
        PlayOffline playOffline = new PlayOffline();
        DeleteFilesHandler deleteFilesHandler = new DeleteFilesHandler();
        PlayList playList = new PlayList();

        FirebaseSelector firebaseSelector = new FirebaseSelector();



        info = (Button) findViewById(R.id.infoBt);
        info.setTransformationMethod(null);

        if(!appModel.pause) {
            if(appModel.uriPlayList.size() == 0) {
                appModel.dbList.clear();
            }
            videoView = (VideoView) findViewById(R.id.videoView2);
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.adx);
            videoView.start();
            EventBus.getDefault().post(new GetOfflinePlayListEvent("delete", this.getApplicationContext()));
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
    public void onEvent(AccessEvent event) {
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
                System.out.println("visable");
                break;
            case "invis":
                info.setVisibility(View.INVISIBLE);
                System.out.println("usvis");
                break;
            case "Download Error":
                logErorEvent("error_event_test",event.getMessage());
                break;
            default:
                info.setText(event.getMessage());
                System.out.println(event.getMessage());
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
            logEvets("played_event_test", String.valueOf(appModel.uriPlayList.get(onTrack)));

            videoView.start();
            EventBus.getDefault().post(new SaveThePlayListEvent("save"));
        }

        appModel.playingVideosStarted = true;



        List<FileDownloadTask> tasks = appModel.storageRef.getActiveDownloadTasks();




        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.d("Error", " - playing video error");
            logErorEvent("error_event_test","Errorplaying- "+ appModel.uriPlayList.get(onTrack).toString());
            if (onTrack >=0) {
                if (onTrack != appModel.uriPlayList.size()-1) {
                    if (onTrack<appModel.uriPlayList.size()-1){
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

            logEvets("played_event_test",String.valueOf(appModel.uriPlayList.get(onTrack)));

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
        params.putString("car_id",appModel.carId);
        params.putString("tv_code",appModel.tvCode);
        params.putString("video_name",getFileName(itemName));
        appModel.mFirebaseAnalytics.logEvent(eventName, params);
        //fabric
        Answers.getInstance().logCustom(new CustomEvent("played_event_test").putCustomAttribute(appModel.carId,getFileName(itemName)));
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
                    System.out.println("uriPlaylist Size: " + appModel.uriPlayList.size());
                    videoView.pause();
                    videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
                    videoView.seekTo(appModel.videoStopPosition);
                    videoView.start();
                    appModel.playing = true;
                    EventBus.getDefault().post(new VideoViewEvent());
                } else if (appModel.pause && appModel.uriPlayList.size() == 0) {
                    videoView = (VideoView) findViewById(R.id.videoView2);
                    videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.adx);
                    videoView.start();
                }
                Log.d("function", "video resumed");

            });
        });
    }
    private void logErorEvent(String eventName, String itemName){
        //firebase
        Bundle params = new Bundle();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        params.putString("ErrorType",itemName);
        appModel.mFirebaseAnalytics.logEvent(eventName, params);
        //fabric
        Answers.getInstance().logCustom(new CustomEvent("error_event_test").putCustomAttribute("error in",itemName));
    }
}