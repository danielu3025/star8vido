package com.apps.koru.star8_video_app.events.downloadsEvents;

import java.util.ArrayList;

/**
 * Created by danielluzgarten on 14/10/2017.
 */

public class DownloadEventStage0 {
    ArrayList<String> stringArrayList;
    public DownloadEventStage0(ArrayList<String> list) {
        System.out.println("DownloadEventStage0 fired!");
        stringArrayList = list;
    }

    public ArrayList<String> getStringArrayList() {
        return stringArrayList;
    }
}




