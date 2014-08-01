package com.itheima.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bruce
 * Data 2014/7/24
 * Time 15:34.
 * 初始化 锁定程序 的数据库
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper {

    /**
     * 创建数据库的构造方法，名字为 applock.db
     *
     * @param context
     */
    public AppLockDBOpenHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    /**
     * 初始化数据库的表结构
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("create table applock (_d integer primary key autoincrement,packname varchar(20))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
