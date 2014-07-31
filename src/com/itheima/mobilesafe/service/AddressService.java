package com.itheima.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.itheima.mobilesafe.db.dao.NumberAddressQueryUtils;

/**
 * Created by Bruce
 * Data 2014/7/24
 * Time 10:20.
 */
public class AddressService extends Service {
    /**
     * 监听来电服务
     */

    private TelephonyManager tm;
    private PhoneStateListener phoneStateListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        phoneStateListener = new myPhoneStateListener();
        Toast.makeText(getApplicationContext(), "hehe", Toast.LENGTH_LONG).show();
        //监听电话状态
        tm.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消监听状态
        tm.listen(phoneStateListener,PhoneStateListener.LISTEN_NONE);
        phoneStateListener = null; //销毁Listener
    }

    private class myPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            //state 手机状态   incomingNumner 来电号码
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: //有来电的时候
                    String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
                 //   Log.i("sdfsdfasfasdfasdf-----",address);
                    Toast.makeText(getApplicationContext(),address,Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }
}
