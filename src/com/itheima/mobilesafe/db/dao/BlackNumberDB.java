package com.itheima.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruce
 * Data 2014/7/24
 * Time 16:09.
 * 数据库的操作类
 */
public class BlackNumberDB {
    private BlackNumberDBOpenHelper helper;

    /**
     * 构造函数中初始化 BlackNumberDBOpenHelper
     */
    public BlackNumberDB(Context context) {
        helper = new BlackNumberDBOpenHelper(context);
    }

    /**
     * 查询 黑名单的数据是否存在ｎｕｍｂｅｒ
     *
     * @param number
     * @return
     */
    public boolean find(String number) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from numberblack where number = ?", new String[]{number});

        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询all
     *
     * @param number
     * @return
     */
    public List<BlackNumberInfo> findAll() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from numberblack order by _d desc", null);
        while (cursor.moveToNext()) {
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            BlackNumberInfo info = new BlackNumberInfo();
            info.number = number;
            info.mode = mode;
            list.add(info);
        }
        cursor.close();
        db.close();
        return list;
    }


    /**
     * 查询 part
     *
     * @param offset 从哪个位置开始查找数据
     *        maxnum 最多显示多少行
     * @return
     */
    public List<BlackNumberInfo> findPart(int offset,int maxnum) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from numberblack order by _d desc limit ? offset ?",
                                    new String[]{String.valueOf(maxnum),String.valueOf(offset)});
        while (cursor.moveToNext()) {
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            BlackNumberInfo info = new BlackNumberInfo();
            info.number = number;
            info.mode = mode;
            list.add(info);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 添加黑名单号码
     *
     * @param number 号码
     * @param mode   拦截模式
     * @return
     */
    public void insert(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        db.insert("numberblack", null, values);
        db.close();
    }


    /**
     * 修改黑名单号码的模式
     *
     * @param number 号码
     * @param mode   拦截模式
     * @return
     */
    public void update(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db.update("numberblack", values, "number=?", new String[]{number});
        db.close();
    }


    /**
     * 删除黑名单号码
     *
     * @param number 号码
     * @return
     */
    public void delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("numberblack", "number=?", new String[]{number});
        db.close();
    }
}
