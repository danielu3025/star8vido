package com.apps.koru.star8_video_app.objects.other;

import android.util.Log;

import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.AccessEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by danielluzgarten on 11/10/2017.
 */

public class CarHandler {
    Model appModel = Model.getInstance();
    String carId = "";
    String tvCode = "";
    String cctv = "";
    String country = "";
    String motorNumber = "";
    String region = "";
    String route = "";
    String tag = "";
    String type = "";
    Car car;
    RouteArea routeArea;

    public CarHandler() {
    }

    public String getTvCode() {
        return tvCode;
    }

    public void setTvCode(String tvCode) {
        this.tvCode = tvCode;
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
    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {

        this.carId = carId;
    }

    public void  setCar(){
        DatabaseReference ref =appModel.databaseReference.child("TVCode").child(appModel.tvCode).child("car");
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setCarId(dataSnapshot.getValue().toString());
                DatabaseReference carRef = appModel.databaseReference.child("Cars");
                carRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(carId)){

                            appModel.databaseReference.child("Cars").child(carId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        car = dataSnapshot.getValue(Car.class);
                                        carId = car.getCarNumber();
                                        tvCode = car.getTvcode();
                                        cctv = car.getCctv();
                                        country = car.getCountry();
                                        motorNumber = car.motorNumber;
                                        region = car.getRegion();
                                        tag = car.getTag();
                                        type= car.getType();
                                        route = car.getRoute();
                                        getRouteName();
                                    }catch (Exception e){
                                        Log.d("Setting car Error", e.getMessage());
                                        e.getMessage();
                                    }


                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }
    private void getRouteName(){
        //change here !!!!
        DatabaseReference routsAreaNode  = appModel.databaseReference.child("Routes");
        routsAreaNode.keepSynced(true);
        routsAreaNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(route)){

                    routsAreaNode.child(route).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            routeArea = dataSnapshot.getValue(RouteArea.class);
                            route = routeArea.getName();
                            region = routeArea.getRegion();
                            appModel.carData = true;
                            EventBus.getDefault().post(new AccessEvent("ok"));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
