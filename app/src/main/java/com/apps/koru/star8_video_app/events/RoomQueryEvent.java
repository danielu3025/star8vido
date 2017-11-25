package com.apps.koru.star8_video_app.events;

/**
 * Created by danielluzgarten on 09/11/2017.
 */

public class RoomQueryEvent {
    String msg;

    public RoomQueryEvent(String msg) {
        this.msg = msg;
        System.out.println("RoomQueryEvent fired!");
    }

    public String getMsg() {
        return msg;
    }
}
