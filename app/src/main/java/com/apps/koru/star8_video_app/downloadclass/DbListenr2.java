package com.apps.koru.star8_video_app.downloadclass;

import com.apps.koru.star8_video_app.events.downloadsEvents.FuterInAdsDownloadEventStage2;
import com.apps.koru.star8_video_app.events.downloadsEvents.FuterInAdsDownloadEventStage3;
import com.apps.koru.star8_video_app.events.downloadsEvents.MissFileEvent;
import com.apps.koru.star8_video_app.objects.AdvertisingObj;
import com.apps.koru.star8_video_app.objects.Model;
import com.apps.koru.star8_video_app.objects.dbobjects.AdFormatAndPo;
import com.apps.koru.star8_video_app.objects.dbobjects.PurchaseOrder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DbListenr2 {
   Model appModel  = Model.getInstance();
   FirebaseDatabase  firebaseDatabase = FirebaseDatabase.getInstance();
   DatabaseReference playlistRef  = firebaseDatabase.getReference("test").child("Playlists").child("playListCodeExample1").child("videos");
   DatabaseReference currentRef  = firebaseDatabase.getReference("test").child("Playlists").child("playListCodeExample1").child("futureIn");
   DatabaseReference poRef  = firebaseDatabase.getReference("test").child("PurchaseOrder");
   DatabaseReference pVideos  = firebaseDatabase.getReference("test").child("Videos");
   DatabaseReference ref =firebaseDatabase.getReference("test").child("Videos") ;
   Date today = new Date() ;
   Date adsDate ;
   DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
   String dateText ="";
   int counter = 0;
   AdFormatAndPo adFormatAndPo;
   PurchaseOrder purchaseOrder;
   ArrayList<String> fullList = new ArrayList<>();



    ArrayList<AdvertisingObj> list = new ArrayList<>();
    ArrayList<AdvertisingObj> toRemove = new ArrayList<>();

   public DbListenr2() {
      //get futurein Stage1
       EventBus.getDefault().register(this);
      currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot ds : dataSnapshot.getChildren()){
               for (DataSnapshot ds2 : ds.getChildren()){
                  list.add(new AdvertisingObj(ds2.getKey(),appModel.videoDir));
               }
            }
            System.out.println(list.size());
            for(AdvertisingObj ad: list){
               ref = pVideos.child(ad.getName());
               ref.addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {
                     adFormatAndPo = dataSnapshot.getValue(AdFormatAndPo.class);
                     ad.setName(ad.getName()+"."+ adFormatAndPo.getFormat());
                     ad.po = adFormatAndPo.getPo();
                     counter++;
                     if (counter == list.size()){
                         counter =0;
                         EventBus.getDefault().post(new FuterInAdsDownloadEventStage2());
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
   @Subscribe
    public void  stage2( FuterInAdsDownloadEventStage2 event){
       for (AdvertisingObj ad : list){
           poRef.child(ad.po).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   purchaseOrder = dataSnapshot.getValue(PurchaseOrder.class);
                   ad.poStartingDate = purchaseOrder.getStartDate();
                   try {
                       adsDate = dateFormat.parse(ad.poStartingDate);
                       long diff = adsDate.getTime() - today.getTime();
                       int daysDiff  = 1+ (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                       if (daysDiff > 3){
                           toRemove.add(ad);
                       }
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }
                   counter++;
                   if (counter == list.size()){
                       if (toRemove.size()>0){
                           list.removeAll(toRemove);
                           toRemove.clear();
                       }
                       EventBus.getDefault().post(new FuterInAdsDownloadEventStage3());
                       counter = 0;
                   }
               }
               @Override
               public void onCancelled(DatabaseError databaseError) {}
           });
       }
   }

   @Subscribe
    public void stage3(FuterInAdsDownloadEventStage3 event){


       playlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               for (DataSnapshot hourList : dataSnapshot.getChildren()){
                   for (DataSnapshot slot : hourList.getChildren()){
                       fullList.add(slot.getValue().toString());
                   }
               }
               for (AdvertisingObj ad : list){
                   fullList.add(ad.getName());
               }

               EventBus.getDefault().post(new MissFileEvent(fullList));
               fullList.clear();

           }
           @Override
           public void onCancelled(DatabaseError databaseError) {}
       });


   }


}