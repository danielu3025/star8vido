package com.apps.koru.star8_video_app.objects;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by danielluzgarten on 30/12/2017.
 */

public class TimeHendler {
    public Date currentTime ;
    public int houer;
    public int minute;

    public TimeHendler() {
        setTime();
    }

    public Date getCurrentTime() {
        setTime();
        return currentTime;
    }

    public int getHouer() {
        setTime();
        return houer;
    }

    public int getMinute() {
        setTime();
        return minute;
    }

    public void setTime(){
        currentTime = Calendar.getInstance().getTime();
        houer  = currentTime.getHours();
        minute = currentTime.getMinutes();
    }

}

