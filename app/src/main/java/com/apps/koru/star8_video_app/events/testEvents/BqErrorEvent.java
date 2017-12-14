package com.apps.koru.star8_video_app.events.testEvents;

/**
 * Created by danielluzgarten on 02/12/2017.
 */

public class BqErrorEvent {
    String msg;

    public BqErrorEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
