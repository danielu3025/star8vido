package com.apps.koru.star8_video_app.objects.other;

/**
 * Created by danielluzgarten on 09/01/2018.
 */

public class RouteArea {
    String country;
    String destination;
    String name;
    String origin;
    String region;
    String type;

    public RouteArea() {

    }

    public RouteArea(String country, String destination, String name, String origin, String region, String type) {
        this.country = country;
        this.destination = destination;
        this.name = name;
        this.origin = origin;
        this.region = region;
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
