package com.itheima.mobilesafe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.itheima.mobilesafe.db.dao.NumberAddressQueryUtils;

/**
 * Created by Bruce
 * Data 2014/7/23
 * Time 15:53.
 */
public class NumberAddressQueryActivity extends Activity {

    private EditText et_phonenumber;
    private Button bt_query;
    private TextView tv_result;
    private Vibrator vibrator; //系统提供的震动服务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);
        et_phonenumber = (EditText) findViewById(R.id.et_phonenumber);
        bt_query = (Button) findViewById(R.id.bt_query);
        tv_result = (TextView) findViewById(R.id.tv_result);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //对编辑框变化的监听
        et_phonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
               if(charSequence.length()>6){

                   tv_result.setText(NumberAddressQueryUtils.queryNumber(charSequence.toString()));
                   tv_result.setTextColor(Color.RED);
               }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void Query(View view) {

        String number = et_phonenumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_phonenumber.startAnimation(shake);
            long[] patten = {300,300,500,500,1000,1000};
            vibrator.vibrate(patten,-1);
            Toast.makeText(this, "你他么木有输入号码！", Toast.LENGTH_LONG).show();
        } else {
            String address = NumberAddressQueryUtils.queryNumber(number);
            if (TextUtils.isEmpty(address)) {
                tv_result.setText("查无此号！！");
                tv_result.setTextColor(Color.RED);
            } else {
                tv_result.setText(address);
                tv_result.setTextColor(Color.RED);
            }

        }
    }
}
