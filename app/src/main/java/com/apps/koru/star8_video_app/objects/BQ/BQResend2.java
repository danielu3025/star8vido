package com.apps.koru.star8_video_app.objects.BQ;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.testEvents.TestRoomDbEvent;
import com.apps.koru.star8_video_app.objects.RoomDb.reports.ReportRecord;
import com.google.cloud.AuthCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.WriteChannelConfiguration;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

/**
 * Created by danielluzgarten on 09/11/2017.
 */

public class BQResend2 extends AsyncTask<String, Integer, String> {
    Model appModel = Model.getInstance();
    String JSON_CONTENT;
    int num;
    TableId tableId;
    int TABLE;
    @SuppressLint("LongLogTag")
    @Override
    protected String doInBackground(String... params) {
        String stat = "Done";
        //JSON_CONTENT = params[0];
        TABLE = Integer.parseInt(params[1]);
        ReportRecord rc = appModel.jsonToRecord.getReportRecord(params[0],0);
        try {
            InputStream isCredentialsFile = appModel.assetManager.open(appModel.getCREDENTIALS_FILE());
            BigQuery bigquery = BigQueryOptions.builder().authCredentials(AuthCredentials.createForJson(isCredentialsFile)).projectId(appModel.getPROJECT_ID()).build().service();
            if (TABLE ==0){
                tableId = TableId.of("playedVideos","masterData03");
            }
            else if (TABLE ==1){
                tableId = TableId.of("playedVideos","downloadsTable");
            }
            //tableId = TableId.of("playedVideos","reSends");
            Table table = bigquery.getTable(tableId);

            num = 0;
            JSON_CONTENT= "";
            //WriteChannelConfiguration configuration = WriteChannelConfiguration.builder(tableId).formatOptions(FormatOptions.json()).build();
                WriteChannelConfiguration configuration1 = WriteChannelConfiguration.builder(tableId).formatOptions(FormatOptions.json()).build();
                WriteChannel channel = bigquery.writer(configuration1);
                try {
                    String newRow =
                            "{" +
                                    "\"video_name\": " +"\"" +rc.getVideo_name()+"\""+ ", " +
                                    "\"vehicle_id\": " + "\""+ rc.getVehicle_id() + "\""+ ","+
                                    "\"tv_code\": " + "\""+ rc.getTv_code() + "\""+ ","+
                                    "\"country\": " + "\""+ rc.getCountry() + "\""+ ","+
                                    "\"region\": " + "\""+ rc.getRegion() + "\""+ ","+
                                    "\"route\": " + "\""+ rc.getRoute() + "\""+ ","+
                                    "\"type\": " + "\""+ rc.getType() + "\""+ ","+
                                    "\"cctv\": " + "\""+ rc.getCctv() + "\""+ ","+
                                    "\"tag\": " + "\""+ rc.getTag() + "\""+ ","+
                                    "\"date\": " + "\""+ rc.getDate()+ "\""+ ","+
                                    "\"time\": " + "\""+ rc.getTime() + "\""+ ","+
                                    "\"comment\": " + "\""+ rc.getComment() + "\""+ ","+
                                    "\"status\": "  + rc.getStatus()+
                                    "}";
                    num += channel.write(ByteBuffer.wrap(newRow.getBytes(StandardCharsets.UTF_8)));
                    channel.close();
                    appModel.localDbManger.deleteRecord(rc);
                }catch (Exception e){
                    e.getMessage();
                }

                JSON_CONTENT +=
                        "{" +
                        "\"video_name\": " +"\"" +rc.getVideo_name()+"\""+ ", " +
                        "\"vehicle_id\": " + "\""+ rc.getVehicle_id() + "\""+ ","+
                        "\"tv_code\": " + "\""+ rc.getTv_code() + "\""+ ","+
                        "\"country\": " + "\""+ rc.getCountry() + "\""+ ","+
                        "\"region\": " + "\""+ rc.getRegion() + "\""+ ","+
                        "\"route\": " + "\""+ rc.getRoute() + "\""+ ","+
                        "\"type\": " + "\""+ rc.getType() + "\""+ ","+
                        "\"cctv\": " + "\""+ rc.getCctv() + "\""+ ","+
                        "\"tag\": " + "\""+ rc.getTag() + "\""+ ","+
                        "\"date\": " + "\""+ rc.getDate()+ "\""+ ","+
                        "\"time\": " + "\""+ rc.getTime() + "\""+ ","+
                        "\"comment\": " + "\""+ rc.getComment() + "\""+ ","+
                        "\"status\": "  + rc.getStatus()+
                        "}"+"\n";

            try {
                 appModel.localDbManger.toreport = appModel.localDbManger.getRecords();
            }catch (Exception e){
                Log.d("BQ-ReSend Room Query deleteAllAndUPdate",e.getMessage());
            }

        } catch (Exception e) {
            Log.d("BQ-ReSend", "Exception: " + e.toString());
            stat =  "Error";
        }
        return  stat;
    }

    @Override
    protected void onPostExecute(String s) {
        if (Objects.equals(s,"Done")){
            appModel.bigQueryReportMangar.tasks.add("task");
            Log.d("BQ-ReSend", "Sending JSON: " + JSON_CONTENT +" into: " + tableId + Integer.toString(num)+" bytes");
//            appModel.localDbManger.deleteFromRecords(appModel.jsonToRecord.getReportRecord(JSON_CONTENT,TABLE));
            if (appModel.bigQueryReportMangar.tasks.size()==appModel.bigQueryReportMangar.num ){
                appModel.bigQueryReportMangar.tasks.clear();
                appModel.bigQueryReportMangar.working = false;
                EventBus.getDefault().post(new TestRoomDbEvent());
            }
        }
        else {

            Log.d("BQ-ReSend", "Error Sending JSON: " + JSON_CONTENT);
        }
        EventBus.getDefault().post(new TestRoomDbEvent());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Random rand = new Random();
        int  n = rand.nextInt(100000) + 1;
        if (!appModel.bigQueryReportMangar.working){
            appModel.bigQueryReportMangar.working = true;
            appModel.bigQueryReportMangar.tasks.add("task"+n);
        }
    }
}
