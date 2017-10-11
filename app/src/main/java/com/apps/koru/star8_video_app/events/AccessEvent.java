package com.apps.koru.star8_video_app.events;

public class AccessEvent {
    private final String message;
    public AccessEvent(String message) {
        this.message = message;
        System.out.println("AccessEvent Fired!");
    }
    public String getMessage() {
        return message;
    }
}
