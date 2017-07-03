package com.apps.koru.star8_video_app.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.apps.koru.star8_video_app.events.DownloadCompleteEvent;
import com.apps.koru.star8_video_app.sharedutils.AsyncHandler;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;


public class VideoPlayer {
    private SharedPreferences sharedPreferences;
    private Context context;
    private Model appModel = Model.getInstance();
    private int onTrack =0;

    public VideoPlayer(Context context) {
        EventBus.getDefault().register(this);
        this.context=context;
    }
    @Subscribe
    public void onEvent(DownloadCompleteEvent event) {
        if (appModel.dbList.size() != 0) {
            System.out.println("lets playyy!!!!!");
            appModel.infoBt.setText("");
            appModel.infoBt.setVisibility(View.INVISIBLE);
            appModel.videoView.stopPlayback();
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
            onTrack = 0;
            appModel.videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
            appModel.videoView.start();
            saveThePlayList();
            appModel.videoView.setOnErrorListener((mp, what, extra) -> {
                Log.d("Error", " - playing video error");
                if (onTrack > 0) {
                    if (onTrack != appModel.uriPlayList.size()) {
                        appModel.videoView.setVideoURI(appModel.uriPlayList.get(onTrack + 1));
                    } else {
                        appModel.videoView.setVideoURI(appModel.uriPlayList.get(0));
                    }
                } else {
                    appModel.videoView.setVideoURI(appModel.uriPlayList.get(0));
                }
                appModel.videoView.start();
                return true;
            });
            appModel.videoView.setOnCompletionListener(mp -> {
                if (onTrack < appModel.uriPlayList.size()) {
                    onTrack++;
                }
                if (onTrack == appModel.uriPlayList.size()) {
                    onTrack = 0;
                }
                appModel.videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
                appModel.videoView.start();
            });
        } else {
            Log.d("function","error calling video player");
        }
    }

    private void saveThePlayList() {
        AsyncHandler.post(() -> {
            sharedPreferences = context.getSharedPreferences("play_list", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("size", appModel.uriPlayList.size());

            for(int i=0;i<appModel.uriPlayList.size();i++)
            {
                editor.putString("video_" + i, String.valueOf(appModel.uriPlayList.get(i)));
            }

            editor.apply();
        });
    }
}
