package com.apps.koru.star8_video_app.downloadclass;


import com.apps.koru.star8_video_app.events.downloadsEvents.DownloadEventStage0;
import com.apps.koru.star8_video_app.events.downloadsEvents.MissFileEvent;
import com.apps.koru.star8_video_app.objects.Model;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

public class MissFileFinder2 {
    Model appMpdel = Model.getInstance();
    public MissFileFinder2() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onMissFileEvent(MissFileEvent event){
        ArrayList <String> adsNames = event.getList();
        ArrayList<String> toDownload  = new ArrayList<>();
        ArrayList<String> onDevice  = new ArrayList<>();
        File [] lf = appMpdel.videoDir.listFiles();
        if (lf != null && lf.length>0){
            for (File  file : lf){
                onDevice.add(file.getName());
            }
            for (String adname : adsNames){
                if (!(onDevice.contains(adname))){
                    toDownload.add(adname);
                }
            }
            EventBus.getDefault().post(new DownloadEventStage0(toDownload));
        }

    }
}
