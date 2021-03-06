package com.apps.koru.star8_video_app.objects.RoomDb.reports;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by danielluzgarten on 04/11/2017.
 */

@Dao
public interface ReportRecordDao {
    @Query("SELECT * FROM reportrecords")
    List<ReportRecord>getall();

    @Insert
    void insertAll(ReportRecord... reportRecords);

    @Delete
    void delete(ReportRecord reportRecord);

}
