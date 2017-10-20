package com.apps.koru.star8_video_app.objects;


import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;

import com.apps.koru.star8_video_app.events.InfoEvent;
import com.apps.koru.star8_video_app.events.testEvents.TestDownloadLIstEvent;
import com.apps.koru.star8_video_app.events.testEvents.TestplayListEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class AdvertisingObj {
    String name;
    int trys;
    File file ;
    Task<StorageMetadata> metadataTask;
    FileDownloadTask fileDownloadTask;
    StorageReference storageReference;
    StorageMetadata storageMetadata;
    public int progress = 0;
    float transferd=0;
    float total = 0;
    float p = 0;

    public void setName(String name) {
        this.name = name;
    }

    public void setTrys(int trys) {
        this.trys = trys;
    }

    public void setFile(File dir) {
        file = new File(dir.getAbsoluteFile()+"/"+this.name);
    }

    public String getName() {
        return name;
    }

    public int getTrys() {
        return trys;
    }

    public File getFile() {
        return file;
    }

    public AdvertisingObj(String name, File dir) {
        this.name = name;
        trys = 0;
        file = new File(dir.getAbsoluteFile()+"/"+this.name);
        fileDownloadTask = null;
        metadataTask = null;
        storageReference = null;

    }
    public boolean updateTry(){
        if (this.trys <16){
            trys++;
        }
        return false;
    }
    public void initTrys(){
        trys = 0;
    }

    public void setMetadataTask() {
        if (storageReference != null){
            metadataTask = storageReference.getMetadata();
        }
        else {
            System.out.printf("AdvertisingObj: " + name + " storage reference is  null" );
        }
    }

    public void setFileDownloadTask() {
        if (storageReference != null){
            if (trys <16){
                long megAvailable =  ((long)Model.getInstance().stat.getBlockSize() * (long) Model.getInstance().stat.getBlockCount())/ 1048576;
                if (((metadataTask.getResult().getSizeBytes()/1048576)+1) < (megAvailable -500)){
                    fileDownloadTask = storageReference.getFile(file);
                    EventBus.getDefault().post(new InfoEvent("vis"));
                    fileDownloadTask.addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            transferd =  (taskSnapshot.getBytesTransferred()/1048576);
                            if (total > 0){
                                p = (transferd/total)*100;
                                if ((int) p >progress){
                                    progress = (int) p;
                                    //EventBus.getDefault().post(new InfoEvent(name +" "+ progress+"%"));
                                    //System.out.println("%%% " + name + " " +progress +"%");
                                    EventBus.getDefault().post(new TestDownloadLIstEvent());
                                }
                            }
                        }
                    });
                }
                else {
                    fileDownloadTask = null;
                    System.out.println("canceling " +name+ "downloading -"+trys + " device storage is short");
                }
            }
            else {
                fileDownloadTask = null;
                System.out.println("canceling " +name+ "downloading -"+trys + " treys was got on-fail");
            }

        }
        else {
            System.out.printf("AdvertisingObj: " + name + " storage reference is  null" );
        }
    }

    public void setStorageReference() {
        storageReference = Model.getInstance().storage.getReferenceFromUrl(Model.getInstance().storgeUrl).child(name);
    }

    public Task<StorageMetadata> getMetadataTask() {
        return metadataTask;
    }


    public FileDownloadTask getFileDownloadTask() {

        return fileDownloadTask;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public void setMetadataTask(Task<StorageMetadata> metadataTask) {
        this.metadataTask = metadataTask;
    }

    public StorageMetadata getStorageMetadata() {
        return storageMetadata;
    }

    public void setStorageMetadata(StorageMetadata storageMetadata) {
        this.storageMetadata = storageMetadata;
        getMetadataTask().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                total =  metadataTask.getResult().getSizeBytes()/1048576;
            }
        });
    }
}
