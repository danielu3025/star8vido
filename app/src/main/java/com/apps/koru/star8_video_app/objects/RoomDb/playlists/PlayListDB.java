package com.apps.koru.star8_video_app.objects.RoomDb.playlists;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by danielluzgarten on 25/11/2017.
 */
@Database(entities = {PlayListVideoRecord.class}, version = 1)

public abstract class PlayListDB  extends RoomDatabase {
        private static PlayListDB INSTANCE;
        public abstract PlayListVideoDao playListVideoDao();
}
