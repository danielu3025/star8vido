package com.apps.koru.star8_video_app.downloadclass;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.apps.koru.star8_video_app.MainActivity;
import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.DownloadCompleteEvent;
import com.apps.koru.star8_video_app.events.DownloadFilesEvent;
import com.apps.koru.star8_video_app.events.MissVideosEvent;
import com.apps.koru.star8_video_app.events.PlayThePlayListEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


/**
 * Created by danielluzgarten on 30/06/2017.
 */

public class FireBaseVideoDownloader {
    int dcount = 0;
    Boolean erorFlag  = false;
    Model appModel = Model.getInstance();
    public FireBaseVideoDownloader() {

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(DownloadFilesEvent event) {
        MainActivity.infoBt.setText("Downloading...");
        try {
           // MainActivity.infoBt.setVisibility(View.VISIBLE);
            //MainActivity.infoBt.setText("getting ready to download..");

            for (String fileName : event.getList()){
                appModel.storageRef = appModel.storage.getReferenceFromUrl(appModel.storgeUrl).child(fileName);
                final File videoFile = new File(appModel.videoDir.getParent(),fileName);

                appModel.storageRef.getFile(videoFile).addOnSuccessListener(taskSnapshot -> {
                    System.out.println("Downloading file: " + videoFile.getName());

                }).addOnFailureListener(exception -> {
                    //MainActivity.infoBt.setText("Downloading error!");
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
                    //MainActivity.infoBt.setText("Downloading videos :"  + (dcount+1) + "/" + event.getList().size());

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
                        //MainActivity.infoBt.setText("");
                        Log.d("status:","complete");
                        dcount = 0;
                        appModel.downloadFinishd = true;
                        //downloadIcon.setVisibility(View.INVISIBLE);
                        // playTheplayList();
                        if (erorFlag){
                            erorFlag =false;
                            EventBus.getDefault().post(new MissVideosEvent("problem"));
                        }
                        else {
                            //MainActivity.infoBt.setVisibility(View.INVISIBLE);
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

    public void copy(File src, File dst) throws IOException {

        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
