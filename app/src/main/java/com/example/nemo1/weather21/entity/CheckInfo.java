package com.example.nemo1.weather21.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CheckInfo")
public class CheckInfo {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name =  "_id")
    private Long _id;

    @ColumnInfo(name =  "DataId")
    private Long dataId;

    @ColumnInfo(name =  "Time")
    private String dataTime;

    @ColumnInfo(name =  "Day")
    private String dataDay;

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public String getDataDay() {
        return dataDay;
    }

    public void setDataDay(String dataDay) {
        this.dataDay = dataDay;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }
}
