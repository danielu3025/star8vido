package com.apps.koru.star8_video_app.events;


public class InfoEvent {
    private final String message;
    public InfoEvent(String message) {
        this.message = message;
        System.out.println("InfoEvent Fired!");
    }
    public String getMessage() {
        return message;
    }
}
