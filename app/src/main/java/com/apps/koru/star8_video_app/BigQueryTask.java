package com.apps.koru.star8_video_app;

import android.os.AsyncTask;


/**
 * Created by danielluzgarten on 21/10/2017.
 */

class BigQueryTask1 extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
//        String JSON_CONTENT = params[0];
//        try {
//            AssetManager am = Main.this.getAssets();
//            InputStream isCredentialsFile = am.open(CREDENTIALS_FILE);
//            BigQuery bigquery = BigQueryOptions.builder()
//                    .authCredentials(AuthCredentials.createForJson(isCredentialsFile))
//                    .projectId( PROJECT_ID )
//                    .build().service();
//
//            TableId tableId = TableId.of("android_app", "test");
//            Table table = bigquery.getTable(tableId);
//
//            int num = 0;
//            Log.d("Main", "Sending JSON: " + JSON_CONTENT);
//            WriteChannelConfiguration configuration = WriteChannelConfiguration.builder(tableId)
//                    .formatOptions(FormatOptions.json())
//                    .build();
//            try (WriteChannel channel = bigquery.writer(configuration)) {
//                num = channel.write(ByteBuffer.wrap(JSON_CONTENT.getBytes(StandardCharsets.UTF_8)));
//                channel.close();
//            } catch (IOException e) {
//                Log.d("Main", e.toString());
//            }
//            Log.d("Main", "Loading " + Integer.toString(num) + " bytes into table " + tableId);
//
//        } catch (Exception e) {
//            Log.d("Main", "Exception: " + e.toString());
//        }
        return "Done";

    }

}
