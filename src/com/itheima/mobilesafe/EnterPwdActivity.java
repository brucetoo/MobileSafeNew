package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Bruce
 * Data 2014/8/1
 * Time 16:40.
 */
public class EnterPwdActivity extends Activity {
    private EditText et_password;
    private TextView tv_name;
    private ImageView iv_icon;
    private String packname; //应用程序的包名
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);

        Intent intent = getIntent();
        //得到被锁定应用程序的包名
        packname = intent.getStringExtra("packname");
        et_password = (EditText) findViewById(R.id.et_password);

        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo  info = pm.getApplicationInfo(packname, 0);
            tv_name.setText(info.loadLabel(pm));
            iv_icon.setImageDrawable(info.loadIcon(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    /*    <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.HOME" />
        <category android:name="android.intent.category.MONKEY" />*/

        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);

        /**
         * 所有Activity的最小化操作都不会执行 onDestroy方法，只执行 onStop 方法
         * 在按返回键  返回桌面 实际上是在重新 Home的方法，因此执行完后必须是finish()该activity
         */
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void Confirm(View view){
      String pass = et_password.getText().toString().trim();
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this,"please input password!",Toast.LENGTH_LONG).show();
            return;
        }

        if(pass.equals("123")){
            //在关闭activity之前，需要通知看门狗 临时停止保护程序
            //自定义广播，完成组建中的通讯
            Intent intent = new Intent();
            intent.setAction("com.bruce.temstop");
            intent.putExtra("packname",packname);  //发送需要解锁的包名
            sendBroadcast(intent);
            finish();
        }else {
            Toast.makeText(this,"wrong password!",Toast.LENGTH_LONG).show();
        }

    }
}
