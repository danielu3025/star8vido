package com.apps.koru.star8_video_app.downloadclass;

import android.os.Bundle;

import com.apps.koru.star8_video_app.events.DownloadCompleteEvent;
import com.apps.koru.star8_video_app.events.MissVideosEvent;
import com.apps.koru.star8_video_app.events.testEvents.TestplayListEvent;
import com.apps.koru.star8_video_app.Model;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * this class job is to listen changes in firebase db
 * it calls also when app get acseess to the db
 * this class decides if it needs to download new content or start playing
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
                appModel.videoListphats.clear();
                appModel.playlistFileNames.clear();
                appModel.playlists.clear();
                for (int i = 0 ;i<24; i++){
                    appModel.playlists.add(new ArrayList<String>());
                }
                if (dataSnapshot.getChildrenCount()<1){
                    System.out.println("empty playlist");
                    Bundle params = new Bundle();
                    params.putString("status","emptyPlaylist");
                    appModel.mFirebaseAnalytics.logEvent("emptyPlatList", params);
                    //fabric
                    Answers.getInstance().logCustom(new CustomEvent("emptyPlatList").putCustomAttribute("status","emptyPlaylist"));
                }
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    for (DataSnapshot aPlaylist: postSnapshot.getChildren()){
//                        appModel.playlists.get(Integer.parseInt(postSnapshot.getKey())).add(aPlaylist.getValue().toString());
//                        appModel.playlistFileNames.add(aPlaylist.getValue().toString());
//                        appModel.videoListphats.add(appModel.videoDir.getAbsolutePath() + "/" + aPlaylist.getValue());
////                        appModel.dbList.add(appModel.videoDir.getAbsolutePath() + "/" + aPlaylist.getValue());
//                    }

                    appModel.playlistFileNames.add((String) postSnapshot.getValue());
                    appModel.videoListphats.add(appModel.videoDir.getAbsolutePath() + "/" + postSnapshot.getValue());
                    appModel.dbList.add(appModel.videoDir.getAbsolutePath() + "/" + postSnapshot.getValue());
//
                    appModel.playlistFileNames = new ArrayList<>(new LinkedHashSet<>(appModel.playlistFileNames));
                    appModel.videoListphats = new ArrayList<>(new LinkedHashSet<>(appModel.videoListphats));
                }

                System.out.println(appModel.playlists.size());
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
                            EventBus.getDefault().post(new TestplayListEvent());

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


