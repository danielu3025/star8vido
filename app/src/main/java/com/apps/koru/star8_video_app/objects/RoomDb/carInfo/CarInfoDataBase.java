package com.apps.koru.star8_video_app.objects.RoomDb.carInfo;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by danielluzgarten on 17/11/2017.
 */

@Database(entities = {CarInfo.class}, version = 1)
public abstract class CarInfoDataBase extends RoomDatabase {
        private static CarInfoDataBase INSTANCE;
        public abstract CarInfoDao carInfoDao();
}
