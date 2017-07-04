package com.apps.koru.star8_video_app.objects;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import java.io.File;
import java.util.ArrayList;




public class PlayList extends AppCompatActivity {
    String event;
    public ArrayList<String> list = new ArrayList<>();
    int onTrack =-1;
    Context context;
    File videoDir;

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

        int flag = 2;
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
                flag =  1;
            }
            else {
                flag =2;
            }
        }
        return  flag;
    }
}



