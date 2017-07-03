package com.apps.koru.star8_video_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.VideoView;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.util.ArrayList;

public class Model {
    private static final Model ourInstance = new Model();
    public static Model getInstance() {
        return ourInstance;
    }

    public ArrayList<Uri> uriPlayList = new ArrayList<>();
    public FirebaseDatabase database ;
    public String event;
    public VideoView videoView;
    public ArrayList<String> list = new ArrayList<>();
    public int onTrack =-1;
    public static int dcount = 0;
    public int p = 0;public int pstep = 1;public  boolean flag = false;
    public ArrayList<String> playlistFileNames = new ArrayList<>();
    public ArrayList<String> videoListphats = new ArrayList<>();
    public FirebaseStorage storage;
    public StorageReference storageRef;
    public Context context;
    public File videoDir ;
    public boolean downloadFinishd = true;
    public DataSnapshot listSnapshot;
    public FirebaseAnalytics mFirebaseAnalytics;
    public SharedPreferences sharedPreferences;
    public PlayList mainPlayListTemp ;
    public VideoView mainVideoView;
    public PlayList mainPlayList ;
    public FirebaseJobDispatcher dispatcher;
    public boolean pause = false;
    public String plyListRoot = "Playlists";
    public String playListKey ="-Kl8dzXX4NqC1b8mYUoG";
    public String playListName = "videos";

    /** prod **/
    //public String storgeUrl = "gs://star8videoapp.appspot.com/ph/videos";

    /** dev **/
    public String storgeUrl = "gs://star8videoapp.appspot.com";


    public DatabaseReference playlistNode  ;
    public ArrayList <String>dbList = new ArrayList<>();
    public boolean playing;
    public int videoStopPosition;


    private Model() {
    }

    public void initModel(Context context){
        videoDir = new File(context.getExternalCacheDir().getAbsolutePath() + "/playlist1");
        videoDir.mkdir();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        /** prod **/
        //playlistNode = database.getReference().child(plyListRoot).child(playListKey).child(playListName);
        /** dev **/
        playlistNode = database.getReference().child("testPlaylist");

        mainPlayList = new PlayList(context);
        mainPlayListTemp = new PlayList(context);
        mainVideoView = new VideoView(context);
    }
}
