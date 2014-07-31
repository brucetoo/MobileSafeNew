package com.itheima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

/**
 * Created by Bruce
 * Data 2014/7/22
 * Time 15:35.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private SharedPreferences sp;
    private TelephonyManager tm;
    @Override
    public void onReceive(Context context, Intent intent) {
        //读取之前保存的信息和对比现在的sim卡信息
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            String sim = sp.getString("sim", null);
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if(sim.equals(tm.getSimSerialNumber())){
                //sim卡没变
            }else{
                //发短信
            }
    }
}
