package com.apps.koru.star8_video_app.objects.RoomDb.playlists;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by danielluzgarten on 25/11/2017.
 */
@Entity(tableName = "playlistvideorecord" ,primaryKeys = {"name","hour","slot"})
public class PlayListVideoRecord {
    @NonNull
    String name;
    @NonNull
    int hour;
    @NonNull
    int slot;


    int trys;
    File file;
    int doration ;
    public String po ;
    public String poStartingDate;
    public String poEndDate;
    public long bitsize;
    String customer;

    public PlayListVideoRecord(@NonNull String name, @NonNull int hour, @NonNull int slot, int trys, File file, int doration, String po, String poStartingDate, String poEndDate, long bitsize, String customer) {
        this.name = name;
        this.hour = hour;
        this.slot = slot;
        this.trys = trys;
        this.file = file;
        this.doration = doration;
        this.po = po;
        this.poStartingDate = poStartingDate;
        this.poEndDate = poEndDate;
        this.bitsize = bitsize;
        this.customer = customer;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public int getHour() {
        return hour;
    }

    public void setHour(@NonNull int hour) {
        this.hour = hour;
    }

    @NonNull
    public int getSlot() {
        return slot;
    }

    public void setSlot(@NonNull int slot) {
        this.slot = slot;
    }

    public int getTrys() {
        return trys;
    }

    public void setTrys(int trys) {
        this.trys = trys;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getDoration() {
        return doration;
    }

    public void setDoration(int doration) {
        this.doration = doration;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getPoStartingDate() {
        return poStartingDate;
    }

    public void setPoStartingDate(String poStartingDate) {
        this.poStartingDate = poStartingDate;
    }

    public String getPoEndDate() {
        return poEndDate;
    }

    public void setPoEndDate(String poEndDate) {
        this.poEndDate = poEndDate;
    }

    public long getBitsize() {
        return bitsize;
    }

    public void setBitsize(long bitsize) {
        this.bitsize = bitsize;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
