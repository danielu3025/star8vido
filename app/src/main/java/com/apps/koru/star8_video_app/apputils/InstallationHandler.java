package com.apps.koru.star8_video_app.apputils;


import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class InstallationHandler {
    private static String sID = null;
    public static final String INSTALLATION = "INSTALLATION";

    File installation ;


    public File getInstallation() {
        return installation;
    }

    public InstallationHandler(Context context) {
        if (sID == null) {
            installation = new File(context.getFilesDir(), INSTALLATION);
        }
    }

    public void fileHendler(String carCode){
        try {
            if (!installation.exists())
                writeInstallationFile(installation, carCode);
            sID = readInstallationFile(installation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public synchronized static String id(Context context , String carCode) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation,carCode);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }


    public static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation,String carCode) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        out.write(carCode.getBytes());
        out.close();
    }
}
