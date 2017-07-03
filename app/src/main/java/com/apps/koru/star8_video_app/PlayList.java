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




public class PlayList extends AppCompatActivity {
    static ArrayList<Uri> uriPlayList = new ArrayList<>();
    String event;
    public ArrayList<String> list = new ArrayList<>();
    int onTrack =-1;
    Context context;
    File videoDir;
    private FirebaseAnalytics mFirebaseAnalytics;

    public PlayList(Context contex) {
        Log.d("function", "PlayList contractor calld");
        context = contex;
        videoDir = new File(context.getExternalCacheDir().getAbsolutePath() + "/playlist1");
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



