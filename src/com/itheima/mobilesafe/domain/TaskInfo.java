package com.itheima.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Bruce
 * Data 2014/7/30
 * Time 13:38.
 * 进程信息的业务bean
 */
public class TaskInfo {
    public Drawable icon;
    public String name;
    public String packageName; //唯一标示
    public long memsize; //内存暂用大小
    public boolean userTask; //是否是用户线程

    public boolean checked;// 是否选中
}
