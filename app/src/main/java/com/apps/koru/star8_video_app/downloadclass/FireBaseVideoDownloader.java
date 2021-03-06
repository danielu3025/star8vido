package com.apps.koru.star8_video_app.downloadclass;

import com.apps.koru.star8_video_app.events.downloadsEvents.DownloadEventStage0;
import com.apps.koru.star8_video_app.objects.other.AdvertisingObj;
import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.DownloadFilesEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * this class job is to download a playlist from fire base storge.
 * the class get a list with event bus and then start an async download
 * if error was happen in the download.. it will skip it and try to download it after he finish.
 * when download fully complete it send an event for playing
 */

public class FireBaseVideoDownloader {
    private int dcount = 0;
    private Boolean erorFlag  = false;
    private Model appModel = Model.getInstance();
    private  String tempText ="";
    ArrayList<AdvertisingObj> advertisingObjs;
    private boolean reDownloadFalg = false;
    public FireBaseVideoDownloader() {

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(DownloadFilesEvent event) {
//        if (appModel.storageRef==null) {
//            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
//            try {
//                for (String fileName : event.getList()) {
//                    long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getBlockCount();
//                    long megAvailable = bytesAvailable / 1048576;
//                    EventBus.getDefault().post(new InfoEvent("invis"));
//                    appModel.storageRef = appModel.storage.getReferenceFromUrl(appModel.storgeUrl).child(fileName);
//                    appModel.storageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
//                        @Override
//                        public void onSuccess(StorageMetadata storageMetadata) {
//                            long fileSizeInMega = storageMetadata.getSizeBytes() / 1048576;
//                            if ((megAvailable + 1)  - 500 >  fileSizeInMega){
//                                EventBus.getDefault().post(new InfoEvent("vis"));
//                                EventBus.getDefault().post(new InfoEvent("Downloading videos :"  +"0/"+event.getList().size()));
//
//                                System.out.println(fileName + " is found");
//                                final File videoFile = new File(appModel.videoDir, fileName);
//                                System.out.println("Downloading file: " + videoFile.getName());
//
//                                FileDownloadTask downloadTask = appModel.storageRef.getFile(videoFile);
//
//                                downloadTask.addOnSuccessListener(taskSnapshot -> {
//                                    Log.d("status",videoFile.getName() + " finish");
//                                    EventBus.getDefault().post(new DownloadCompleteEvent("done"));
//                                });
//                                downloadTask.addOnFailureListener(exception -> {
//                                    EventBus.getDefault().post(new InfoEvent("Download Error"));
//                                    EventBus.getDefault().post(new DownloadErrorEvent());
//
//                                    Log.d("status",videoFile.getName() + " got error");
//
//                                    erorFlag = true;
//                                    if (videoFile.exists()) {
//                                        Log.d("deleting", "deleting video: " + videoFile.getPath());
//                                        try {
//                                            videoFile.delete();
//                                            Log.d("deleting", "successes");
//                                            // dcount ++;
//                                        } catch (Exception e) {
//                                            e.getCause();
//                                            Log.d("deleting", "Field");
//                                        }
//                                    }
//                                    exception.getCause();
//                                });
//                                downloadTask.addOnPausedListener(taskSnapshot -> {
//                                    Log.d("status",videoFile.getName() + " paused");
//                                });
//                                downloadTask.addOnCompleteListener(task -> {
//                                    Log.d("Complete from total", (dcount + 1) + "/" + event.getList().size());
//                                    if (tempText != "Downloading videos :" + (dcount+1) +"/"+event.getList().size()){
//                                        tempText = "Downloading videos :" + (dcount+1) +"/"+event.getList().size();
//                                        EventBus.getDefault().post(new InfoEvent(tempText));
//                                    }
//                                    dcount++;
//                                    if (dcount == event.getList().size()) {
//                                        Log.d("status:", "complete all");
//                                        dcount = 0;
//                                        appModel.downloadFinishd = true;
//                                        if (erorFlag) {
//                                            erorFlag = false;
//                                            Log.d("status:", "some file was field to download re download them");
//
//                                            EventBus.getDefault().post(new MissVideosEvent("problem"));
//                                        } else {
//                                            File[] lf = appModel.videoDir.listFiles();
//                                            ArrayList<String> folderPhats = new ArrayList<>();
//                                            for (File file :lf){
//                                                folderPhats.add(file.getAbsolutePath());
//                                            }
//                                            if(folderPhats.containsAll(appModel.dbList)) {
//                                                Log.d("status:", "all files are in storage");
//                                                EventBus.getDefault().post(new DownloadCompleteEvent("done"));
//                                            } else {
//                                                EventBus.getDefault().post(new MissVideosEvent("more downloads"));
//                                            }
//                                        }
//                                    }
//                                });
//                            }
//                            else {
//                                System.out.println("short memory "  +  fileName + "download canceled" );
//                            }
//                        }
//                    });
//                    appModel.storageRef.getMetadata().addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            System.out.println("file : " + fileName + " not found in Firebase Storage");
//
//                        }
//                    });
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("Main", "IOE Exception");
//            }
//        } else {
//            System.out.println("already downloading");
//        }

        EventBus.getDefault().post(new DownloadEventStage0(event.getList()));
    }
}


