package com.itheima.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
public class WatchDogService extends Service {

    private boolean flag;      //在服务停止的时候终止
    private AppLockDao dao;    //数据库操作
    private InnerReceiver reciever;  //接收自定义的广播
    private String tempStopPackname;//临时需要解锁的包名

    private ScreenReceiver screenReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //接收自定义的广播
    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到了临时停止保护的广播事件
            // System.out.println("接收到了临时停止保护的广播事件");
            tempStopPackname = intent.getStringExtra("packname");
        }
    }

    //接收锁屏的广播
    private class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //锁屏就清空
            tempStopPackname = null;
        }
    }

    @Override
    public void onCreate() {

        reciever = new InnerReceiver();
        registerReceiver(reciever, new IntentFilter("com.bruce.temstop"));

        screenReceiver = new ScreenReceiver();
        registerReceiver(screenReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF)); //注册关屏的广播

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
                    if (dao.findApp(packname)) { //如果 在数据库中，这锁该程序
                        //广播中需要停止保护的程序包
                        if (!packname.equals(tempStopPackname)) { //不存在，才去保护
                            Intent intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
                            //设置要保护程序的包名
                            intent.putExtra("packname", packname);
                            //服务没有任务栈，在开启Activity时需要指定一个
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
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
        unregisterReceiver(reciever);
        reciever = null;
        unregisterReceiver(screenReceiver);
        screenReceiver = null;
        super.onDestroy();
    }
}
