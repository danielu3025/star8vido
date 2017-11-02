package com.apps.koru.star8_video_app.events.downloadsEvents;

/**
 * Created by danielluzgarten on 02/11/2017.
 */

public class DownloadComplateReportEvent {
    String itemName;
    String comment;
    int status;

    public DownloadComplateReportEvent(String itemName, String comment, int status) {
        System.out.println("DownloadComplateReportEvent Fired!");
        this.itemName = itemName;
        this.comment = comment;
        this.status = status;
    }

    public String getItemName() {
        return itemName;
    }

    public String getComment() {
        return comment;
    }

    public int getStatus() {
        return status;
    }
}
