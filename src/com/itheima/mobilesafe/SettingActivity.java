package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import com.itheima.mobilesafe.service.AddressService;
import com.itheima.mobilesafe.ui.SettingItemView;

public class SettingActivity extends Activity {
    //�����Զ�����
    private SettingItemView siv_update;
    //����������ʾ
    private SettingItemView siv_call_show;
    private SharedPreferences sp;

    private Intent intent;

    private WindowManager wm;
    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        siv_update = (SettingItemView) findViewById(R.id.siv_update);


        boolean update = sp.getBoolean("update", false);
        if (update) {
            //�Զ������Ѿ�����
            siv_update.setChecked(true);
            //	siv_update.setDesc("�Զ������Ѿ�����");
        } else {
            //�Զ������Ѿ��ر�
            siv_update.setChecked(false);
            //	siv_update.setDesc("�Զ������Ѿ��ر�");
        }
        siv_update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Editor editor = sp.edit();
                //�ж��Ƿ���ѡ��
                //�Ѿ����Զ�������
                if (siv_update.isChecked()) {
                    siv_update.setChecked(false);
                    //		siv_update.setDesc("�Զ������Ѿ��ر�");
                    editor.putBoolean("update", false);

                } else {
                    //û�д��Զ�����
                    siv_update.setChecked(true);
                    //		siv_update.setDesc("�Զ������Ѿ�����");
                    editor.putBoolean("update", true);
                }
                editor.commit();
            }
        });
        //�����Ƿ��Զ�����
        siv_call_show = (SettingItemView) findViewById(R.id.siv_call_show);
        intent = new Intent(SettingActivity.this, AddressService.class);

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        siv_call_show.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(siv_call_show.isChecked()){ //�����ѽ�����
                    siv_call_show.setChecked(false);
                    //stopService(intent);

                }else{ //û�п�������
                    siv_call_show.setChecked(true);

                    showToast("wo caini ma!");

                 //   startService(intent);
                }
            }

            private void showToast(String s) {
               view = View.inflate(SettingActivity.this,R.layout.address_show,null);
                TextView textView = (TextView) view.findViewById(R.id.tv_address);
                textView.setText(s);
                view.setBackgroundResource(R.drawable.call_locate_blue);

                //���������
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.format = PixelFormat.TRANSLUCENT;
                params.type = WindowManager.LayoutParams.TYPE_TOAST;
                params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                wm.addView(view,params);

                new CountDownTimer(2000,1000){

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        if(view !=null){
                            wm.removeView(view);
                        }
                    }
                }.start();
            }
        });

    }

}
