package com.apps.koru.star8_video_app.apputils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.events.VideoViewEvent;
import com.apps.koru.star8_video_app.objects.Model;
import com.apps.koru.star8_video_app.sharedutils.AsyncHandler;
import com.apps.koru.star8_video_app.sharedutils.UiHandler;

import org.greenrobot.eventbus.EventBus;

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
                    appModel.dbList.add(i, sharedPreferences.getString("db_"+i,null));
                    appModel.uriPlayList.add(i, Uri.parse(sharedPreferences.getString("video_"+i, null)));

                }
                Log.e("function", "isfinishloading");
                playOffline();
            });
        });
    }
    private void playOffline(){
        if(appModel.uriPlayList.size()>0&& appModel.dbList.size()>0) {
            Log.d("function", "PlayOffline called");
            EventBus.getDefault().post(new VideoViewEvent());

        } else {
            Toast.makeText(context.getApplicationContext(), "Turn on internet Connection!",
                    Toast.LENGTH_LONG).show();
            EventBus.getDefault().post(new InfoEvent("vis"));
            EventBus.getDefault().post(new InfoEvent("Turn on internet Connection!"));
        }
    }
}

