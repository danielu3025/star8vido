package com.apps.koru.star8_video_app.objects;

import android.net.Uri;
import android.util.Log;

import com.apps.koru.star8_video_app.events.DownloadCompleteEvent;
import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.events.VideoViewEvent;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;


public class VideoPlayer {
    private Model appModel = Model.getInstance();

    public VideoPlayer() {
        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void onEvent(DownloadCompleteEvent event) {
        if (appModel.dbList.size() != 0) {
            System.out.println("lets playyy!!!!!");
            EventBus.getDefault().post(new InfoEvent(""));
            EventBus.getDefault().post(new InfoEvent("invis"));

            //get Uri play List
            File lf[] = appModel.videoDir.listFiles();
            ArrayList<String> folderList = new ArrayList<>();
            for (File file : lf) {
                folderList.add(file.getAbsolutePath());
            }
            appModel.uriPlayList.clear();

            for (String path : appModel.dbList) {
                appModel.uriPlayList.add(Uri.parse(path));
            }

            appModel.needToRefrash = true;

            if (!appModel.playingVideosStarted){
                EventBus.getDefault().post(new VideoViewEvent());
            }

        } else {
            Log.d("function","error calling video player");
        }
    }
}
