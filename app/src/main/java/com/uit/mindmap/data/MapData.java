package com.uit.mindmap.data;

import android.graphics.Bitmap;

import java.text.DateFormat;

public class MapData {
    public String name;
    public Bitmap thumbnail;
    public long date;

    public MapData(String name, Bitmap thumbnail, long date){
        this.name=name;
        this.thumbnail=thumbnail;
        this.date=date;
    }

    public String getDate(){
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        return dateFormat.format(date);
    }
}
