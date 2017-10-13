package com.apps.koru.star8_video_app.objects;


import android.os.Environment;

import java.io.File;

public class AdvertisingObj {
    String name;
    int trys;
    File file ;

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
}
