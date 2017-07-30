package com.apps.koru.star8_video_app.downloadclass;

import android.support.annotation.NonNull;
import android.util.Log;

import com.apps.koru.star8_video_app.events.AcseesEvent;
import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.objects.Model;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executor;

public class FirebaseSelector {
    Model appModel = Model.getInstance();
    String devideId =appModel.osId;
    String email = "";String password = "";

    public FirebaseSelector() {
        appModel.mAuth = FirebaseAuth.getInstance();
        appModel.imeiNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(devideId)){

                    appModel.imeiNode.child(devideId).child("car").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            appModel.carId = (String) dataSnapshot.getValue();
                            appModel.carNode.child(appModel.carId).child("playlist").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    appModel.conectToPlayList((String) dataSnapshot.getValue());
                                    EventBus.getDefault().post(new AcseesEvent("ok"));
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
                    EventBus.getDefault().post(new InfoEvent("Device: " + devideId + ", is not Listed!"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    public void newUser(){
        appModel.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Auth", "createUserWithEmail:success");
                            FirebaseUser user = appModel.mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Auth", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }
}
