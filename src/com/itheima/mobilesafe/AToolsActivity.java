package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by Bruce
 * Data 2014/7/23
 * Time 15:37.
 */
public class AToolsActivity extends Activity {

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    //点击进入查询界面
    public void numberQuery(View view){
        Intent intent = new Intent(this,NumberAddressQueryActivity.class);
        startActivity(intent);
    }

    public void showBar(View view) {

        progressBar.setMax(100);

        new Thread(){
            int i = 1;
            public void run(){
                while(i<100){
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                    progressBar.setProgress(i);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AToolsActivity.this,"done",Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }.start();


    }
}
