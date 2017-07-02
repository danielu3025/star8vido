package com.apps.koru.star8_video_app.events;

/**
 * Created by danielluzgarten on 28/06/2017.
 */

public class PlayThePlayListEvent {
    private final String message;

    public PlayThePlayListEvent(String message) {
        this.message = message;
        System.out.println("PlayThePlayListEvent Fired!");
    }
    public String getMessage() {

        return message;
    }
}
