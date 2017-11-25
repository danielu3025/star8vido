package com.apps.koru.star8_video_app.objects.BQ;

import android.util.Log;

import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.RoomQueryEvent;
import com.apps.koru.star8_video_app.objects.RoomDb.reports.ReportRecord;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by danielluzgarten on 09/11/2017.
 */

public class BigQueryReportMangar {
    int size = 0;
    Model appModel = Model.getInstance();
    public  boolean finish = true;
    public  Boolean working = false;
    List<ReportRecord> goingTobereported = null;
    public ArrayList<String> tasks = new ArrayList<>();
    public int num  = 0 ;

    public BigQueryReportMangar() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onQueryFinishEvent(RoomQueryEvent event){
        if (Objects.equals(event.getMsg(),"getRecordsinReportRecs")){
            System.out.println("queryFinished");
            if (appModel.localDbManger.toreport.size()>0){
                System.out.println("toReportList  = 0");
            }
            else {
                System.out.println("toReportList" + appModel.localDbManger.toreport.size());
            }
        }
        else if (Objects.equals(event.getMsg(),"done")){
            size++;
            if (goingTobereported != null) {
                if (size==goingTobereported.size()){
                    goingTobereported.clear();
                    goingTobereported = null;
                    size =0;
                    Log.d("BigQueryReportMangar","report one session");
                }
            }
        }
    }

}
