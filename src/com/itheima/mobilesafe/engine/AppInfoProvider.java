package com.itheima.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import com.itheima.mobilesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruce
 * Data 2014/7/28
 * Time 11:06.
 * 应用程序的管理类，提供手机安装的所有安装程序的信息
 */
public class AppInfoProvider {

    /**
     * 获取安装程序的信息
     *
     * @param context
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context) {

        PackageManager pm = context.getPackageManager();
        //获取包的名字 返回一个PackageInfo集合
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        //PackageInfo 相当于获取到清单文件
        for (PackageInfo packageInfo : packageInfos) {
            String packageName = packageInfo.packageName; //获取包名
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm); //获取Icon
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();//获取名字

            AppInfo appInfo = new AppInfo();
            int flag = packageInfo.applicationInfo.flags;//获取应用程序的标示
             /*  android:installLocation="preferExternal" 安装的时候优先安装到外部存储*/
            if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //表示该程序是用户程序
                appInfo.setUserApp(true);
            } else {
                //系统程序
                appInfo.setUserApp(false);
            }
            if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                //表示程序安装在手机中
                appInfo.setInRom(true);
            } else {
                //程序安装在SD卡
                appInfo.setInRom(false);
            }

            appInfo.setIcon(icon);
            appInfo.setPackagename(packageName);
            appInfo.setName(name);
            appInfos.add(appInfo);
        }
        return appInfos;
    }

}
