package com.apps.koru.star8_video_app.objects;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;

public class ReportsHandler {

    private final String CREDENTIALS_FILE = "star8videoApp-1f5da8279871.json";
    private final String PROJECT_ID = "star8videoapp";
    private final int ROW_INTERVAL = 10;
    private String JsonRows = "";
    private int num_rows = 0;

    GoogleCredentials googleCredentials;
    public ReportsHandler() {

    }

    public  void connect () throws IOException {
        // Instantiates a client

    }



}
