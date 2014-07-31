package com.itheima.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Created by Bruce
 * Data 2014/7/25
 * Time 13:36.
 */
public class CallSmsSafeService extends Service {

    private InnerSmsReciever reciever;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class InnerSmsReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        reciever = new InnerSmsReciever();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(reciever,filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(reciever);
        super.onDestroy();
    }
}
