package com.apps.koru.star8_video_app;


import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.apps.koru.star8_video_app.apputils.InstallationHandler;
import com.apps.koru.star8_video_app.downloadclass.DeleteFilesHandler;
import com.apps.koru.star8_video_app.downloadclass.FireBaseDbListener;
import com.apps.koru.star8_video_app.downloadclass.FireBaseVideoDownloader;
import com.apps.koru.star8_video_app.downloadclass.FireBaseVideoDownloader2;
import com.apps.koru.star8_video_app.downloadclass.MissFileFinder;
import com.apps.koru.star8_video_app.downloadclass.MissFileFinder2;
import com.apps.koru.star8_video_app.events.AccessEvent;
import com.apps.koru.star8_video_app.events.BQSucseesEvent;
import com.apps.koru.star8_video_app.events.DeleteVideosEvent;
import com.apps.koru.star8_video_app.events.DownloadErrorEvent;
import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.events.VideoViewEvent;
import com.apps.koru.star8_video_app.events.downloadsEvents.DownloadComplateReportEvent;
import com.apps.koru.star8_video_app.events.downloadsEvents.ReSendtoBqEvent;
import com.apps.koru.star8_video_app.events.testEvents.BqErrorEvent;
import com.apps.koru.star8_video_app.events.testEvents.TestRoomDbEvent;
import com.apps.koru.star8_video_app.events.testEvents.TestplayListEvent;
import com.apps.koru.star8_video_app.objects.BQ.BQResend2;
import com.apps.koru.star8_video_app.objects.BQ.BigQueryDownloadReport;
import com.apps.koru.star8_video_app.objects.BQ.BigQueryPlayedReport;
import com.apps.koru.star8_video_app.objects.BQ.BigQueryReportMangar;
import com.apps.koru.star8_video_app.objects.FireBaseOfflineHendler;
import com.apps.koru.star8_video_app.objects.FirebaseSelector;
import com.apps.koru.star8_video_app.objects.PlayList;
import com.apps.koru.star8_video_app.objects.RoomDb.carInfo.CarInfoDataBase;
import com.apps.koru.star8_video_app.objects.RoomDb.reports.ReportRecord;
import com.apps.koru.star8_video_app.objects.RoomDb.reports.ReportsRecDatabase;
import com.apps.koru.star8_video_app.objects.TimeHendler;
import com.apps.koru.star8_video_app.objects.VideoPlayer;
import com.apps.koru.star8_video_app.sharedutils.AsyncHandler;
import com.apps.koru.star8_video_app.sharedutils.UiHandler;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import io.fabric.sdk.android.Fabric;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

//import com.apps.koru.star8_video_app.events.SaveThePlayListEvent;

public class MainActivity extends AppCompatActivity {
    Model appModel = Model.getInstance();
    Button info;
    Button downloadStatus;
    TextView btlist;
    Button btReports;
    Button btVersion;
    TextView btFolder;
    Button btNext;
    Button btBack;
    Button btRoomStat;
    Button btRoomError;
    Boolean doReports = true;
    int onTrack = 0;
    private SharedPreferences sharedPreferences;
    VideoView videoView;
    boolean buttons = false;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");
    Date now = new Date();
    String txt = "";
    String[] textArr = new String[4];
    Date now2 = new Date();
    String txt2 = "";
    String[] textArr2 = new String[4];
    AssetManager am;
    private InstallationHandler installationHandler;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private FireBaseOfflineHendler fireBaseOfflineHendler;

    /**====================lifeCycle methods======================**/
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("funtion called:", "onCreate");
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view AFTER ABOVE sequence (to avoid crash)
        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            Log.d("boot", "im here!!");
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
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setContentView(R.layout.activity_main2);

        appModel.initModel(this.getApplicationContext());

        am = MainActivity.this.getAssets();
        appModel.assetManager = am;

        appModel.mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = appModel.mAuth.getCurrentUser();

        //OfflinePlayList offlinePlayList = new OfflinePlayList();
        //PlayOffline playOffline = new PlayOffline();
        DeleteFilesHandler deleteFilesHandler = new DeleteFilesHandler();
        PlayList playList = new PlayList();


        appModel.bigQueryReportMangar = new BigQueryReportMangar();
        appModel.localDbManger.reportsRecDatabase = Room.databaseBuilder(this.getApplicationContext(), ReportsRecDatabase.class, "reports.db").build();
        appModel.localDbManger.carInfoDataBase = Room.databaseBuilder(this.getApplicationContext(), CarInfoDataBase.class, "carsInfo.db").build();


        FirebaseSelector firebaseSelector = new FirebaseSelector();

        btlist = (TextView) findViewById(R.id.btPlatlist);
        btFolder = (TextView) findViewById(R.id.btFolder);
        btReports = (Button) findViewById(R.id.btReports);
        btVersion = (Button) findViewById(R.id.btversion);
        btReports.setBackgroundColor(Color.GREEN);

        btNext = (Button) findViewById(R.id.btNext);
        btBack = (Button) findViewById(R.id.btBack);
        btRoomStat = (Button) findViewById(R.id.btRoomStat);
        btRoomError = (Button) findViewById(R.id.btRoomErrorInfo);
        btRoomError.setText("");

        info = (Button) findViewById(R.id.infoBt);
        info.setTransformationMethod(null);
        downloadStatus = (Button) findViewById(R.id.btDownloadStatus);


        downloadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                fireBaseOfflineHendler.offlinePlaylis();

                System.out.println(FirebaseAuth.getInstance().getCurrentUser().getEmail());


                if (buttons) {
                    btFolder.setVisibility(View.INVISIBLE);
                    btlist.setVisibility(View.INVISIBLE);
                    info.setVisibility(View.INVISIBLE);
                    downloadStatus.setBackgroundColor(Color.TRANSPARENT);
                    btVersion.setVisibility(View.INVISIBLE);
                    btReports.setVisibility(View.INVISIBLE);
                    btNext.setVisibility(View.INVISIBLE);
                    btBack.setVisibility(View.INVISIBLE);
                    btRoomStat.setVisibility(View.INVISIBLE);
                    btRoomError.setVisibility(View.INVISIBLE);
                    buttons = false;
                } else {
                    btFolder.setVisibility(View.VISIBLE);
                    btlist.setVisibility(View.VISIBLE);
                    info.setVisibility(View.VISIBLE);
                    btVersion.setVisibility(View.VISIBLE);
                    btReports.setVisibility(View.VISIBLE);
                    btBack.setVisibility(View.VISIBLE);
                    btNext.setVisibility(View.VISIBLE);
                    btRoomStat.setVisibility(View.VISIBLE);
                    btRoomError.setVisibility(View.VISIBLE);
                    downloadStatus.setBackgroundColor(Color.GREEN);
                    buttons = true;
                }
            }
        });
        btRoomStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appModel.localDbManger.toreport.size() > 0) {
                    btRoomStat.setBackgroundColor(Color.rgb(65, 134, 159));
                    appModel.rcs.clear();
                    for (ReportRecord rc : appModel.localDbManger.toreport) {
                        appModel.rcs.add(rc);
                    }
                    for (ReportRecord rc : appModel.rcs) {
                        try {
                            new BQResend2().executeOnExecutor(THREAD_POOL_EXECUTOR, rc.toJson(), "0");
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }
            }
        });
        btReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (doReports) {
                    doReports = false;
                    btReports.setBackgroundColor(Color.RED);
                } else {
                    doReports = true;
                    btReports.setBackgroundColor(Color.GREEN);
                }
            }
        });
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTrack == appModel.uriPlayList.size() - 1) {
                    onTrack = 0;
                } else {
                    onTrack++;
                }
                EventBus.getDefault().post(new TestplayListEvent());
                try {
                    videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
                    videoView.start();
                } catch (Exception e) {
                    onTrack = 0;
                    videoView.start();
                    EventBus.getDefault().post(new TestplayListEvent());
                }
                logEvets(String.valueOf(appModel.nowPlayingName), "skip", 0);
            }
        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTrack == 0) {
                    onTrack = appModel.uriPlayList.size() - 1;
                } else {
                    onTrack--;
                }
                try {
                    videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
                    videoView.start();
                    EventBus.getDefault().post(new TestplayListEvent());
                } catch (Exception e) {
                    onTrack = appModel.uriPlayList.size() - 1;
                    videoView.start();
                    EventBus.getDefault().post(new TestplayListEvent());
                }
                logEvets(String.valueOf(appModel.nowPlayingName), "skip", 0);

            }
        });
        btVersion.setBackgroundColor(Color.rgb(83, 187, 240));
        btVersion.setText(appModel.getEnvironment());

        if (!appModel.pause) {
            if (appModel.uriPlayList.size() == 0) {
                appModel.dbList.clear();
            }
            videoView = (VideoView) findViewById(R.id.videoView2);
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.adx);
            videoView.start();
            //EventBus.getDefault().post(new GetOfflinePlayListEvent("delete", this.getApplicationContext()));
        }


        installationHandler = appModel.installationHandler;

        if (installationHandler == null) {
            installationHandler = new InstallationHandler(this);
        }
        try {
            appModel.tvCode = InstallationHandler.readInstallationFile(installationHandler.getInstallation());
            System.out.println("tv code: " + appModel.tvCode);
            appModel.carHandler.setCar();
            appModel.carHandler.setCarFromRoom();


        } catch (IOException e) {
            e.printStackTrace();
        }
        appModel.localDbManger.getRecordsinReportRecs();

//        btFolder.setVisibility(View.INVISIBLE);
//        btlist.setVisibility(View.INVISIBLE);
//        info.setVisibility(View.INVISIBLE);
//        downloadStatus.setBackgroundColor(Color.TRANSPARENT);
//        btVersion.setVisibility(View.INVISIBLE);
//        btReports.setVisibility(View.INVISIBLE);
//        btNext.setVisibility(View.INVISIBLE);
//        btBack.setVisibility(View.INVISIBLE);
//        btRoomStat.setVisibility(View.INVISIBLE);
//        btRoomError.setVisibility(View.INVISIBLE);
//
//        buttons = false;
        btlist.setMovementMethod(new ScrollingMovementMethod());
        btFolder.setMovementMethod(new ScrollingMovementMethod());

        setLocationManager();


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

    /**====================Activity utils======================**/

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

        params.putInt("status",status);

        appModel.mFirebaseAnalytics.logEvent("played_event_alpha1", params);


        //BIGQUERY
        if (doReports){
            saveToBQ(0,getFileName(itemName),appModel.carHandler.getCarId(),appModel.carHandler.getTvCode(),
                    appModel.carHandler.getCountry(),appModel.carHandler.getRegion(),
                    appModel.carHandler.getRoute(),appModel.carHandler.getType(),
                    appModel.carHandler.getCctv(),appModel.carHandler.getTag(),textArr[0],textArr[1],comment,status);
        }

        //fabric
        Answers.getInstance().logCustom(new CustomEvent("played_event_alpha1").putCustomAttribute(appModel.carId,getFileName(itemName)));
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String getFileName(String path){
        String txt =  path;
        String lastWord = txt.substring(txt.lastIndexOf("/")+1);
        return  lastWord;
    }

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

    /**====================Events======================**/
    @Subscribe
    public void onEvent(AccessEvent event) {
        if (event.getMessage().equals("ok") && appModel.carData) {
            VideoPlayer player = new VideoPlayer();
            FireBaseVideoDownloader2 fireBaseVideoDownloader2 = new FireBaseVideoDownloader2();
            FireBaseVideoDownloader fireBaseVideoDownloader = new FireBaseVideoDownloader();
            MissFileFinder2 missFileFinder2 = new MissFileFinder2();
            MissFileFinder missFileFinder = new MissFileFinder();
            FireBaseDbListener fireBaseDbListener = new FireBaseDbListener();
            //DbListenr2 dbListenr2 = new DbListenr2();
            System.out.println(appModel.carHandler.getMotorNumber());

            // testing here///

            try {
                appModel.databaseReference.child("Presence").child(appModel.carId).setValue("onLine");
                appModel.databaseReference.child("Presence").child(appModel.carId).onDisconnect().setValue("offLine");
                fireBaseOfflineHendler = new FireBaseOfflineHendler();
            }
            catch (Exception e){
                e.getMessage();
            }



            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        System.out.println("connected");
                        appModel.databaseReference.child("Presence").child(appModel.carId).setValue("onLine");
                    } else {
                        System.out.println("not connected");
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println("Listener was cancelled");
                }
            });


        }
         else if (event.getMessage().equals("setRealTimeListener")) {
             if (!appModel.isOldInstace){
                 appModel.isOldInstace = true;
                 //FireBaseDbListener fireBaseDbListener = new FireBaseDbListener();
             }
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
                //info.setVisibility(View.VISIBLE);
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
            onTrack = insertInTime();
            videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
            appModel.nowPlayingName = new File(String.valueOf(appModel.uriPlayList.get(onTrack))).getName();
            System.out.println("!!!!!!! " + appModel.osId);
            System.out.println("Playing:>> " + onTrack + ": " + appModel.uriPlayList.get(onTrack));

            videoView.start();
            //EventBus.getDefault().post(new SaveThePlayListEvent("save"));
            //EventBus.getDefault().post(new TestplayListEvent());
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
            logEvets(String.valueOf(appModel.nowPlayingName),error,0);

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
            appModel.nowPlayingName = new File(String.valueOf(appModel.uriPlayList.get(onTrack))).getName();
            EventBus.getDefault().post(new TestplayListEvent());
            return true;
        });

        videoView.setOnCompletionListener(mp -> {

            logEvets(String.valueOf(appModel.nowPlayingName),"ok",1);


            if (appModel.needToRefrash){
                Log.d("**playing"," playlist has Updated");
                EventBus.getDefault().post(new DeleteVideosEvent(appModel.dbList,"del"));
                //EventBus.getDefault().post(new SaveThePlayListEvent("save"));
                appModel.needToRefrash = false;
            }
//            if (onTrack < appModel.uriPlayList.size()){
//                logEvets(String.valueOf(appModel.uriPlayList.get(onTrack)),"ok",1);
//            }


            if (onTrack < appModel.uriPlayList.size()-1) {
                onTrack++;
            }
            else{
                onTrack = 0;
                appModel.hour++;
                if (appModel.hour == 24){
                    appModel.hour = 0;
                }
                appModel.uriPlayList = appModel.urisPlayLists.get(appModel.hour);
            }
            videoView.setVideoURI(appModel.uriPlayList.get(onTrack));

            System.out.println("Playing:>> " + onTrack +": " + appModel.uriPlayList.get(onTrack)) ;
            EventBus.getDefault().post(new TestplayListEvent());
            videoView.start();
            appModel.nowPlayingName = new File(String.valueOf(appModel.uriPlayList.get(onTrack))).getName();
            EventBus.getDefault().post(new TestplayListEvent());
            setTestColor();

        });
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

    // 0 to masterdata  1 to downloading table
    public void saveToBQ(int table,String name,String id,String tvcode, String country ,String region, String route , String type ,String cctv,String tag , String date, String time, String comment , int value){
           final String newRow =
                       "{" +
                        "\"video_name\": " +"\"" +name +"\""+ ", " +
                        "\"vehicle_id\": " + "\""+ id + "\""+ ","+
                        "\"tv_code\": " + "\""+ tvcode + "\""+ ","+
                        "\"country\": " + "\""+ country + "\""+ ","+
                        "\"region\": " + "\""+ region + "\""+ ","+
                        "\"route\": " + "\""+ route + "\""+ ","+
                        "\"type\": " + "\""+ type + "\""+ ","+
                        "\"cctv\": " + "\""+ cctv + "\""+ ","+
                        "\"tag\": " + "\""+ tag + "\""+ ","+
                        "\"date\": " + "\""+ date + "\""+ ","+
                        "\"time\": " + "\""+ time + "\""+ ","+
                        "\"comment\": " + "\""+ comment + "\""+ ","+
                        "\"status\": "  + value +
                        "}";
        appModel.localDbManger.reportRecord =  appModel.localDbManger.jsonToRecord.getReportRecord(newRow,table);
        //reportRecord = new ReportRecord(table,name,id,tvcode,country,region,route,type,cctv,tag,date,time,comment,value);
        // BigQuery Streaming in blocks of ROW_INTERVAL records
        if (table ==0){
            try {
                new BigQueryPlayedReport().executeOnExecutor(THREAD_POOL_EXECUTOR,newRow);
            }catch (Exception e ){
                e.getMessage();
            }
        }

        else if (table == 1){

            new BigQueryDownloadReport().executeOnExecutor(THREAD_POOL_EXECUTOR,newRow);

        }
        if (appModel.localDbManger.toreport != null){
//                appModel.bigQueryReportMangar.clearRecords();

           }
    }

    @Subscribe
    public void downloadComplateEvent(DownloadComplateReportEvent event){
        if (doReports){
            now2 = new Date();
            txt2  =  df.format(now);
            textArr2 = txt2.split("-", -1);
            saveToBQ(1,getFileName(event.getItemName()),appModel.carHandler.getCarId(),appModel.carHandler.getTvCode(),
                    appModel.carHandler.getCountry(),appModel.carHandler.getRegion(),
                    appModel.carHandler.getRoute(),appModel.carHandler.getType(),
                    appModel.carHandler.getCctv(),appModel.carHandler.getTag(),textArr2[0],textArr2[1],event.getComment(),event.getStatus());
        }
    }

    @Subscribe
    public void onSendAgianReportEvent(ReSendtoBqEvent event){

    }

    @Subscribe
    public void onRoomStatUpdate(TestRoomDbEvent event){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appModel.bigQueryReportMangar.tasks.size()==0){
                    btRoomStat.setBackgroundColor(Color.rgb(254,197,112));
                }
                btRoomStat.setText(String.valueOf(appModel.localDbManger.toreport.size()));

            }
        });
    }

    @Subscribe
    public void OnBqErrorEvent(BqErrorEvent event){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btRoomError.setText(event.getMsg());
            }
        });
    }

    @Subscribe
    public void onBQsecsuuesed(BQSucseesEvent event){

        try{
             if (appModel.localDbManger.toreport.size()>0 && !appModel.bigQueryReportMangar.working){
                 btRoomStat.setBackgroundColor(Color.rgb(65,134,159));
                 appModel.rcs.clear();
                 for (ReportRecord rc : appModel.localDbManger.toreport){
                     appModel.rcs.add(rc);
                 }
                 appModel.bigQueryReportMangar.num = appModel.rcs.size();
                 if (appModel.bigQueryReportMangar.num>100){
                     appModel.bigQueryReportMangar.num = 100;
                 }
                 for (int i =0 ; i<appModel.bigQueryReportMangar.num; i++){
                     Log.d("bq-resand","starting  pool size  " + i +"/"+ appModel.bigQueryReportMangar.num);
                     new BQResend2().executeOnExecutor(THREAD_POOL_EXECUTOR,appModel.rcs.get(i).toJson(),String.valueOf(appModel.rcs.get(i).getTable()));
                 }

             }
         }catch (Exception e){
             e.getMessage();
         }
    }

    public void setTestColor(){
        if (Objects.equals(btRoomStat.getText(),"0")){
            btRoomError.setText("");
            btRoomStat.setBackgroundColor(Color.rgb(254,197,112));
        }
    }

    public int insertInTime(){
        int min =0;
        int i = 0;
        int videoNumber = 0;
        try {
            for(Uri file : appModel.uriPlayList){
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                //use one of overloaded setDataSource() functions to set your data source
                retriever.setDataSource(this, file);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time);
                timeInMillisec = timeInMillisec / 1000;
                min+=timeInMillisec;
                if (min/60 > new TimeHendler().minute){
                    System.out.println("this is the video to start with:: " + i);
                    videoNumber =i;
                }
                else {
                    i++;
                }
                retriever.release();
            }
        }catch (Exception e){
            e.getMessage();
            return  0;
        }

        return videoNumber;
    }

    /**====================Location functions======================**/

    public void onRequestPermissionsResult(int requestcode, @NonNull String[] premissons , @NonNull int[] getresults){
        super.onRequestPermissionsResult(requestcode,premissons,getresults);

        if (requestcode == 1){
            if (getresults.length>0 && getresults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    requestForLocationListenr();
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    public void requestForLocationListenr(){
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,100,locationListener);
        }catch (Exception e){
            e.getMessage();
        }
    }
    public void updateLocation(Location location){
        try {
            System.out.println("%%%Gps");
            appendNode(appModel.carId,location.getLatitude(),location.getLongitude());
        }catch (Exception e){
            e.getMessage();
        }

    }
    public void setLocationManager(){

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateLocation(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {}

                @Override
                public void onProviderEnabled(String s) {}

                @Override
                public void onProviderDisabled(String s) {}
            };

            if (Build.VERSION.SDK_INT <23){
                requestForLocationListenr();
            }
            else {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
                }
                else {
                    requestForLocationListenr();
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (lastKnownLocation != null){
                        updateLocation(lastKnownLocation);
                    }
                }
            }
        }catch (Exception e){
            e.getMessage();
        }


    }
    public void appendNode(String carid, double lat , double lon) {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Locations");
            if (carid != ""){
                if (appModel.carHandler.getMotorNumber() != null){
                    reference.child(carid+"/motor").setValue(String.valueOf(appModel.carHandler.getMotorNumber()));
                }
                reference.child(carid+"/motor").setValue(String.valueOf(appModel.carHandler.getMotorNumber()));
                reference.child(carid+"/lat").setValue(String.valueOf(lat));
                reference.child(carid+"/lon").setValue(String.valueOf(lon));
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

}