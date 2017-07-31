package com.apps.koru.star8_video_app.events;

import android.content.Context;

public class GetOfflinePlayList {
    private final String message;
    private final Context context;

    public GetOfflinePlayList(String message, Context context) {
        this.context = context;
        this.message = message;
        System.out.println("OfflinePlayList Fired!");
    }

    public Context getContext() {
        return context;
    }
    public String getMessage() {
        return message;
    }
}

