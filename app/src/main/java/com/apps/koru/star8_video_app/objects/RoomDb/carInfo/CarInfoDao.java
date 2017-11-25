package com.apps.koru.star8_video_app.objects.RoomDb.carInfo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by danielluzgarten on 17/11/2017.
 */
@Dao
public interface CarInfoDao {
    @Query("SELECT * FROM carInfo")
    List<CarInfo> getall();
    @Insert
    void insertAll(CarInfo... carInfos);
    @Delete
    void delete(CarInfo carInfo);
}
