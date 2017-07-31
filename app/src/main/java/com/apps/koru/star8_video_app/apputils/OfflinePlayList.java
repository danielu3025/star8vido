package com.apps.koru.star8_video_app.apputils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.apps.koru.star8_video_app.events.DeleteVideosEvent;
import com.apps.koru.star8_video_app.events.GetOfflinePlayList;
import com.apps.koru.star8_video_app.events.GetToPlayOffline;
import com.apps.koru.star8_video_app.objects.Model;
import com.apps.koru.star8_video_app.sharedutils.AsyncHandler;
import com.apps.koru.star8_video_app.sharedutils.UiHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class OfflinePlayList {
    private Model appModel = Model.getInstance();
    private SharedPreferences sharedPreferences;
    private Context context;

    public OfflinePlayList() {
        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void onEvent(GetOfflinePlayList event) {
        context = event.getContext();
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
            });
        });
    }
}
