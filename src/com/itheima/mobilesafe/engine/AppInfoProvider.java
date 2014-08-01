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
 * Ӧ�ó���Ĺ����࣬�ṩ�ֻ���װ�����а�װ�������Ϣ
 */
public class AppInfoProvider {

    /**
     * ��ȡ��װ�������Ϣ
     *
     * @param context
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context) {

        PackageManager pm = context.getPackageManager();
        //��ȡ�������� ����һ��PackageInfo����
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        //PackageInfo �൱�ڻ�ȡ���嵥�ļ�
        for (PackageInfo packageInfo : packageInfos) {
            String packageName = packageInfo.packageName; //��ȡ����
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm); //��ȡIcon
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();//��ȡ����

            AppInfo appInfo = new AppInfo();
            int flag = packageInfo.applicationInfo.flags;//��ȡӦ�ó���ı�ʾ
             /*  android:installLocation="preferExternal" ��װ��ʱ�����Ȱ�װ���ⲿ�洢*/
            if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //��ʾ�ó������û�����
                appInfo.setUserApp(true);
            } else {
                //ϵͳ����
                appInfo.setUserApp(false);
            }
            if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                //��ʾ����װ���ֻ���
                appInfo.setInRom(true);
            } else {
                //����װ��SD��
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
