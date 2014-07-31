package com.itheima.mobilesafe;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Bruce
 * Data 2014/7/21
 * Time 19:02.
 */
public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showPre() {

    }

    @Override
    public void showNext() {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

}
