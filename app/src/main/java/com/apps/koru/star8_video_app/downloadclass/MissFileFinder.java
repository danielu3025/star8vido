package com.apps.koru.star8_video_app.downloadclass;

import android.util.Log;

import com.apps.koru.star8_video_app.objects.Model;
import com.apps.koru.star8_video_app.events.MissVideosEvent;
import com.apps.koru.star8_video_app.events.DownloadFilesEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;


/**
 this class need to ba activated by EventBus event trigger.

 the only thing this class is doing is to  compare
 the video files in the storage with the list of video files from the DB
 and remove from the the DB list the files that are in the storage allready
 after that it update the toDownload list in the model and call post an Event that means he's finished
 is part and now the downloading action can start
 */

public class MissFileFinder {
    Model appModel = Model.getInstance();

    public MissFileFinder() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(MissVideosEvent event) {
        Log.d("function","downloadMissVideos calld");

        File[] lf = appModel.videoDir.listFiles();
        ArrayList<String> onDevice = new ArrayList<>();
        ArrayList<String> toDownloadList = new ArrayList<>();
        if (lf != null) {
            for (File aLf : lf) {
                onDevice.add(aLf.getAbsolutePath());
            }
            if (appModel.videoListphats.size() == onDevice.size() || appModel.videoListphats.size() < onDevice.size()) {
                for (String path : appModel.videoListphats) {
                    if (!onDevice.contains(path)) {
                        File temp = new File(path);
                        toDownloadList.add(temp.getName());
                    }
                }
            } else if (appModel.videoListphats.size() > onDevice.size()) {
                for (String devicePath : onDevice) {
                    if (appModel.videoListphats.contains(devicePath)) {
                        appModel.videoListphats.remove(devicePath);
                    }
                }
                for (String path : appModel.videoListphats) {
                    File temp = new File(path);
                    toDownloadList.add(temp.getName());
                }
            }
            EventBus.getDefault().post(new DownloadFilesEvent(toDownloadList));
        }
    }

}
