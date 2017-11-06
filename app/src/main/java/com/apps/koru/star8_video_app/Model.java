package com.apps.koru.star8_video_app;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import com.apps.koru.star8_video_app.apputils.InstallationHandler;
import com.apps.koru.star8_video_app.objects.AdvertisingObj;
import com.apps.koru.star8_video_app.objects.CarHandler;
import com.apps.koru.star8_video_app.objects.PlayList;
import com.apps.koru.star8_video_app.objects.RoomDb.LocalDbManger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * this class job is to hold the state of the app
 * its a singleton so only one instance for all the app
 */
public class Model {
    private static final Model ourInstance = new Model();
    public static Model getInstance() {

        return ourInstance;
    }
    public FirebaseAnalytics mFirebaseAnalytics;
    public ArrayList<Uri> uriPlayList = new ArrayList<>();
    private FirebaseDatabase database ;
    public DatabaseReference databaseReference;
    public ArrayList<String> playlistFileNames = new ArrayList<>();
    public ArrayList<String> videoListphats = new ArrayList<>();
    public ArrayList<AdvertisingObj> advertisingObjs = new ArrayList<>();
    public FirebaseStorage storage;
    public StorageReference storageRef;
    public File videoDir ;
    public boolean downloadFinishd = true;
    public DataSnapshot listSnapshot;
    public PlayList mainPlayListTemp ;
    public PlayList mainPlayList ;
    public boolean pause = false;
    public boolean playingVideosStarted = false;
    public boolean needToRefrash = false;
    public String osId = "";
    public DatabaseReference imeiNode ;
    public DatabaseReference carNode ;
    public String carId = "";
    public String tvCode = "";
    public boolean carData = false;
    public CarHandler carHandler ;
    public StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
    public String nowPlayingName = "";
    public Boolean isOldInstace = false;
    public LocalDbManger localDbManger = new LocalDbManger();


    public String plyListRoot = "Playlists";
    public String playListName = "videos";
    public String playListKey = "";
    public FirebaseAuth mAuth;

    public InstallationHandler installationHandler;

    public String storgeUrl = "gs://star8videoapp.appspot.com/ph/videos";

    /**--------environment---------**/

    //final private  String environment = "DEV" ;
    //final private  String environment = "PROD" ;
    final private  String environment = "QA" ;

    /**-------------------------**/

    public ArrayList<ArrayList<String>> playlists = new ArrayList<>() ;
    public DatabaseReference playlistNode  ;
    public ArrayList <String>dbList = new ArrayList<>();
    public boolean playing;
    public int videoStopPosition;




    private Model() {
    }

    public void initModel(Context context){
        carHandler = new CarHandler();
        videoDir = new File(context.getExternalCacheDir().getAbsolutePath() + "/playlist");
        if (!videoDir.exists()){
            videoDir.mkdir();
        }


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        storage = FirebaseStorage.getInstance();
        storage.setMaxOperationRetryTimeMillis(30000);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        isOldInstace = false;
        //dev - mode :
        if (Objects.equals(environment, "DEV")){
            databaseReference = database.getReference().child("DEV");
        }
        else if (Objects.equals(environment,"PROD")){
            databaseReference = database.getReference().child("PROD");
        }
        else {
            databaseReference = database.getReference();
        }
        imeiNode = databaseReference.child("TVCode");
        carNode = databaseReference.child("Cars");




        //conectToPlayList("-Kl8dzXX4NqC1b8mYUoG");
    }
    public void conectToPlayList (String pListKey){
        if (pListKey != null){
            playListKey = pListKey;
            playlistNode = databaseReference.child(plyListRoot).child(playListKey).child(playListName);
            //this is for testing only remove me
//            playlistNode = databaseReference.child("test/Playlists/playListCodeExample1/videos");
            mainPlayList = new PlayList();
            mainPlayListTemp = new PlayList();
        }
    }


    public String getEnvironment() {
        return environment;
    }
}
