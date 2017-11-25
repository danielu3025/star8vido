package com.apps.koru.star8_video_app.objects.RoomDb.playlists;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by danielluzgarten on 25/11/2017.
 */
@Dao
public interface PlayListVideoDao {

    @Query("SELECT * FROM playlistvideorecord")
    List<PlayListVideoRecord> getall();

    @Insert
    void insertAll(PlayListVideoRecord... playListVideoRecords);

    @Delete
    void delete(PlayListVideoRecord playListVideoRecord);

}
