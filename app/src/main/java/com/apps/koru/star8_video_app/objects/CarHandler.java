package com.apps.koru.star8_video_app.objects;

import android.os.AsyncTask;

import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.AccessEvent;
import com.apps.koru.star8_video_app.objects.RoomDb.carInfo.CarInfo;
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
                                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                                        switch (snap.getKey()){
                                            case "cctv":
                                                cctv = snap.getValue().toString();
                                                break;
                                            case "country":
                                                country = snap.getValue().toString();
                                                break;
                                            case "motorNumber":
                                                motorNumber = snap.getValue().toString();
                                                break;
                                            case "region":
                                                region = snap.getValue().toString();
                                                break;
                                            case "route":
                                                route = snap.getValue().toString();
                                                break;
                                            case "tag":
                                                tag = snap.getValue().toString();
                                                break;
                                            case "tvcode":
                                                tvCode = snap.getValue().toString();
                                                break;
                                            case "type":
                                                type = snap.getValue().toString();
                                                break;
                                        }
                                    }
                                    getRouteName();

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
        appModel.databaseReference.child("Routes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(route)){
                    appModel.databaseReference.child("Routes").child(route).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            route = dataSnapshot.getValue().toString();
                            appModel.carData = true;
                            appModel.localDbManger.insertCarInfo(new CarInfo(carId,tvCode,country,region,route,type,cctv,tag));
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
    public void setCarFromRoom(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (CarInfo info : appModel.localDbManger.getCarInfos()){
                    appModel.localDbManger.carInfosArray.add(info);
                }
                if (appModel.localDbManger.carInfosArray.size()>0){
                    CarInfo carInfo = appModel.localDbManger.carInfosArray.get(0);
                    if (carInfo !=null){
                        carId = carInfo.getVehicle_id();
                        tvCode = carInfo.getTv_code();
                        cctv = carInfo.getCctv();
                        country = carInfo.getCountry();
                        motorNumber = " ";
                        region = carInfo.getRegion();
                        route = carInfo.getRoute();
                        tag = carInfo.getTag();
                        type = carInfo.getType();
                        EventBus.getDefault().post(new AccessEvent("ok"));
                    }
                }

            }
        });

    }
}
