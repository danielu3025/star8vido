package com.apps.koru.star8_video_app;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.apps.koru.star8_video_app.MainActivity;
import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.DownloadCompleteEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by danielluzgarten on 30/06/2017.
 */

public class VideoPlayer {
    Model appModel = Model.getInstance();
    int onTrack =0;

    public VideoPlayer() {

        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void onEvent(DownloadCompleteEvent event) {
        System.out.println("lets playyy!!!!!");
        MainActivity.infoBt.setText("");
        MainActivity.infoBt.setVisibility(View.INVISIBLE);
        MainActivity.videoView.stopPlayback();
        //get Uri play List
        File lf[] = appModel.videoDir.listFiles();
        ArrayList <String >folderList = new ArrayList<>();
        for (File file : lf){
            folderList.add(file.getAbsolutePath());
        }

        appModel.uriPlayList.clear();

        for (String path :appModel.dbList){
            appModel.uriPlayList.add(Uri.parse(path));
        }
        appModel.dbList.clear();
            onTrack = 0;
            MainActivity.videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
            MainActivity.videoView.start();
            MainActivity.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d("Error"," - playing video error");
                    if (onTrack > 0){
                        if (onTrack != appModel.uriPlayList.size()){
                            MainActivity.videoView.setVideoURI(appModel.uriPlayList.get(onTrack + 1));
                        }
                        else {
                            MainActivity.videoView.setVideoURI(appModel.uriPlayList.get(0));
                        }
                    }
                    else {
                        MainActivity.videoView.setVideoURI(appModel.uriPlayList.get(0));
                    }
                    MainActivity.videoView.start();
                    return true;
                }
            });
        MainActivity.videoView.setOnCompletionListener(mp -> {
                 if (onTrack<appModel.uriPlayList.size()){
                     onTrack++;
                 }
                 if (onTrack==appModel.uriPlayList.size()){
                     onTrack = 0;
                 }
                MainActivity.videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
                MainActivity.videoView.start();
            });

    }
}
