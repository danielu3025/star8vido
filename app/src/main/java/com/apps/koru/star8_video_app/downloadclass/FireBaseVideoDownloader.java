package com.apps.koru.star8_video_app.downloadclass;

import android.util.Log;

import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.objects.Model;
import com.apps.koru.star8_video_app.events.DownloadCompleteEvent;
import com.apps.koru.star8_video_app.events.DownloadFilesEvent;
import com.apps.koru.star8_video_app.events.MissVideosEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * this class job is to download a playlist from fire base storge.
 * the class get a list with event bus and then start an async download
 * if error was happen in the download.. it will skip it and try to download it after he finish.
 * when download fully complete it send an event for playing
 */

public class FireBaseVideoDownloader {
    private int dcount = 0;
    private Boolean erorFlag  = false;
    private Model appModel = Model.getInstance();
    private  String tempText ="";
    public FireBaseVideoDownloader() {

        EventBus.getDefault().register(this);
    }

    @SuppressWarnings("VisibleForTests")
    @Subscribe
    public void onEvent(DownloadFilesEvent event) {
        if (appModel.storageRef==null || appModel.storageRef.getActiveDownloadTasks().size() == 0) {

            EventBus.getDefault().post(new InfoEvent("vis"));
            EventBus.getDefault().post(new InfoEvent("Downloading..."));
            try {
                for (String fileName : event.getList()) {
                    appModel.storageRef = appModel.storage.getReferenceFromUrl(appModel.storgeUrl).child(fileName);
                    final File videoFile = new File(appModel.videoDir, fileName);
                    System.out.println("Downloading file: " + videoFile.getName());
                    appModel.storageRef.getFile(videoFile).addOnSuccessListener(taskSnapshot -> {
                    }).addOnFailureListener(exception -> {
                        EventBus.getDefault().post(new InfoEvent("Download Error"));
                        erorFlag = true;
                        if (videoFile.exists()) {
                            Log.d("deleting", "deleting video: " + videoFile.getPath());
                            try {
                                videoFile.delete();
                                Log.d("deleting", "successes");
                                // dcount ++;
                            } catch (Exception e) {
                                e.getCause();
                                Log.d("deleting", "Field");
                            }
                        }
                        exception.getCause();
                    }).addOnCompleteListener(task -> {
                        Log.d("Complete from total", (dcount + 1) + "/" + event.getList().size());
                        if (tempText != "Downloading videos :" + (dcount+1) +"/"+event.getList().size()){
                            tempText = "Downloading videos :" + (dcount+1) +"/"+event.getList().size();
                            EventBus.getDefault().post(new InfoEvent(tempText));

                        }
                        dcount++;
                        if (dcount == event.getList().size()) {
                            Log.d("status:", "complete");
                            dcount = 0;
                            appModel.downloadFinishd = true;
                            if (erorFlag) {
                                erorFlag = false;
                                EventBus.getDefault().post(new MissVideosEvent("problem"));
                            } else {
                                File[] lf = appModel.videoDir.listFiles();
                                ArrayList<String> folderPhats = new ArrayList<>();
                                for (File file :lf){
                                    folderPhats.add(file.getAbsolutePath());
                                }
                                if(folderPhats.containsAll(appModel.dbList)) {
                                    EventBus.getDefault().post(new DownloadCompleteEvent("done"));
                                } else {
                                    EventBus.getDefault().post(new MissVideosEvent("more downloads"));
                                }
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Main", "IOE Exception");
            }
        } else {
            System.out.println("already downloading");
        }
    }
}

