package com.apps.koru.star8_video_app.events;

import java.util.ArrayList;

public class DownloadFilesEvent {
    private ArrayList <String> list;
    public DownloadFilesEvent(ArrayList <String> filesToDownloadList) {
        this.list = filesToDownloadList;
        System.out.println("DownloadFilesEvent Fired!");
    }
    public ArrayList <String> getList() {
        return list;
    }
}
