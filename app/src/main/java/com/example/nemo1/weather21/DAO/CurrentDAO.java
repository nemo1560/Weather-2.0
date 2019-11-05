package com.example.nemo1.weather21.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.nemo1.weather21.entity.CheckInfo;
import com.example.nemo1.weather21.entity.Current;

import java.util.List;

@Dao
public interface CurrentDAO {
    @Insert
    void insertCurrent(Current current);

    @Query("Select max(_id) as _id from CurrentInfo")
    Current getCurrent();

    @Insert
    void insertResult(CheckInfo checkInfo);
}
