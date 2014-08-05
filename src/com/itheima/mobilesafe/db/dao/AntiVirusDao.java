package com.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Bruce
 * Data 2014/8/5
 * Time 11:38.
 * 查询病毒数据库
 */
public class AntiVirusDao {
    static String path = "/data/data/com.itheima.mobilesafe/files/antivirus.db";//数据库地址

    public static String isVirus(String md5) {
        String result = "";
        SQLiteDatabase sd = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = sd.rawQuery("select desc from datable where md5=?", new String[]{md5});
        if (cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        cursor.close();
        sd.close();
        return result;
    }
}
