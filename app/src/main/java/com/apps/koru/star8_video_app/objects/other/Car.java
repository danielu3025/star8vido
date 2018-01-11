package com.apps.koru.star8_video_app.objects.other;

/**
 * Created by danielluzgarten on 09/01/2018.
 */


public class Car {
    String carNumber ;
    String tvcode ;
    String cctv ;
    String country ;
    String motorNumber ;
    String region ;
    String route ;
    String tag ;
    String type ;
    String playlist;

    public Car() {
    }

    public Car(String carNumber, String tvcode, String cctv, String country, String motorNumber, String region, String route, String tag, String type, String playlist) {
        this.carNumber = carNumber;
        this.tvcode = tvcode;
        this.cctv = cctv;
        this.country = country;
        this.motorNumber = motorNumber;
        this.region = region;
        this.route = route;
        this.tag = tag;
        this.type = type;
        this.playlist = playlist;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getTvcode() {
        return tvcode;
    }

    public void setTvcode(String tvcode) {
        this.tvcode = tvcode;
    }

    public String getCctv() {
        return cctv;
    }

    public void setCctv(String cctv) {
        this.cctv = cctv;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMotorNumber() {
        return motorNumber;
    }

    public void setMotorNumber(String motorNumber) {
        this.motorNumber = motorNumber;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlaylist() {
        return playlist;
    }

    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }
}

