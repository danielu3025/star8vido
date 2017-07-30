package com.apps.koru.star8_video_app.events;

public class AcseesEvent {
    private final String message;
    public AcseesEvent(String message) {
        this.message = message;
        System.out.println("AcseesEvent Fired!");
    }
    public String getMessage() {
        return message;
    }
}
