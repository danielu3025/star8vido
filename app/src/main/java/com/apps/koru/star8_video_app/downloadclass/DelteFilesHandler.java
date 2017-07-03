package com.apps.koru.star8_video_app.downloadclass;

import com.apps.koru.star8_video_app.events.DeleteVideosEvent;
import com.apps.koru.star8_video_app.events.DownloadFilesEvent;
import com.apps.koru.star8_video_app.objects.Model;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

public class DelteFilesHandler {
    Model appModel = Model.getInstance();

    public DelteFilesHandler() {
        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void onEvent(DeleteVideosEvent event) {
        File[] lf = appModel.videoDir.listFiles();
        ArrayList<String> folderPhats = new ArrayList<>();
        for (File file :lf){
            folderPhats.add(file.getAbsolutePath());
        }
        for (String path : folderPhats){
            if (!event.getList().contains(path)){
                File  toDelte = new File(path);
                try {
                    toDelte.delete();
                    System.out.println("**cleaning files: " + path + "eas deleted");
                }catch (Exception e){
                    System.out.println(e.getCause());
                }
            }
        }
    }
}
