package com.apps.koru.star8_video_app.events;


import java.util.ArrayList;

public class DeleteVideosEvent {
    private final ArrayList<String> list;

    public DeleteVideosEvent(ArrayList<String> a_list) {
        this.list = a_list;
        System.out.println("DeleteVideosEvent Fired!");
    }
    public ArrayList<String> getList() {
        return list;
    }
}
