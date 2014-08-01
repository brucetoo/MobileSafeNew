package com.itheima.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.itheima.mobilesafe.EnterPwdActivity;
import com.itheima.mobilesafe.db.dao.AppLockDao;

import java.util.List;

/**
 * Created by Bruce
 * Data 2014/8/1
 * Time 14:45.
 * <p/>
 * 程序开启的 看门狗
 */
public class WatchDogService extends Service{

    private boolean flag;
    private AppLockDao dao;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        dao = new AppLockDao(this);
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
                    //packname 是表示当前真正运行的程序
                    if(dao.findApp(packname)){ //如果 在数据库中，这锁该程序
                       Intent intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
                        //服务没有任务栈，在开启Activity时需要指定一个
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    //每隔 50mm  取一次
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
