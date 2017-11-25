package com.apps.koru.star8_video_app.objects.BQ;

import android.os.AsyncTask;
import android.util.Log;

import com.apps.koru.star8_video_app.Model;
import com.apps.koru.star8_video_app.events.BQSucseesEvent;
import com.google.cloud.AuthCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.WriteChannelConfiguration;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;



public class BigQueryDownloadReport  extends AsyncTask<String, Integer, String> {
    Model appModel = Model.getInstance();
    String JSON_CONTENT;
    int num = 0;
    TableId tableId;
    String stat="Done";
    @Override
    protected String doInBackground(String... params) {
        JSON_CONTENT = params[0];
        try {
            InputStream isCredentialsFile = appModel.assetManager.open(appModel.getCREDENTIALS_FILE());
            BigQuery bigquery = BigQueryOptions.builder().authCredentials(AuthCredentials.createForJson(isCredentialsFile)).projectId(appModel.getPROJECT_ID()).build().service();
            tableId = TableId.of("playedVideos","downloadsTable");
            Table table = bigquery.getTable(tableId);
            num = 0;
            WriteChannelConfiguration configuration = WriteChannelConfiguration.builder(tableId).formatOptions(FormatOptions.json()).build();
            try (WriteChannel channel = bigquery.writer(configuration)) {
                num = channel.write(ByteBuffer.wrap(JSON_CONTENT.getBytes(StandardCharsets.UTF_8)));
                channel.close();
            } catch (IOException e) {
                Log.d("BQ-Download", e.toString());
                stat = "Error";
            }

        } catch (Exception e) {
            Log.d("BQ-Download", "Exception: " + e.toString());
            stat = "Error";
        }
        return stat;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (Objects.equals(s,"Done")){
            Log.d("BQ-Download", "Sending JSON: " + JSON_CONTENT +" into: " + tableId + Integer.toString(num)+" bytes");
            EventBus.getDefault().post(new BQSucseesEvent());

        }
        else {
            Log.d("BQ-Download", "Error Sending JSON: " + JSON_CONTENT);
            appModel.localDbManger.insertToReportRecDb(appModel.jsonToRecord.getReportRecord(JSON_CONTENT,1));
        }
    }
}
