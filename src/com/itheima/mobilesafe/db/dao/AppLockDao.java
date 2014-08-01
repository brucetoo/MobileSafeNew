package com.itheima.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Bruce
 * Data 2014/7/24
 * Time 16:09.
 * 数据库的操作类
 */
public class AppLockDao {
    private AppLockDBOpenHelper helper;

    /**
     * 构造函数中初始化 BlackNumberDBOpenHelper
     */
    public AppLockDao(Context context) {
        helper = new AppLockDBOpenHelper(context);
    }

    /**
     * 增加 要锁定的程序
     *
     * @param packname
     * @return
     */
    public void addApp(String packname) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname", packname);
        db.insert("applock", null, values);
        db.close();
    }

    /**
     * 删除 要锁定的程序
     *
     * @param packname
     * @return
     */
    public void deleteApp(String packname) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("applock", "packname=?", new String[]{packname});
        db.close();
    }


    /**
     * 查询点击的程序是否存在
     *
     * @param packname
     * @return
     */
    public boolean findApp(String packname) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
        while (cursor.moveToNext()) {
                return true;
        }
        cursor.close();;
        db.close();
        return false;
    }

}
