package com.apps.koru.star8_video_app.events.downloadsEvents;

import java.util.ArrayList;

/**
 * Created by danielluzgarten on 27/10/2017.
 */

public class MissFileEvent {
    ArrayList<String> list ;

    public MissFileEvent(ArrayList<String> list) {
        this.list = list;
        System.out.println("MissFileEvent fired!");
    }

    public ArrayList<String> getList() {
        return list;
    }
}
