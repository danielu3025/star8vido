package com.apps.koru.star8_video_app.downloadclass;

import android.util.Log;

import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.DeleteVideosEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

/**
 *  this class job is to delete all videos that are not nursery for the play list
 *  the class response to  event bus - DeleteVideosEvent.
 *  then it take the list of videos it get and compere them with the videos in the device storage
 *  any file on storage that is not found in the list get deleted
 */
public class DeleteFilesHandler {
    private Model appModel = Model.getInstance();
    private String message;
    private ArrayList<String> list;
    public DeleteFilesHandler() {
        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void onEvent(DeleteVideosEvent event) {

        list  = event.getList();
        // get all files from folder
        File[] lf = appModel.videoDir.listFiles();
        ArrayList<String> paths = new ArrayList<>();
        ArrayList<String> toDelet = new ArrayList<>();
        for (File file : lf){
            String name = file.getName();
            name = name.toUpperCase();
            if (!name.contains("STAR8")){
                paths.add(file.getAbsolutePath());
            }
        }
        //isolate only files that need to br removed
        for (String name : paths){
            if (!list.contains(name)){
                toDelet.add(name);
            }
        }

        //delete files

        if (toDelet.size()>0){
            for (String filepath : toDelet){
                File  file=  new File(filepath);
                if (file.exists()){
                    Log.d("Delteting","trying to delete - " + file.getName());
                    try {
                        file.delete();
                        Log.d("Delteting","deleted - " + file.getName());
                    }
                    catch (Exception e){
                        e.getMessage();
                        Log.d("Delteting","failed to  delete - " + file.getName());

                    }
                }
            }
        }
    }
}
