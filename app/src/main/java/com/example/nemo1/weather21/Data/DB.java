package com.example.nemo1.weather21.Data;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.nemo1.weather21.DAO.CurrentDAO;
import com.example.nemo1.weather21.entity.CheckInfo;
import com.example.nemo1.weather21.entity.Current;

import java.io.File;
import java.net.URL;

@Database(entities = {Current.class, CheckInfo.class},version = 2,exportSchema = false)
public abstract class DB extends RoomDatabase {
    private static DB INSTANCE;
    public abstract CurrentDAO current();
    private static final String path = Environment.getExternalStorageDirectory()+"/Weather/";
    public static Migration VER_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    public static DB createTable(Context context){
        if(INSTANCE == null){
            File file = new File(path);
            if(!file.isDirectory()){
                file.mkdir();
            }
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    DB.class,file.getAbsolutePath()+"/weather.db")
                    .addMigrations(VER_1_2)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
