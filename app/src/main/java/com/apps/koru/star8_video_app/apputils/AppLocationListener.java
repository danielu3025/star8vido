package com.apps.koru.star8_video_app.apputils;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by danielluzgarten on 06/12/2017.
 */

public class AppLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location loc) {
        System.out.println("GPS:");
        System.out.println(loc.getLatitude());
        System.out.println(loc.getLongitude());
    }
    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

}
