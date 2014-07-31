package com.itheima.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * Created by Bruce
 * Data 2014/7/21
 * Time 19:02.
 */
public class Setup4Activity extends BaseSetupActivity {

    private CheckBox vb_setup4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        vb_setup4 = (CheckBox) findViewById(R.id.vb_setup4);
        vb_setup4.setChecked(true);
        vb_setup4.setText("安全设置完成");
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
    }

    @Override
    public void showNext() {
        sp.edit().putBoolean("configed", true).commit();

        if (!TextUtils.isEmpty(sp.getString("phone", ""))) {

            Intent intent = new Intent(this, LostFindActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
        }else{
            Toast.makeText(this,"安全号码没设置",Toast.LENGTH_LONG).show();
        }


    }

}
