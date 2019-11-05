package com.example.nemo1.weather21;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.nemo1.weather21.DAO.CurrentDAO;
import com.example.nemo1.weather21.entity.Current;

@Database(entities = {Current.class},version = 1,exportSchema = false)
public abstract class Data extends RoomDatabase {
    private static Data INSTANCE;
    public abstract CurrentDAO current();
    public static Migration VER_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    public static Data createTable(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    Data.class,"weather.db")
                    .addMigrations(VER_1_2)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
