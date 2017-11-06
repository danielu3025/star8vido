package com.apps.koru.star8_video_app.objects.RoomDb;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonToRecord {
    ReportRecord reportRecord;
    JsonElement root ;

    public ReportRecord getReportRecord(String json , int toTable) {
        root = new JsonParser().parse(json);
        int table = toTable;
        String video_name = root.getAsJsonObject().get("video_name").getAsString();
        String vehicle_id = root.getAsJsonObject().get("vehicle_id").getAsString();
        String tv_code = root.getAsJsonObject().get("tv_code").getAsString();
        String country = root.getAsJsonObject().get("country").getAsString();
        String region = root.getAsJsonObject().get("region").getAsString();
        String route = root.getAsJsonObject().get("route").getAsString();
        String type = root.getAsJsonObject().get("type").getAsString();
        String cctv = root.getAsJsonObject().get("cctv").getAsString();
        String tag = root.getAsJsonObject().get("tag").getAsString();
        String date = root.getAsJsonObject().get("date").getAsString();
        String time = root.getAsJsonObject().get("time").getAsString();
        String comment = root.getAsJsonObject().get("comment").getAsString();
        int status = root.getAsJsonObject().get("status").getAsInt();
        reportRecord = new ReportRecord(table,video_name,vehicle_id,tv_code,country,region,route,type,cctv,tag,date,time,comment,status);

        return reportRecord;
    }
}
