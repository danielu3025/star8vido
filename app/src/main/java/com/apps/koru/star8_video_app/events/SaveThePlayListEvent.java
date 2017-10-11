package com.apps.koru.star8_video_app.events;

public class SaveThePlayListEvent {
    private final String message;

    public SaveThePlayListEvent(String message) {
        this.message = message;
        System.out.println("PlayOffline Fired!");
    }

    public String getMessage() {
        return message;
    }
}

