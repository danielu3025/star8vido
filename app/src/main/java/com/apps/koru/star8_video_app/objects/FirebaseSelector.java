package com.apps.koru.star8_video_app.objects;

import com.apps.koru.star8_video_app.events.AccessEvent;
import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.objects.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

public class FirebaseSelector {
    Model appModel = Model.getInstance();
    String devideId =appModel.osId;

    public FirebaseSelector() {
        appModel.mAuth = FirebaseAuth.getInstance();
        appModel.imeiNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(appModel.tvCode)){

                    appModel.imeiNode.child(appModel.tvCode).child("car").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            appModel.carId = (String) dataSnapshot.getValue();
                            appModel.carNode.child(appModel.carId).child("playlist").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    appModel.conectToPlayList((String) dataSnapshot.getValue());
                                    EventBus.getDefault().post(new AccessEvent("ok"));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println(databaseError.getMessage());
                                }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println(databaseError.getMessage());
                        }
                    });
                }
                else {
                    EventBus.getDefault().post(new InfoEvent("vis"));
                    EventBus.getDefault().post(new InfoEvent("Tv code: " + appModel.tvCode + ", is not Listed!"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }
}
