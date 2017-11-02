package com.apps.koru.star8_video_app.downloadclass;


import android.util.Log;

import com.apps.koru.star8_video_app.events.AccessEvent;
import com.apps.koru.star8_video_app.events.DownloadCompleteEvent;
import com.apps.koru.star8_video_app.events.DownloadErrorEvent;
import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.events.MissVideosEvent;
import com.apps.koru.star8_video_app.events.downloadsEvents.DownloadComplateReportEvent;
import com.apps.koru.star8_video_app.events.downloadsEvents.DownloadEventStage0;
import com.apps.koru.star8_video_app.events.downloadsEvents.DownloadEventStage1;
import com.apps.koru.star8_video_app.events.downloadsEvents.DownloadEventStage2;
import com.apps.koru.star8_video_app.events.testEvents.TestDownloadLIstEvent;
import com.apps.koru.star8_video_app.objects.AdvertisingObj;
import com.apps.koru.star8_video_app.Model;



import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


public class FireBaseVideoDownloader2 {

    ArrayList<AdvertisingObj> toRemove;
    Model appModel = Model.getInstance();
    String info  = "###";
    int count = 0;
    boolean errorFlag = false;



    public FireBaseVideoDownloader2() {
        EventBus.getDefault().register(this);
    }
    public  void  downloadStage1(){
        toRemove = new ArrayList<>();
        //for each add - add a the download file target ref
        for (AdvertisingObj ad : appModel.advertisingObjs){
            ad.setStorageReference();
            ad.setMetadataTask();
        }

        //checks That evey file are on found on storage if not remove him from AdvertisingObjs;
        count = 0;
        for (AdvertisingObj ad : appModel.advertisingObjs){

            ad.getMetadataTask().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    ad.setStorageMetadata(task.getResult());
                }
                else {
                    System.out.println("getMeta Error trying to get meta for : " +ad.getName() +" " + task.getException().getMessage() );
                    toRemove.add(ad);
                    EventBus.getDefault().post(new DownloadCompleteEvent("done"));
                }
                count++;
                if (count == appModel.advertisingObjs.size()){
                    EventBus.getDefault().post(new DownloadEventStage2());
                }
            });
        }
    }

    public void downloadStage2(){
        count =0;
        appModel.advertisingObjs.removeAll(toRemove);
        if (appModel.advertisingObjs.size()>0){
            for (AdvertisingObj ad : appModel.advertisingObjs){
                ad.setFileDownloadTask();
                if (ad.getFileDownloadTask() != null){

                    updateInfo("downloading "+ ad.getName());
                    ad.getFileDownloadTask().addOnCompleteListener(task -> {
                        count++;
                        if (task.isSuccessful()){
                            System.out.println("finish downloading " + ad.getName());
                            //send updatePlaylist Event

                            EventBus.getDefault().post(new DownloadCompleteEvent("done"));
                            EventBus.getDefault().post(new DownloadComplateReportEvent(ad.getName(),"Successful",1));
                        }
                        else {
                            errorFlag = true;
                            ad.updateTry();
                            EventBus.getDefault().post(new DownloadCompleteEvent("done"));
                            EventBus.getDefault().post(new DownloadErrorEvent());
                            EventBus.getDefault().post(new DownloadComplateReportEvent(ad.getName(),"Failed",-1));
                            updateInfo("Error while downloading - " + ad.getName());
                            if (ad.getFile().exists()) {
                                Log.d("deleting", "deleting video: " + ad.getFile().getName());
                                try {
                                    ad.getFile().delete();

                                    Log.d("deleting", "successes");
                                } catch (Exception e) {
                                    Log.d("deleting", "Field");
                                    System.out.println(e.getMessage());
                                }
                            }
                            System.out.println("Error downloading " + task.getClass().getName()+" error msg is:\n"+task.getException().getMessage());
                        }
                        if (count == appModel.advertisingObjs.size()){
                            System.out.println(" this download session is done");
                            if (errorFlag){
                                errorFlag = false;
                            }
                            File[] lf = appModel.videoDir.listFiles();
                            ArrayList<String> folderPhats = new ArrayList<>();
                            for (File file :lf){
                                folderPhats.add(file.getAbsolutePath());
                            }
                            ArrayList<String> needTobePaths = new ArrayList<String>();
                            for (AdvertisingObj obj : appModel.advertisingObjs){
                                needTobePaths.add(obj.getFile().getAbsolutePath());
                            }
                            if(folderPhats.containsAll(needTobePaths)) {
                                Log.d("status:", "all files are in storage");
                                EventBus.getDefault().post(new DownloadCompleteEvent("done"));
                                EventBus.getDefault().post(new AccessEvent("setRealTimeListener"));
                            } else {
                                EventBus.getDefault().post(new MissVideosEvent("more downloads"));
                            }

                            appModel.advertisingObjs.clear();

                            updateInfo("done");
                            System.out.println(info);
                            updateInfo("clear");
                        }
                    });

                }
            }
        }
    }

    public void setList(ArrayList<String> names){
        if (appModel.advertisingObjs.size()>0){
            appModel.advertisingObjs.clear();
        }
        for (String fileName : names){
            appModel.advertisingObjs.add(new AdvertisingObj(fileName,appModel.videoDir));
        }
        EventBus.getDefault().post(new DownloadEventStage1());
    }
    private void updateInfo(String txt){
        if (Objects.equals(txt,"clear")){
            info = "###";
        } else {info = info + "\n###" + txt;}
    }

    //events
    @Subscribe
    public void stage0onEvenStage0(DownloadEventStage0 event){
        setList(event.getStringArrayList());
    }
    @Subscribe
    public void stage0onEvenStage1(DownloadEventStage1 event){
        downloadStage1();

    }
    @Subscribe
    public void stage0onEvenStage2(DownloadEventStage2 event){
        downloadStage2();
    }
    @Subscribe
    public void doenloadInformer(TestDownloadLIstEvent event){
        String txt = "Downloads:\n";
        System.out.println(appModel.advertisingObjs.size());
        for (AdvertisingObj ad : appModel.advertisingObjs){
            if (ad.progress != 100){
                txt = txt + ad.getName() + " - " + ad.progress +"%\n";
            }
        }
        EventBus.getDefault().post(new InfoEvent(txt));
    }



}
