package com.itheima.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import com.itheima.mobilesafe.ui.SettingItemView;

/**
 * Created by Bruce
 * Data 2014/7/21
 * Time 19:02.
 */
public class Setup2Activity extends BaseSetupActivity {
    private SettingItemView siv_setup2_sim;
    private TelephonyManager tm;  //读取手机卡信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        siv_setup2_sim = (SettingItemView) findViewById(R.id.siv_setup2_sim);
        if (TextUtils.isEmpty(sp.getString("sim", null))) {
            siv_setup2_sim.setChecked(true);
        } else {
            siv_setup2_sim.setChecked(false);
        }
        siv_setup2_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                if (siv_setup2_sim.isChecked()) {
                    siv_setup2_sim.setChecked(false);
                    editor.putString("sim", null);
                } else {
                    siv_setup2_sim.setChecked(true);
                    //保存sim卡信息
                    editor.putString("sim", tm.getSimSerialNumber());
                }
                 editor.commit();

            }
        });
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
    }

    @Override
    public void showNext() {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

}
