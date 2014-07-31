package com.itheima.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bruce
 * Data 2014/7/24
 * Time 15:34.
 */
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

    /**
     * 创建数据库的构造方法，名字为 blacknumber.db
     *
     * @param context
     */
    public BlackNumberDBOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    /**
     * 初始化数据库的表结构
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("create table numberblack (_d integer primary key autoincrement,number varchar(20),mode varchar(2))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
