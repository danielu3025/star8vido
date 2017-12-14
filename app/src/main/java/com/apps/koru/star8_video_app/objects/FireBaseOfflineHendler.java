package com.apps.koru.star8_video_app.objects;

import com.apps.koru.star8_video_app.Model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
// * Created by danielluzgarten on 13/12/2017.
 */

public class FireBaseOfflineHendler {
    Model appModel = Model.getInstance();
    DatabaseReference savedList;

    public FireBaseOfflineHendler() {
        savedList = appModel.databaseReference.child("Playlists/boracay/videos");
        savedList.keepSynced(true);
    }

    public void offlinePlaylis(){
        savedList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
