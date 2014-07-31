package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Bruce
 * Data 2014/7/22
 * Time 14:10.
 */
public abstract class BaseSetupActivity extends Activity {
    //定义手势识别器
    public GestureDetector detector;

    protected SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        //实例化手势识别器
        detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            /**
             * 当我们的手指在上面滑动的时候回调
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                    //屏蔽在X滑动很慢的情形

                    if(Math.abs(velocityX)<200){
                        Toast.makeText(getApplicationContext(), "too slowly", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    //屏蔽斜滑这种情况
                    if(Math.abs((e2.getRawY() - e1.getRawY())) > 100){
                        Toast.makeText(getApplicationContext(),"dont sideways cross ",Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    if((e2.getRawX() - e1.getRawX())> 200 ){
                        //显示上一个页面：从左往右滑动
                      //  System.out.println("显示上一个页面：从左往右滑动");
                        showPre();
                        return true;

                    }

                    if((e1.getRawX()-e2.getRawX()) > 200 ){
                        //显示下一个页面：从右往左滑动
                     //   System.out.println("显示下一个页面：从右往左滑动");
                        showNext();
                        return true;
                    }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

    }

    public abstract void showPre();
    public abstract void showNext();

    /**
     * 下一步的点击事件
     * @param view
     */
    public void next(View view){
        showNext();

    }

    /**
     *   上一步
     * @param view
     */
    public void pre(View view){
        showPre();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //使用 手势识别器
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
