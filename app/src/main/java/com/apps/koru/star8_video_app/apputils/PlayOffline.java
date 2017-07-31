package com.apps.koru.star8_video_app.apputils;

import android.util.Log;

import com.apps.koru.star8_video_app.events.GetOfflinePlayList;
import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.events.VideoViewEvent;
import com.apps.koru.star8_video_app.objects.Model;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PlayOffline {
    private Model appModel = Model.getInstance();
    public PlayOffline() { EventBus.getDefault().register(this);
    }
    @Subscribe
    public void onEvent(GetOfflinePlayList event) {
        if(appModel.uriPlayList.size()>0&& appModel.dbList.size()>0) {
            Log.d("function", "PlayOffline called");
            EventBus.getDefault().post(new VideoViewEvent());

        } else {
            EventBus.getDefault().post(new InfoEvent("vis"));
            EventBus.getDefault().post(new InfoEvent("Turn on internet Connection!"));
        }
    }
}

