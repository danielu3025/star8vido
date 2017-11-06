package com.apps.koru.star8_video_app.objects.RoomDb;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Created by danielluzgarten on 04/11/2017.
 */

@Entity (tableName = "reportrecords" ,primaryKeys = {"video_name","date","time"})
public class ReportRecord {
    private int table;
    @NonNull
    private String video_name;
    private String vehicle_id;
    private String tv_code;
    private String country;
    private String region;
    private String route;
    private String type;
    private String cctv;
    private String tag;
    @NonNull
    private String date;
    @NonNull
    private String time;
    private String comment;
    private int status;



    public ReportRecord(int table, String video_name, String vehicle_id, String tv_code, String country, String region, String route, String type, String cctv, String tag, String date, String time, String comment, int status) {
        this.table = table;
        this.video_name = video_name;
        this.vehicle_id = vehicle_id;
        this.tv_code = tv_code;
        this.country = country;
        this.region = region;
        this.route = route;
        this.type = type;
        this.cctv = cctv;
        this.tag = tag;
        this.date = date;
        this.time = time;
        this.comment = comment;
        this.status = status;
    }

    public int getTable() {
        return table;
    }

    public void setTable(int table) {
        this.table = table;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getTv_code() {
        return tv_code;
    }

    public void setTv_code(String tv_code) {
        this.tv_code = tv_code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCctv() {
        return cctv;
    }

    public void setCctv(String cctv) {
        this.cctv = cctv;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String toJson(){
        String json =
                "{" +
                        "\"video_name\": " +"\"" +video_name +"\""+ ", " +
                        "\"vehicle_id\": " + "\""+ vehicle_id + "\""+ ","+
                        "\"tv_code\": " + "\""+ tv_code + "\""+ ","+
                        "\"country\": " + "\""+ country + "\""+ ","+
                        "\"region\": " + "\""+ region + "\""+ ","+
                        "\"route\": " + "\""+ route + "\""+ ","+
                        "\"type\": " + "\""+ type + "\""+ ","+
                        "\"cctv\": " + "\""+ cctv + "\""+ ","+
                        "\"tag\": " + "\""+ tag + "\""+ ","+
                        "\"date\": " + "\""+ date + "\""+ ","+
                        "\"time\": " + "\""+ time + "\""+ ","+
                        "\"comment\": " + "\""+ comment + "\""+ ","+
                        "\"status\": "  + status +
                        "}";
        return json;
    }

}
