package com.apps.koru.star8_video_app.events;

public class VideoViewEvent {
    private final String message;
    public VideoViewEvent(String message) {
        this.message = message;
        System.out.println("VideoViewEvent Fired!");
    }
    public String getMessage() {

        return message;
    }
}
