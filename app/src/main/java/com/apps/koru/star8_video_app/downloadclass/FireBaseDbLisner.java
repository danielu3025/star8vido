package com.apps.koru.star8_video_app.downloadclass;

import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.DownloadCompleteEvent;
import com.apps.koru.star8_video_app.events.MissVideosEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashSet;


/**
 * Created by danielluzgarten on 28/06/2017.
 */

public class FireBaseDbLisner {
    public Model appModel = Model.getInstance();

    public FireBaseDbLisner() {
        appModel.playlistNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get playlist files names
                appModel.listSnapshot = dataSnapshot;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    appModel.playlistFileNames.add((String) postSnapshot.getValue());
                    appModel.videoListphats.add(appModel.videoDir.getAbsolutePath() + "/" + (String) postSnapshot.getValue());
                    appModel.dbList.add(appModel.videoDir.getAbsolutePath() + "/" + (String) postSnapshot.getValue());
                }
                appModel.playlistFileNames = new ArrayList<>(new LinkedHashSet<>(appModel.playlistFileNames));
                appModel.videoListphats = new ArrayList<>(new LinkedHashSet<>(appModel.videoListphats));
                //check if playlist Folder is exists
                if (appModel.mainPlayList.checkFolderExists(appModel.videoDir)) {
                    //all videos are in storage ?
                    switch (appModel.mainPlayList.allVideosOnDevice(appModel.videoDir, appModel.playlistFileNames)) {
                        case 1: // all videos is in storage
                            appModel.mainPlayListTemp.list.removeAll(appModel.mainPlayListTemp.list);
                            for (int i = 0; i < appModel.playlistFileNames.size(); i++) {
                                appModel.mainPlayListTemp.list.add(appModel.videoDir.getAbsolutePath() + "/" + appModel.playlistFileNames.get(i));
                            }
                            //playTheplayList
                            EventBus.getDefault().post(new DownloadCompleteEvent("play"));
                            break;
                        case 2:// not all videos are in the storage
                            //downloadMissVideos(videoDir, videoListphats);
                            EventBus.getDefault().post(new MissVideosEvent("download"));
                            appModel.mainPlayListTemp.list.clear();
                            for (int i = 0; i < appModel.playlistFileNames.size(); i++) {
                                appModel.mainPlayListTemp.list.add(appModel.videoDir.getAbsolutePath() + "/" + appModel.playlistFileNames.get(i));
                            }
                            break;
                        default:
                    }
                    //appModel.playlistFileNames.clear();
                    //appModel.videoListphats.clear();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println( databaseError.getMessage());

            }
        });
    }
}
