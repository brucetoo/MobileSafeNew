package com.itheima.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by Bruce
 * Data 2014/8/5
 * Time 9:16.
 */
public class AntVirusActivity extends Activity {
    private ImageView iv_scanning;
    private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ant_virus);
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        RotateAnimation ra = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,
                                                 Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE);
        iv_scanning.setAnimation(ra);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setMax(100);
        new Thread(){
            @Override
            public void run() {

                for(int i=0;i<=100;i++){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pb.setProgress(i);
                }
            }
        }.start();
    }
}
