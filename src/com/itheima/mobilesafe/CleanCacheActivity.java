package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.*;
import android.net.Uri;
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
                        Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
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

    /**
     * 清除所有缓存  利用了android的漏洞：内存暂用太多，通知系统清理内存
     * @param view
     */
    public void cleanAll(View view) {
       // Toast.makeText(getApplicationContext(), "sssssss", Toast.LENGTH_SHORT).show();

        Method[] methods = PackageManager.class.getMethods();
        for(Method method:methods){
            if("freeStorageAndNotify".equals(method.getName())){
                try {
                    method.invoke(pm, Integer.MAX_VALUE, new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName,
                                                      boolean succeeded) throws RemoteException {
                            System.out.println("---------------------------------------"+succeeded);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll_container.removeAllViews();
                                    Toast.makeText(getApplicationContext(),"Clear done!",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
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
            final String packname = pStats.packageName;//包名
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
                            ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
                            //点击进入系统设置界面单独清除缓存
                            //原因是缓存的清除只能是系统应用才能完成，加上权限普通应用也不能清除缓存
                            iv_delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //deleteApplicationCacheFiles
                                    try {
                                        //通过反射调用隐藏的远程服务方法，
                                        Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                                        method.invoke(pm,packname,new StatsObserver());
                                    } catch (Exception e) {
                                        //此处调用方法是会抛出异常，再次启动系统的界面去清除缓存
                                        Intent intent = new Intent();
                                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                                        intent.setData(Uri.parse("package:" + packname));
                                        startActivity(intent);
                                        e.printStackTrace();
                                    }
                                }
                            });
                            ll_container.addView(view, 0);
                        }
                    }
                });

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 清除缓存后调用该方法
     */
    class StatsObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            System.out.println("---------------------------------"+packageName+" is ----"+succeeded);
        }
    }

}
