package com.apps.koru.star8_video_app;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.apps.koru.star8_video_app.sharedutils.AsyncHandler;
import com.apps.koru.star8_video_app.sharedutils.UiHandler;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;


import static com.apps.koru.star8_video_app.MainActivity.downloadIcon;
import static com.apps.koru.star8_video_app.MainActivity.mainPlayList;
import static com.apps.koru.star8_video_app.MainActivity.mainVideoView;
import static com.apps.koru.star8_video_app.MainActivity.mainPlayListTemp;
import static com.apps.koru.star8_video_app.MainActivity.mainVideoViewTemp;


public class PlayList extends AppCompatActivity {
    static ArrayList<Uri> uriPlayList = new ArrayList<>();
    String event;
    ArrayList<String> list = new ArrayList<>();
    int onTrack =-1;int dcount = 0; int p = 0; int pstep = 1; boolean flag = false;
    ArrayList<String> playlistFileNames = new ArrayList<>();
    ArrayList<String> videoListphats = new ArrayList<>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    Context context;
    File videoDir;
    boolean downloadFinishd = true;
    DatabaseReference playlistNode;
    DataSnapshot listSnapshot;
    private FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences sharedPreferences;

    public PlayList(Context contex) {
        Log.d("function","PlayList contractor calld");
        context = contex;
        // create a File object for the parent directory
        videoDir = new File(context.getExternalCacheDir().getAbsolutePath()+"/playlist1");
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(contex);

    }

    public void downloadPlaylist(String playlistName){
        Log.d("function", "downloadPlaylist calld");
        //get the playlist files name
        MainActivity.noInternet.setVisibility(View.INVISIBLE);
        /*playlistNode = MainActivity.database.getReference("Playlists").child("-Kl8dzXX4NqC1b8mYUoG").child(playlistName);*/
        playlistNode = MainActivity.database.getReference(playlistName);

        playlistNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
/*
                mainVideoView.stopPlayback();
*/
                //get playlist files names
                listSnapshot = snapshot;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    playlistFileNames.add((String) postSnapshot.getValue());
                    videoListphats.add(videoDir.getAbsolutePath() + "/" + (String) postSnapshot.getValue());
                }
                playlistFileNames = new ArrayList<>(new LinkedHashSet<>(playlistFileNames));
                videoListphats = new ArrayList<>(new LinkedHashSet<>(videoListphats));
                //check if playlist Folder is exists
                if (checkFolderExists(videoDir)) {
                    //all videos are in storage ?
                    switch (allVideosOnDevice(videoDir, playlistFileNames)) {
                        case 1: // all videos is in storage
                            mainPlayListTemp.list.removeAll(list);
                            for (int i = 0; i < playlistFileNames.size(); i++) {
                                mainPlayListTemp.list.add(videoDir.getAbsolutePath() + "/" + playlistFileNames.get(i));
                            }
                            playTheplayList();
                            break;
                        case 2:// not all videos are in the storage
                            downloadMissVideos(videoDir, videoListphats);
                            mainPlayListTemp.list.clear();
                            for (int i = 0; i < playlistFileNames.size(); i++) {
                                mainPlayListTemp.list.add(videoDir.getAbsolutePath() + "/" + playlistFileNames.get(i));
                            }
                            break;
                        default:
                    }
                    ArrayList<String> temp = new ArrayList<>(playlistFileNames);
                    playlistFileNames.clear();
                    videoListphats.clear();

                    //Collections.sort(mainPlayList.list);
                    //mainPlayList.list = new ArrayList<String>(new LinkedHashSet<String>(mainPlayList.list));

                    File[] folder = videoDir.listFiles();
                    ArrayList<String> tempString = new ArrayList<>();
                    for (File path : folder) {
                        tempString.add(path.getAbsolutePath());
                    }
                    //playTheplayList(temp);
                } else {
                    try {
                        videoDir.mkdirs();
                        /*downloadPlaylist("videos");*/
                        downloadPlaylist("testPlaylist");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }
        });
    }

    private void saveThePlayList() {
        AsyncHandler.post(() -> {
            sharedPreferences = context.getSharedPreferences("play_list", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("size", uriPlayList.size());

            for(int i=0;i<uriPlayList.size();i++)
            {
                editor.putString("video_" + i, String.valueOf(uriPlayList.get(i)));
            }

            editor.apply();
        });
    }

    public void loadThePlayList(){
        Log.d("function", "loadThePlayList called");

        final int[] size = new int[1];
        uriPlayList.clear();
        AsyncHandler.post(() -> {
            sharedPreferences = context.getSharedPreferences("play_list", Context.MODE_PRIVATE);
            UiHandler.post(() -> {
                size[0] = sharedPreferences.getInt("size", 0);
                for(int i=0;i<size[0];i++)
                {
                    uriPlayList.add(i,Uri.parse(sharedPreferences.getString("video_"+i, null)));

                }
                Log.e("function", "isfinishloading");
                playOffline();
            });
        });
    }

    public void playOffline(){
        Log.d("function", "PlayOffline called");
        if(uriPlayList.size()==0){
            MainActivity.noConnectionText.setVisibility(View.VISIBLE);
            MainActivity.obj.setVariableChangeListener(task -> {
                Log.d("function", "connection_changed");
                MainActivity.noConnectionText.setVisibility(View.GONE);
                /*downloadPlaylist("videos")*/;
                downloadPlaylist("testPlaylist");
            });
        } else {
            onTrack = 0;
            //mainVideoView.setVideoPath(mainPlayList.list.get(mainPlayList.onTrack));
            mainVideoView.setVideoURI(uriPlayList.get(onTrack));
            mainVideoView.start();
            MainActivity.mainVideoView.setOnCompletionListener(mp -> {
                if(MainActivity.isConnection){
                    Log.d("function", "isConnected");
                    /*downloadPlaylist("videos");*/
                    downloadPlaylist("testPlaylist");
                }
                if (onTrack < uriPlayList.size()-1) {
                    onTrack++;
                } else {
                    onTrack = 0;
                }
                //  mainVideoView.setVideoPath(mainPlayList.list.get(onTrack));
                mainVideoView.setVideoURI(uriPlayList.get(onTrack));
                mainVideoView.start();
            });
        }
    }
    public void playTheplayList(){
        mainVideoView.stopPlayback();
        uriPlayList.clear();
        int counter = 0;
        Log.d("function","playTheplayList called");
        Iterable<DataSnapshot> list =  listSnapshot.getChildren();
        mainPlayList.list.clear();
        for (DataSnapshot data: list){
            mainPlayList.list.add( videoDir.getAbsolutePath()+"/"+data.getValue().toString());
            counter++;
        }
        File[] lf = videoDir.listFiles();
        ArrayList<String> temp = new ArrayList<>();
        for (File file :lf){
            temp.add(file.getAbsolutePath());
        }
        for (String path :mainPlayList.list){
            uriPlayList.add(Uri.parse(path));
        }


        // mainVideoView.setVideoPath(mainPlayList.list.get(0));
        if (temp.containsAll(mainPlayList.list)){
            final Bundle bundle = new Bundle();
            //mainVideoView.setVideoPath(mainPlayList.list.get(0));
            mainPlayList.onTrack =0;
            //mainVideoView.setVideoPath(mainPlayList.list.get(mainPlayList.onTrack));

            mainVideoView.setVideoURI(uriPlayList.get(onTrack));
            mainVideoView.start();
            MainActivity.mainVideoView.setOnCompletionListener(mp -> {
                Log.d("function","playTheplayList - on complate calld");

                if (mainPlayList.onTrack < MainActivity.mainPlayList.list.size()){
                    mainPlayList.onTrack++;
                }
                if (onTrack == MainActivity.mainPlayList.list.size()){
                    mainPlayList.onTrack=0;
                }

                //  mainVideoView.setVideoPath(mainPlayList.list.get(onTrack));
                mainVideoView.setVideoURI(uriPlayList.get(onTrack));

                saveThePlayList();
                try{
                    event = uriPlayList.get(onTrack).toString();
                    event = event.substring(event.lastIndexOf("/") + 1);
                }catch (Exception e){
                    e.getCause();
                }
                mainVideoView.start();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,event);
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video");
                mFirebaseAnalytics.logEvent("VideoPlayed",bundle);
            });
        }else {
            downloadMissVideos(videoDir,mainPlayList.list);
            
        }
    }

    private boolean checkFolderExists(File dir){
        Log.d("function","checkFolderExists calld");

        return dir.exists();
    }

    private void downloadMissVideos(File dir, ArrayList<String> playlistPaths) {
        downloadIcon.setVisibility(View.VISIBLE);

        Log.d("function","downloadMissVideos calld");

        File[] lf = dir.listFiles();
        ArrayList<String> onDevice = new ArrayList<>();
        ArrayList<String> toDownloadList = new ArrayList<>();
        if (lf != null) {
            for (File aLf : lf) {
                onDevice.add(aLf.getAbsolutePath());
            }
            if (playlistPaths.size() == onDevice.size() || playlistPaths.size() < onDevice.size() ){
                for(String path : playlistPaths){
                    if (!onDevice.contains(path)){
                        File temp =new File(path);
                        toDownloadList.add(temp.getName());
                    }
                }
            }
            else if (playlistPaths.size() > onDevice.size()){
                for(String devicePath : onDevice){
                    if (playlistPaths.contains(devicePath)){
                        playlistPaths.remove(devicePath);
                    }
                }
                for (String path : playlistPaths){
                    File temp =new File(path);
                    toDownloadList.add(temp.getName());
                }
            }
            fetchFilesFromFireBaseStorage(toDownloadList);

        }

    }
    private void fetchFilesFromFireBaseStorage(final ArrayList<String> toDownloadList){
        Log.d("function","fetchFilesFromFireBaseStorage calld");
        try {
            downloadFinishd = false;
            pstep = 100/toDownloadList.size();
            for (String fileName : toDownloadList) {
                storageRef = storage.getReferenceFromUrl("gs://star8videoapp.appspot.com").child(fileName);
                final File videoFile = new File(videoDir, fileName);
                storageRef.getFile(videoFile).addOnSuccessListener(taskSnapshot -> {
                    Log.d("function","fetchFilesFromFireBaseStorage - onSucsees calld");

                    System.out.println("make file: " + videoFile.getPath());
                }).addOnFailureListener(exception -> {
                    Log.d("function","fetchFilesFromFireBaseStorage - onFail calld");
                    if (videoFile.exists()){
                        Log.d("deleting","deleting video: " + videoFile.getPath());
                        try {
                            videoFile.delete();
                            Log.d("deleting","successes");
                            dcount ++;

                        }catch (Exception e){
                            e.getCause();
                            Log.d("deleting","Field");

                        }
                    }
                    exception.getCause();
                }).addOnCompleteListener(task -> {
                    Log.d("function","fetchFilesFromFireBaseStorage - on complate calld");

                    dcount++;
                    p=p+pstep;
                    if (dcount == toDownloadList.size()){
                        Log.d("status:","complete");
                        dcount = 0;
                        p = 0;
                        downloadFinishd = true;
                        downloadIcon.setVisibility(View.INVISIBLE);
                        playTheplayList();
                    }
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("Main", "IOE Exception");
        }
    }

    private int allVideosOnDevice(File dir, ArrayList<String> filesName) {
        Log.d("function","allVideosOnDevice calld");

        int fleg = 2;
        File[] lf = dir.listFiles();
        if (lf != null) {
            ArrayList<String> onDevice = new ArrayList<>();
            ArrayList<String> needToBeOnDevice = new ArrayList<>();
            for (File filePath : lf) {
                onDevice.add(filePath.getAbsolutePath());
            }
            for (String name : filesName) {
                needToBeOnDevice.add(dir.getAbsolutePath() + "/" + name);
            }
            if (onDevice.containsAll(needToBeOnDevice)) {
                fleg =  1;
            }
            else {
                fleg =2;
            }
        }
        return  fleg;
    }
}



