package com.apps.koru.star8_video_app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.apps.koru.star8_video_app.downloadclass.FireBaseDbListener;
import com.apps.koru.star8_video_app.downloadclass.FireBaseVideoDownloader;
import com.apps.koru.star8_video_app.downloadclass.FireBaseVideoDownloader2;
import com.apps.koru.star8_video_app.downloadclass.MissFileFinder;
import com.apps.koru.star8_video_app.events.AccessEvent;
import com.apps.koru.star8_video_app.events.DeleteVideosEvent;
import com.apps.koru.star8_video_app.events.DownloadErrorEvent;
import com.apps.koru.star8_video_app.events.GetOfflinePlayListEvent;
import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.events.SaveThePlayListEvent;
import com.apps.koru.star8_video_app.events.VideoViewEvent;
import com.apps.koru.star8_video_app.events.testEvents.TestplayListEvent;
import com.apps.koru.star8_video_app.objects.FirebaseSelector;
import com.apps.koru.star8_video_app.objects.Model;
import com.apps.koru.star8_video_app.objects.PlayList;
import com.apps.koru.star8_video_app.objects.ReportsHandler;
import com.apps.koru.star8_video_app.objects.VideoPlayer;
import com.apps.koru.star8_video_app.sharedutils.AsyncHandler;
import com.apps.koru.star8_video_app.sharedutils.UiHandler;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    Model appModel = Model.getInstance();
    Button info ;
    Button downloadStatus;
    Button btlist;
    Button btFolder;
    int onTrack =0;
    private SharedPreferences sharedPreferences;
    VideoView videoView ;
    boolean buttons = true;
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
    Date now = new Date();
    String txt = "";
    String[] textArr = new String[2];

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

        btlist = (Button) findViewById(R.id.btPlatlist);
        btFolder = (Button) findViewById(R.id.btFolder);

        info = (Button) findViewById(R.id.infoBt);
        info.setTransformationMethod(null);
        downloadStatus= (Button) findViewById(R.id.btDownloadStatus);
        downloadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttons) {
                    btFolder.setVisibility(View.INVISIBLE);
                    btlist.setVisibility(View.INVISIBLE);
                    info.setVisibility(View.INVISIBLE);
                    downloadStatus.setBackgroundColor(Color.TRANSPARENT);
                    buttons = false;
                } else {
                    btFolder.setVisibility(View.VISIBLE);
                    btlist.setVisibility(View.VISIBLE);
                    info.setVisibility(View.VISIBLE);
                    downloadStatus.setBackgroundColor(Color.GREEN);
                    buttons = true;
                }
            }
        });


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
            appModel.carHandler.setCar();

        } catch (IOException e) {
            e.printStackTrace();
        }
        info.setVisibility(View.VISIBLE);

        ReportsHandler reportsHandler= new ReportsHandler();


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
        try{
            videoView.pause();
            savePlayListPosition();
        }catch (Exception e){}
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
        if (event.getMessage().equals("ok") && appModel.carData){
            VideoPlayer player= new VideoPlayer();
            FireBaseVideoDownloader fireBaseVideoDownloader = new FireBaseVideoDownloader();
            FireBaseVideoDownloader2 fireBaseVideoDownloader2 = new FireBaseVideoDownloader2();
            MissFileFinder missFileFinder = new MissFileFinder();
            FireBaseDbListener fireBaseDbListener = new FireBaseDbListener();
            System.out.println(appModel.carHandler.getMotorNumber());
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
                //info.setVisibility(View.INVISIBLE);
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

            videoView.start();
            EventBus.getDefault().post(new SaveThePlayListEvent("save"));
            EventBus.getDefault().post(new TestplayListEvent());
        }

        appModel.playingVideosStarted = true;

        //List<FileDownloadTask> tasks = appModel.storageRef.getActiveDownloadTasks();

        videoView.setOnErrorListener((mp, what, extra) -> {
            String error = "";
            switch (extra){
                case  -1004 :
                    error = "MEDIA_ERROR_IO";
                    break;
                case -1007 :
                    error = "MEDIA_ERROR_MALFORMED";
                    break;
                case -1010:
                    error = "MEDIA_ERROR_UNSUPPORTED";
                    break;
                case -110 :
                    error = "MEDIA_ERROR_TIMED_OUT";
                    break;
                default:
                    error = "MEDIA_ERROR_UNKNOWN";
            }

            Log.d("Error", " - playing video error");
            File tempFile = new File(appModel.uriPlayList.get(onTrack).toString());

            logEvets(String.valueOf(appModel.uriPlayList.get(onTrack)),error,-1);

            if (onTrack >=0) {
                if (onTrack != appModel.uriPlayList.size()-1) {
                    if (onTrack<appModel.uriPlayList.size()-1){
                        onTrack ++;
                    }
                    videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
                } else {
                    try{
                        videoView.setVideoURI(appModel.uriPlayList.get(0));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            } else if (onTrack>=appModel.uriPlayList.size()){
                videoView.setVideoURI(appModel.uriPlayList.get(0));
            }
            videoView.start();
            EventBus.getDefault().post(new TestplayListEvent());
            return true;
        });

        videoView.setOnCompletionListener(mp -> {


            if (appModel.needToRefrash){
                Log.d("**playing"," playlist has Updated");
                EventBus.getDefault().post(new DeleteVideosEvent(appModel.dbList,"del"));
                EventBus.getDefault().post(new SaveThePlayListEvent("save"));
                appModel.needToRefrash = false;
            }
            if (onTrack < appModel.uriPlayList.size()){
                logEvets(String.valueOf(appModel.uriPlayList.get(onTrack)),"ok",1);
            }


            if (onTrack < appModel.uriPlayList.size()-1) {
                onTrack++;
            }
            else{
                onTrack = 0;
            }
            videoView.setVideoURI(appModel.uriPlayList.get(onTrack));

            System.out.println("Playing:>> " + onTrack +": " + appModel.uriPlayList.get(onTrack)) ;
            logEvets(String.valueOf(appModel.uriPlayList.get(onTrack)),"ok",1);

            EventBus.getDefault().post(new TestplayListEvent());
            videoView.start();
            EventBus.getDefault().post(new TestplayListEvent());
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
                if (appModel.dbList.size()>0){
                    editor.putString("db_" + i, String.valueOf(appModel.dbList.get(i)));
                    editor.putString("video_" + i, String.valueOf(appModel.uriPlayList.get(i)));
                }

            }

            editor.apply();
            Log.d("**saving"," playlist saved");
        });
    }

    private void logEvets(String itemName ,String comment ,int status){
        now = new Date();
        txt  =  df.format(now);
        textArr = txt.split("-", -1);
        //firebase
        Bundle params = new Bundle();
        params.putString("video_name",getFileName(itemName));
        params.putString("vehicle_id",appModel.carHandler.getCarId());
        params.putString("tv_code",appModel.carHandler.getTvCode());
        params.putString("country",appModel.carHandler.getCountry());
        params.putString("region",appModel.carHandler.getRegion());
        params.putString("route",appModel.carHandler.getRoute());
        params.putString("type",appModel.carHandler.getType());
        params.putString("cctv",appModel.carHandler.getCctv());
        params.putString("tag",appModel.carHandler.getTag());
        params.putString("date",textArr[0]);
        params.putString("time", textArr[1]);
        params.putString("comment",comment);
        params.putInt("status",status);

        appModel.mFirebaseAnalytics.logEvent("played_event_alpha1", params);
        //fabric
        Answers.getInstance().logCustom(new CustomEvent("played_event_alpha1").putCustomAttribute(appModel.carId,getFileName(itemName)));
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

                if(appModel.uriPlayList.size()>0 && appModel.pause && onTrack < appModel.uriPlayList.size())  {
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


        now = new Date();
        txt  =  df.format(now);
        textArr = txt.split("-", -1);

        //firebase

        Bundle params = new Bundle();
        params.putString("ErrorType",itemName);
        params.putString("video_name",getFileName(itemName));
        params.putString("vehicle_id",appModel.carHandler.getCarId());
        params.putString("tv_code",appModel.carHandler.getTvCode());
        params.putString("country",appModel.carHandler.getCountry());
        params.putString("region",appModel.carHandler.getRegion());
        params.putString("route",appModel.carHandler.getRoute());
        params.putString("type",appModel.carHandler.getType());
        params.putString("cctv",appModel.carHandler.getCctv());
        params.putString("tag",appModel.carHandler.getTag());
        params.putString("date",textArr[0]);
        params.putString("time", textArr[1]);

        appModel.mFirebaseAnalytics.logEvent("error_event_alpha1", params);
        //fabric
        Answers.getInstance().logCustom(new CustomEvent("error_event_alpha1").putCustomAttribute("error in",itemName));
    }
    @Subscribe
    public void DownloadErrorEventListener(DownloadErrorEvent event) {
        System.out.println("DownloadErrorEventListener");
        downloadStatus.setBackgroundColor(Color.RED);
    }
    @Subscribe
    public void testPlayList(TestplayListEvent event) {
        if (appModel.uriPlayList.size() > 0) {
            String txt = "playlist:\n";
            for (int i = 0; i < appModel.uriPlayList.size(); i++) {
                if (i == onTrack) {
                    txt = txt + "=> " + new File(appModel.uriPlayList.get(i).toString()).getName() + "\n";

                } else {
                    txt = txt + new File(appModel.uriPlayList.get(i).toString()).getName() + "\n";
                }
            }
            btlist.setText(txt);
        }
        if (appModel.videoDir.listFiles().length > 0) {
            String txt2 = "storage:\n";
            for (int i = 0; i < appModel.videoDir.listFiles().length; i++) {
                txt2 = txt2 + appModel.videoDir.listFiles()[i].getName() +"\n";
            }
            btFolder.setText(txt2);
        }
    }


}