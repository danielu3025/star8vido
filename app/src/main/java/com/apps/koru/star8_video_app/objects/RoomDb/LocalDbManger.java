package com.apps.koru.star8_video_app.objects.RoomDb;

import android.os.AsyncTask;
import android.util.Log;

import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.RoomQueryEvent;
import com.apps.koru.star8_video_app.events.testEvents.TestRoomDbEvent;
import com.apps.koru.star8_video_app.objects.RoomDb.carInfo.CarInfo;
import com.apps.koru.star8_video_app.objects.RoomDb.carInfo.CarInfoDataBase;
import com.apps.koru.star8_video_app.objects.RoomDb.reports.ReportRecord;
import com.apps.koru.star8_video_app.objects.RoomDb.reports.ReportsRecDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielluzgarten on 05/11/2017.
 */

public class LocalDbManger {
    public JsonToRecord jsonToRecord = new JsonToRecord();
    public List<ReportRecord> toreport;
    public ReportsRecDatabase reportsRecDatabase;
    public CarInfoDataBase carInfoDataBase;
    public ReportRecord reportRecord;
    Model appModel = Model.getInstance();
    public  ArrayList<CarInfo> carInfosArray = new ArrayList<>();

    public void insertToReportRecDb (ReportRecord record){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    reportsRecDatabase.reportRecordDao().insertAll(record);
                    EventBus.getDefault().post(new RoomQueryEvent("insertToReportRecDb"));
                    appModel.localDbManger.toreport.add(record);
                    Log.d("ROOM-Query","Report-Record Inserted : " +record.getVideo_name()+","+record.getTime());
                    System.out.println("@@@@ "+ appModel.localDbManger.toreport.size());
                    for (ReportRecord rc :appModel.localDbManger.toreport){
                        System.out.println("@@@@ "+rc.getTime());
                    }
                    EventBus.getDefault().post(new TestRoomDbEvent());

                }
                catch (Exception e){
                    Log.d("ROOM-Query",e.getMessage());
                }
            }
        });
    }
    public void deleteFromRecords (ReportRecord record){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    reportsRecDatabase.reportRecordDao().delete(record);
                    EventBus.getDefault().post(new RoomQueryEvent("deleteFromRecords"));
                    appModel.localDbManger.toreport.remove(record);
                    Log.d("ROOM-Query","Report-Record Deleted : " +record.getVideo_name()+","+record.getTime());
                    System.out.println("@@@@ "+appModel.localDbManger.toreport.size());

                    EventBus.getDefault().post(new TestRoomDbEvent());

                    for (ReportRecord rc :appModel.localDbManger.toreport){
                        System.out.println("@@@@ "+rc.getTime());
                    }


                }catch (Exception e){
                    Log.d("ROOM-Query",e.getMessage());
                }

            }
        });
    }
    public void getRecordsinReportRecs (){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    appModel.localDbManger.toreport =  reportsRecDatabase.reportRecordDao().getall();
                    System.out.println("@@@@@"+appModel.localDbManger.toreport.size());
                    //send finish query Event
                    EventBus.getDefault().post(new RoomQueryEvent("getRecordsinReportRecs"));
                    EventBus.getDefault().post(new TestRoomDbEvent());

                    for (ReportRecord rc :appModel.localDbManger.toreport){
                        System.out.println("@@@@ "+rc.getTime());
                    }

                }catch (Exception e){
                    Log.d("ROOM-Query",e.getMessage());
                }
            }
        });
    }

    public List<ReportRecord> getRecords() {
        return reportsRecDatabase.reportRecordDao().getall();
    }
    public void deleteRecord(ReportRecord record) {
        try {
            reportsRecDatabase.reportRecordDao().delete(record);
            Log.d("deleteRecord","deleting "+ record.getVideo_name() + " "+ record.getTime());
        }catch (Exception e){
            Log.d("deleteRecord","Error deleting "+ record.getVideo_name() + " "+ record.getTime()+"" +" \n"+e.getMessage());
        }
    }


    public void insertCarInfo (CarInfo info){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    carInfoDataBase.carInfoDao().insertAll(info);
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        });
    }
    public List<CarInfo> getCarInfos() {
        try {
            return carInfoDataBase.carInfoDao().getall();
        }catch (Exception e){
            e.getMessage();
            return null;
        }
    }
}
