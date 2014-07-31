package com.itheima.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * Created by Bruce
 * Data 2014/7/31
 * Time 14:32.
 */
public class ScreenOffService extends Service {

    private ScreenReciever reciever;
    private ActivityManager am;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        reciever = new ScreenReciever();
        //在服务启动的时候就注册一个广播接受者
        registerReceiver(reciever,new IntentFilter(Intent.ACTION_SCREEN_OFF)); //关屏的广播
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        unregisterReceiver(reciever);
        reciever = null;
    }


    private class ScreenReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到广播后  清理内存
            Log.i("ScreenReciever--------------","开始清理内存,.....");
            List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo info : infos){
                 am.killBackgroundProcesses(info.processName);
            }
        }
    }

}
