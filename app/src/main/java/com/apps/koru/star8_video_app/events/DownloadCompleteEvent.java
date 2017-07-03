package com.apps.koru.star8_video_app.events;

public class DownloadCompleteEvent {
    private final String message;

    public DownloadCompleteEvent(String message) {
        this.message = message;
        System.out.println("DownloadCompleteEvent Fired!");
    }

    public String getMessage() {
        return message;
    }
}
