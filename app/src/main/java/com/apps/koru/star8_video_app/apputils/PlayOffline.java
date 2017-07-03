package com.apps.koru.star8_video_app.apputils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.apps.koru.star8_video_app.objects.Model;
import com.apps.koru.star8_video_app.sharedutils.AsyncHandler;
import com.apps.koru.star8_video_app.sharedutils.UiHandler;

public class PlayOffline {
    private Model appModel = Model.getInstance();
    private SharedPreferences sharedPreferences;
    private Context context;

    public PlayOffline(Context context) {
        this.context=context;
    }

    public void loadThePlayList(){
        Log.d("function", "loadThePlayList called");

        final int[] size = new int[1];
        appModel.uriPlayList.clear();
        AsyncHandler.post(() -> {
            sharedPreferences = context.getSharedPreferences("play_list", Context.MODE_PRIVATE);
            UiHandler.post(() -> {
                size[0] = sharedPreferences.getInt("size", 0);
                for(int i=0;i<size[0];i++)
                {
                    appModel.uriPlayList.add(i, Uri.parse(sharedPreferences.getString("video_"+i, null)));

                }
                Log.e("function", "isfinishloading");
                playOffline();
            });
        });
    }
    private void playOffline(){
        if(appModel.uriPlayList.size()>0) {
            Log.d("function", "PlayOffline called");
            final int[] onTrack = {0};
            appModel.videoView.setVideoURI(appModel.uriPlayList.get(onTrack[0]));
            appModel.videoView.start();
            appModel.videoView.setOnCompletionListener(mp -> {
                if (onTrack[0] < appModel.uriPlayList.size() - 1) {
                    onTrack[0]++;
                } else {
                    onTrack[0] = 0;
                }
                appModel.videoView.setVideoURI(appModel.uriPlayList.get(onTrack[0]));
                appModel.videoView.start();
            });
        } else {
            Toast.makeText(context.getApplicationContext(), "Turn on internet Connection!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
