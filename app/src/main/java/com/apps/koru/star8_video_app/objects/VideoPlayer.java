package com.apps.koru.star8_video_app.objects;

import android.net.Uri;
import android.util.Log;

import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.DownloadCompleteEvent;
import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.events.VideoViewEvent;
import com.apps.koru.star8_video_app.events.testEvents.TestplayListEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;


public class VideoPlayer {
    private Model appModel = Model.getInstance();
    TimeHendler timeHendler = new TimeHendler() ;

    public VideoPlayer() {
        EventBus.getDefault().register(this);
        appModel.urisPlayLists = new ArrayList<>();
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

            if (appModel.urisPlayLists.size()>0){
                appModel.urisPlayLists.clear();
            }

            for (ArrayList<String> arrayList: appModel.playlists){
                ArrayList<Uri> uriList = new ArrayList<>();
                for (String videoName : arrayList){
                    uriList.add(Uri.parse(appModel.videoDir.getAbsolutePath()+"/"+videoName));
                }
                appModel.urisPlayLists.add(uriList);
            }
            appModel.hour = timeHendler.getHouer();
            appModel.uriPlayList =  appModel.urisPlayLists.get(appModel.hour);


            EventBus.getDefault().post(new TestplayListEvent());
            //EventB us.getDefault().post(new SaveThePlayListEvent("save"));
            appModel.needToRefrash = true;

            if (!appModel.playingVideosStarted){
               // EventBus.getDefault().post(new SaveThePlayListEvent("save"));
                EventBus.getDefault().post(new VideoViewEvent());
            }

        } else {
            Log.d("function","error calling video player");
        }
    }
}
