package com.apps.koru.star8_video_app.objects.RoomDb;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by danielluzgarten on 05/11/2017.
 */

public class LocalDbManger {
    public JsonToRecord jsonToRecord = new JsonToRecord();
    public List<ReportRecord> toreport;
    public ReportsRecDatabase reportsRecDatabase;
    public ReportRecord reportRecord;

    public void insertToReportRecDb (ReportRecord record){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                reportsRecDatabase.reportRecordDao().insertAll(record);
            }
        });
    }
    public void deleteFromRecords (ReportRecord record){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                reportsRecDatabase.reportRecordDao().delete(record);
            }
        });
    }
    public void getRecordsinReportRecs (){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                toreport =  reportsRecDatabase.reportRecordDao().getall();
                System.out.println("@@@@@");
                System.out.println(toreport.size());
            }
        });
    }
}
