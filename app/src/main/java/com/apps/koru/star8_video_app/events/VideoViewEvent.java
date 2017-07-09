package com.apps.koru.star8_video_app.events;

import android.net.Uri;

public class VideoViewEvent {
    private final Uri uri;
    private final String action;
    public VideoViewEvent(Uri uri ,String action) {
        this.uri = uri;
        this.action = action;
        System.out.println("VideoViewEvent Fired!");
    }
    public Uri getUri() {
        return uri;
    }
    public String getAction(){
        return action;
    }
}
