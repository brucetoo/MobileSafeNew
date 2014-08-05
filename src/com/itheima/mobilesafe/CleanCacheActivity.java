package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.pm.*;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.widget.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Bruce
 * Data 2014/8/5
 * Time 14:52.
 * 清除缓存
 */
public class CleanCacheActivity extends Activity {
    private TextView tv_status;   //正在扫描
    private ProgressBar progressBar; //进度条
    private LinearLayout ll_container; //程序名字容器
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        tv_status = (TextView) findViewById(R.id.tv_status);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        scanCache();
    }

    /**
     * 扫描所用应用程序的缓存
     */
    private void scanCache() {
        pm = getPackageManager();
        new Thread() {
            @Override
            public void run() {

                List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
                progressBar.setMax(packageInfos.size());
                int process = 0;
                for (PackageInfo info : packageInfos) {
                    try {
                        //反射方法的调用
                        //需要加入权限
                        // <uses-permission android:name="android.permission.GET_PACKAGE_SIZE"/>
                        Method method = PackageManager.class.getMethod("getPackageSizeInfo",String.class,IPackageStatsObserver.class);
                        method.invoke(pm, info.packageName, new DataObserver());
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    process++;
                    progressBar.setProgress(process);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_status.setText("scan over...");
                    }
                });
            }
        }.start();
    }


    public void cleanAll(View view) {
        Toast.makeText(getApplicationContext(), "sssssss", Toast.LENGTH_SHORT).show();
    }

    /**
     * IPackageStatsObserver 类表示 获取各个应用的缓存信息，是隐藏的API
     * 需要先 反射PackageManager 找到 getPackageSizeInfo方法，invoke时需要
     * 类DataObserver 继承IPackageStatsObserver.Stub
     */
    class DataObserver extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            final long cacheSize = pStats.cacheSize;  //缓存大小
            long dataSize = pStats.dataSize;    //数据大小
            long codeSize = pStats.codeSize;    //代码大小
            String packname = pStats.packageName;//包名
            System.out.println("codeSize = " + codeSize);
            try {
                final ApplicationInfo info = pm.getApplicationInfo(packname, 0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_status.setText("scanning:" + info.loadLabel(pm));
                        if (cacheSize > 0) {  //存在缓存
                           /*TextView tv = new TextView(getApplicationContext());
                            tv.setText(info.loadLabel(pm)+" cacheSize:"+
                                               Formatter.formatFileSize(getApplicationContext(),cacheSize));*/
                            View view = View.inflate(getApplicationContext(), R.layout.list_item_cacheinfo, null);
                            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
                            TextView tv_cache = (TextView) view.findViewById(R.id.tv_cache);
                            iv_icon.setImageDrawable(info.loadIcon(pm));
                            tv_name.setText(info.loadLabel(pm));
                            tv_cache.setText("cache:" + Formatter.formatFileSize(getApplicationContext(), cacheSize));
                            ll_container.addView(view, 0);
                        }
                    }
                });

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

}
