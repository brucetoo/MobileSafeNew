package com.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Bruce
 * Data 2014/7/23
 * Time 16:19.
 */
public class NumberAddressQueryUtils {

    public static String path = "data/data/com.itheima.mobilesafe/files/address.db";

    /**
     * 查询号码归属地数据库
     *
     * @param number
     * @return
     */
    public static String queryNumber(String number) {

        String address = null;
        //path 把数据库拷贝到 data/data/<包名>/files/address.db

        if (number.matches("^1[34568]\\d{9}$")) {
            SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);//只读模式打开数据库
            Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)",
                                                    new String[]{number.substring(0, 7)});

            if (cursor.moveToNext()) {
                String location = cursor.getString(0);
                address = location;
            }
            cursor.close();
        }else{ //其他情况难的写
            address = null;
        }
        return address;
    }
}
