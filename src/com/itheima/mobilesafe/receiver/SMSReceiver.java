package com.itheima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import com.itheima.mobilesafe.R;


/**
 * Created by Bruce
 * Data 2014/7/23
 * Time 9:29.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //接收短信
        Object[] obj = (Object[]) intent.getExtras().get("pdus");

        for(Object o : obj){
            //获取每条短信
            SmsMessage message = SmsMessage.createFromPdu((byte[]) o);
            String msg = message.getMessageBody();
            //读取发送者
            String sender = message.getOriginatingAddress();
            if("***".equals(msg)){

                MediaPlayer mp = MediaPlayer.create(context, R.raw.ylzs);
                mp.start();
                mp.setLooping(false); //设置循环
                mp.setVolume(1.0f,1.0f); //设置左右声道为最大
                //终止广播,不让其他程序接收
                abortBroadcast();
            }
        }
    }
}
