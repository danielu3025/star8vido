package com.apps.koru.star8_video_app.downloadclass;

import com.apps.koru.star8_video_app.objects.Model;
import com.apps.koru.star8_video_app.events.DownloadCompleteEvent;
import com.apps.koru.star8_video_app.events.MissVideosEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * this class job is to listen changes in firebase db
 * it calls also when app get acseess to the db
 * this class decides if it nedd to download new content or start playing
 */
public class FireBaseDbListener {
    private Model appModel = Model.getInstance();

    public FireBaseDbListener() {
        appModel.playlistNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get playlist files names
                appModel.listSnapshot = dataSnapshot;
                appModel.dbList.clear();
                appModel.playlistFileNames.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    appModel.playlistFileNames.add((String) postSnapshot.getValue());
                    appModel.videoListphats.add(appModel.videoDir.getAbsolutePath() + "/" + postSnapshot.getValue());
                    appModel.dbList.add(appModel.videoDir.getAbsolutePath() + "/" + postSnapshot.getValue());
                }
                appModel.playlistFileNames = new ArrayList<>(new LinkedHashSet<>(appModel.playlistFileNames));
                appModel.videoListphats = new ArrayList<>(new LinkedHashSet<>(appModel.videoListphats));

                //check if playlist Folder is exists
                if (appModel.mainPlayList.checkFolderExists(appModel.videoDir)) {
                    //all videos are in storage ?
                    switch (appModel.mainPlayList.allVideosOnDevice(appModel.videoDir, appModel.playlistFileNames)) {
                        case 1: // all videos is in storage
                            appModel.mainPlayListTemp.list.clear();
                            for (int i = 0; i < appModel.playlistFileNames.size(); i++) {
                                appModel.mainPlayListTemp.list.add(appModel.videoDir.getAbsolutePath() + "/" + appModel.playlistFileNames.get(i));
                            }
                            //playTheplayList
                            EventBus.getDefault().post(new DownloadCompleteEvent("play"));
                            break;
                        case 2:// not all videos are in the storage
                            EventBus.getDefault().post(new MissVideosEvent("download"));
                            appModel.mainPlayListTemp.list.clear();
                            for (int i = 0; i < appModel.playlistFileNames.size(); i++) {
                                appModel.mainPlayListTemp.list.add(appModel.videoDir.getAbsolutePath() + "/" + appModel.playlistFileNames.get(i));
                            }
                            break;
                        default:
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println( databaseError.getMessage());

            }
        });
    }
}

