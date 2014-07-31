package com.itheima.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Bruce
 * Data 2014/7/21
 * Time 19:02.
 */
public class Setup3Activity extends BaseSetupActivity {
    private TextView tv_phone;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_phone.setText(sp.getString("phone",""));
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
    }

    @Override
    public void showNext() {

        sp.edit().putString("phone",phone).commit();
        Intent intent = new Intent(this, Setup4Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    public void chooseContact(View view) {

        Intent intent = new Intent(this, SelectContactActivity.class);
        startActivityForResult(intent, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        phone = data.getStringExtra("phone");
        if(requestCode == 0){
             tv_phone.setText(data.getStringExtra("phone"));
        }
    }
}
