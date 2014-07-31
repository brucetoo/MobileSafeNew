package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.itheima.mobilesafe.service.ScreenOffService;
import com.itheima.mobilesafe.utils.ServiceStatusUtils;

/**
 * Created by Bruce
 * Data 2014/7/31
 * Time 11:49.
 */
public class TaskSettingActivity extends Activity{

    private CheckBox cb_show_system;
    private CheckBox cb_clear_mem;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);

        cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
        cb_clear_mem = (CheckBox) findViewById(R.id.cb_clear_mem);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        cb_show_system.setChecked(sp.getBoolean("ischecked",false));
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 //记录选择的状态
                sp.edit().putBoolean("ischecked",isChecked).commit();
            }
        });

        cb_clear_mem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //关屏的时候清理进程，首先要注册广播接受者，然后是用一个服务在后台启动广播接受者
                Intent intent = new Intent(TaskSettingActivity.this, ScreenOffService.class);
                if(isChecked){
                    startService(intent);
                }else{
                    stopService(intent);
                }
            }
        });


    }

    @Override
    protected void onStart() {
        cb_clear_mem.setChecked(ServiceStatusUtils.isServiceRunning(TaskSettingActivity.this,
                                                                    "com.itheima.mobilesafe.service.ScreenOffService"));
        super.onStart();
    }
}
