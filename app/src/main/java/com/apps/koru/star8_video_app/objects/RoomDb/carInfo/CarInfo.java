package com.apps.koru.star8_video_app.objects.RoomDb.carInfo;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Created by danielluzgarten on 17/11/2017.
 */
@Entity(tableName = "carInfo" ,primaryKeys = {"vehicle_id","tv_code"})
public class CarInfo {
    @NonNull
    String vehicle_id;
    @NonNull
    String tv_code;
    String country;
    String region;
    String route;
    String type;
    String cctv;
    String tag;

    public CarInfo(@NonNull String vehicle_id, @NonNull String tv_code, String country, String region, String route, String type, String cctv, String tag) {
        this.vehicle_id = vehicle_id;
        this.tv_code = tv_code;
        this.country = country;
        this.region = region;
        this.route = route;
        this.type = type;
        this.cctv = cctv;
        this.tag = tag;
    }
    @NonNull
    public String getVehicle_id() {
        return vehicle_id;
    }

    @NonNull
    public String getTv_code() {
        return tv_code;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getRoute() {
        return route;
    }

    public String getType() {
        return type;
    }

    public String getCctv() {
        return cctv;
    }

    public String getTag() {
        return tag;
    }

    public void setVehicle_id(@NonNull String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public void setTv_code(@NonNull String tv_code) {
        this.tv_code = tv_code;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCctv(String cctv) {
        this.cctv = cctv;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
