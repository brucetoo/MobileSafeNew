package com.itheima.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;

/**
 * Created by Bruce
 * Data 2014/8/1
 * Time 14:45.
 * <p/>
 * 程序开启的 看门狗
 */
public class WatchDogService extends Service{

    boolean flag;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        flag = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (flag) {
                    List<ActivityManager.RunningTaskInfo> taskInfos = am.getRunningTasks(100);
                    //获取栈顶的activity程序
                    String packname = taskInfos.get(0).topActivity.getPackageName();
                    System.out.println("packname = " + packname);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.start();
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        flag = false;
        super.onDestroy();
    }
}
