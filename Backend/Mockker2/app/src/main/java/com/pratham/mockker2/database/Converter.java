package com.pratham.mockker2.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

public class Converter {
    @TypeConverter
    public String fromArray(String[] options){
        if(options==null) return null;
        return new Gson().toJson(options);
    }
    @TypeConverter
    public String[] toAray(String data){
        if(data==null) return null;
        return new Gson().fromJson(data,String[].class);
    }
}
