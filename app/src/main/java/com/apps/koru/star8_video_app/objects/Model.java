package com.apps.koru.star8_video_app.objects;

import android.content.Context;
import android.net.Uri;
import android.widget.VideoView;

import com.apps.koru.star8_video_app.apputils.InstallationHandler;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.util.ArrayList;

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
    public ArrayList<String> playlistFileNames = new ArrayList<>();
    public ArrayList<String> videoListphats = new ArrayList<>();
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


    public String plyListRoot = "Playlists";
    public String playListName = "videos";
    public String playListKey = "";
    public FirebaseAuth mAuth;

    public InstallationHandler installationHandler;

    /** prod **/
    public String storgeUrl = "gs://star8videoapp.appspot.com/ph/videos";

    /** dev **/
//    public String storgeUrl = "gs://star8videoapp.appspot.com";



    public DatabaseReference playlistNode  ;
    public ArrayList <String>dbList = new ArrayList<>();
    public boolean playing;
    public int videoStopPosition;


    private Model() {
    }

    public void initModel(Context context){
        videoDir = new File(context.getExternalCacheDir().getAbsolutePath() + "/playlist");
        videoDir.mkdir();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storage.setMaxOperationRetryTimeMillis(30000);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        imeiNode = database.getReference().child("Imei");
        carNode = database.getReference().child("Cars");


        //conectToPlayList("-Kl8dzXX4NqC1b8mYUoG");
    }
    public void conectToPlayList (String pListKey){
        /** prod **/
        playListKey = pListKey;
        /** dev **/
//      playListKey = "test";
        playlistNode = database.getReference().child(plyListRoot).child(playListKey).child(playListName);
        mainPlayList = new PlayList();
        mainPlayListTemp = new PlayList();
    }
}
