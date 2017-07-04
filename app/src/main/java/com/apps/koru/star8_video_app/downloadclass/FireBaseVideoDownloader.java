package com.apps.koru.star8_video_app.downloadclass;

import android.util.Log;
import android.view.View;

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

public class FireBaseVideoDownloader {
    private int dcount = 0;
    private Boolean erorFlag  = false;
    private Model appModel = Model.getInstance();
    public FireBaseVideoDownloader() {

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(DownloadFilesEvent event) {
        appModel.infoBt.setVisibility(View.VISIBLE);
        appModel.infoBt.getWindowVisibility();
        appModel.infoBt.setText("Downloading...");
        try {
            for (String fileName : event.getList()){
                appModel.storageRef = appModel.storage.getReferenceFromUrl(appModel.storgeUrl).child(fileName);
                final File videoFile = new File(appModel.videoDir.getParent(),fileName);
                System.out.println("Downloading file: " + videoFile.getName());
                appModel.storageRef.getFile(videoFile).addOnSuccessListener(taskSnapshot -> {}).addOnFailureListener(exception -> {
                    //appModel.infoBt.setText("Downloading error!");
                    erorFlag = true;
                    if (videoFile.exists()){
                        Log.d("deleting","deleting video: " + videoFile.getPath());
                        try {
                            videoFile.delete();
                            Log.d("deleting","successes");
                           // dcount ++;
                        }catch (Exception e){
                            e.getCause();
                            Log.d("deleting","Field");
                        }
                    }
                    exception.getCause();
                }).addOnCompleteListener(task -> {
                    Log.d("Complete from total",(dcount+1) + "/" + event.getList().size() );
                    //appModel.infoBt.setText("Downloading videos :"  + (dcount+1) + "/" + event.getList().size());

                    try {
                        File temp =  new File(appModel.videoDir+"/"+fileName);
                        if (!temp.exists()){
                            copy(videoFile,temp);
                            System.out.println("copy: " + videoFile.getName());
                            videoFile.delete();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println(" ERROR in copying" + videoFile.getName());
                    }
                    dcount++;
                    if (dcount == event.getList().size()){
                        //appModel.infoBt.setText("");
                        Log.d("status:","complete");
                        dcount = 0;
                        appModel.downloadFinishd = true;
                        if (erorFlag){
                            erorFlag =false;
                            EventBus.getDefault().post(new MissVideosEvent("problem"));
                        }
                        else {
                            EventBus.getDefault().post(new DownloadCompleteEvent("done"));
                        }
                    }
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("Main", "IOE Exception");
        }
    }

    private void copy(File src, File dst) throws IOException {

        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}

