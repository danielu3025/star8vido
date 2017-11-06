package com.apps.koru.star8_video_app.objects.RoomDb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by danielluzgarten on 04/11/2017.
 */

@Database(entities = {ReportRecord.class}, version = 1)

public abstract class ReportsRecDatabase extends RoomDatabase {
    private static ReportsRecDatabase INSTANCE;
    public abstract ReportRecordDao reportRecordDao();
}

