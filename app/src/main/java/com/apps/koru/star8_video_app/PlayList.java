package com.apps.koru.star8_video_app;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.apps.koru.star8_video_app.sharedutils.AsyncHandler;
import com.apps.koru.star8_video_app.sharedutils.UiHandler;
import com.google.firebase.analytics.FirebaseAnalytics;


import java.io.File;
import java.util.ArrayList;


import static com.apps.koru.star8_video_app.MainActivity.videoView;


public class PlayList extends AppCompatActivity {
    static ArrayList<Uri> uriPlayList = new ArrayList<>();
    String event;
    public ArrayList<String> list = new ArrayList<>();
    int onTrack =-1;
    Context context;
    File videoDir;
    private FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences sharedPreferences;


    public PlayList(Context contex) {
        Log.d("function", "PlayList contractor calld");
        context = contex;
        videoDir = new File(context.getExternalCacheDir().getAbsolutePath() + "/playlist1");
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
           // MainActivity.noConnectionText.setVisibility(View.VISIBLE);
            MainActivity.obj.setVariableChangeListener(task -> {
                Log.d("function", "connection_changed");
                //MainActivity.noConnectionText.setVisibility(View.GONE);
                //downloadPlaylist("videos");
                /*downloadPlaylist("testPlaylist");*/
            });
        } else {
            onTrack = 0;
            //videoView.setVideoPath(mainPlayList.list.get(mainPlayList.onTrack));
            videoView.setVideoURI(uriPlayList.get(onTrack));
            videoView.start();
            MainActivity.videoView.setOnCompletionListener(mp -> {
                if(MainActivity.isConnection){
                    Log.d("function", "isConnected");
                    //downloadPlaylist("videos");
                    /*downloadPlaylist("testPlaylist");*/
                }
                if (onTrack < uriPlayList.size()-1) {
                    onTrack++;
                } else {
                    onTrack = 0;
                }
                //  videoView.setVideoPath(mainPlayList.list.get(onTrack));
                videoView.setVideoURI(uriPlayList.get(onTrack));
                videoView.start();
            });
        }
    }
    public boolean checkFolderExists(File dir){
        Log.d("function","checkFolderExists calld");

        return dir.exists();
    }
    public int allVideosOnDevice(File dir, ArrayList<String> filesName) {
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



