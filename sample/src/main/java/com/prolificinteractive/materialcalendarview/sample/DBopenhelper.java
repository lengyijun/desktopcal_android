package com.prolificinteractive.materialcalendarview.sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by steven on 2016/1/19.
 */
public class DBopenhelper extends SQLiteOpenHelper {
    public DBopenhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists main_tb(_id integer primary key autoincrement," +
                "st_name text not null,st_val text not null)");
        db.execSQL("insert into main_tb(st_name,st_val) values" +
                "('db_path','/storage/emulated/0/Android/data/nutstore.android/cache/objectcache/1/calendar.db')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
