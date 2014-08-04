package com.itheima.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Bruce
 * Data 2014/7/28
 * Time 10:49.
 * 应用程序的具体信息类
 */
public class AppInfo {

    private Drawable icon; // 应用图标
    private String name;   //应用名
    private String packagename; //应用的报名。唯一标示
    private boolean inRom;  //是否安装到Rom
    private boolean userApp; // 是否是用户的app
    private int userId; //用户的ID 用于获取数据流量

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }
}
