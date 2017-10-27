package com.apps.koru.star8_video_app.objects.dbobjects;

public class PurchaseOrder {
    String startDate;
    String endDate;

    private PurchaseOrder(){}

    public PurchaseOrder(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }
}
