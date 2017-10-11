package com.apps.koru.star8_video_app.events;

public class MissVideosEvent {
    private final String message;
    public MissVideosEvent(String message) {
        this.message = message;
        System.out.println("MissVideosEvent Fired!");
    }
    public String getMessage() {

        return message;
    }
}

