package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.*;
import com.itheima.mobilesafe.db.dao.AppLockDao;
import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;
import com.itheima.mobilesafe.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Bruce
 * Data 2014/7/28
 * Time 9:39.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {

    private TextView tv_avail_rom;
    private TextView tv_avail_sd;
    private ListView lv_app_manager;
    private LinearLayout ll_loading;
    private List<AppInfo> appInfos;
    private AppManagerAdapter adapter;

    private AppLockDao dao;

    /**
     * 手机中的应用程序
     */
    private List<AppInfo> userInfos;
    /**
     * 系统中的应用程序
     */
    private List<AppInfo> systemInfos;
    /**
     * 显示的应用状态标示
     */
    private TextView tv_status;
    /**
     * 悬浮窗体
     */
    private PopupWindow popupWindow;
    /**
     * 卸载
     */
    private LinearLayout ll_uninstall;
    /**
     * 启动
     */
    private LinearLayout ll_start;
    /**
     * 分享
     */
    private LinearLayout ll_share;
    /**
     * 被点击的条目
     */
    private AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
        tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
        lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_status = (TextView) findViewById(R.id.tv_status);
         dao = new AppLockDao(this);
        long sdsize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        long romsize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());

        tv_avail_rom.setText("SD Avail:" + Formatter.formatFileSize(this, sdsize));
        tv_avail_sd.setText("ROM Avail:" + Formatter.formatFileSize(this, romsize));


        fillData();  //填充数据

        //当滚动ListView时
        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            //滑动时调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                closePopWindow();
                if (userInfos != null && systemInfos != null) {
                    if (firstVisibleItem <= userInfos.size()) {
                        tv_status.setText("user App:" + userInfos.size());
                    } else {
                        tv_status.setText("System App:" + systemInfos.size());
                    }
                }
            }
        });

        /**
         * 条目点击事件
         */
        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断点击的位置
                if (position == 0 || position == userInfos.size() + 1) {
                    return;
                } else if (position <= userInfos.size()) {//用户程序
                    int newposition = position - 1;
                    appInfo = userInfos.get(newposition);
                } else {//系统程序
                    int newposition = position - 1 - userInfos.size() - 1;
                    appInfo = systemInfos.get(newposition);
                }
                // Toast.makeText(AppManagerActivity.this,appInfo.getPackagename(),Toast.LENGTH_SHORT).show();
                closePopWindow();

//                TextView contentView = new TextView(getApplicationContext());
//                contentView.setText(appInfo.getPackagename());
//                contentView.setTextColor(Color.BLACK);
                View contentView = View.inflate(getApplicationContext(), R.layout.popup_app_item, null);

                //初始化LinearLayout
                ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
                ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);

                ll_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_start.setOnClickListener(AppManagerActivity.this);
                ll_share.setOnClickListener(AppManagerActivity.this);
                //初始化popWindow，contentView表示要显示的窗体内容，-2表示包裹内容 wrap_content
                popupWindow = new PopupWindow(contentView, -2, -2);
                //在popWindow中要显示动画效果，必须设置背景色，TRANSPARENT 透明色
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[] location = new int[2];
                //获取点击view在窗口中的位置，保存在Location中
                view.getLocationInWindow(location);
                //显示窗体
                //代码里定义的宽高值都是px ,要转成 dip
                int px = DensityUtil.dip2px(getApplicationContext(), 60);
                popupWindow.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, px, location[1]);
                //缩放动画效果
                ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
                                                       Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(500);
                //淡入淡出动画效果
                AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                aa.setDuration(500);
                //动画集
                AnimationSet as = new AnimationSet(false);//false表示 动画 演示独立
                as.addAnimation(sa);
                as.addAnimation(aa);
                contentView.setAnimation(as);

            }
        });


        //按ListView 长按：是否锁定程序
        lv_app_manager.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == userInfos.size() + 1) {
                    return true;//true 表示截断点击事件
                } else if (position <= userInfos.size()) {//用户程序
                    int newposition = position - 1;
                    appInfo = userInfos.get(newposition);
                } else {                                  //系统程序
                    int newposition = position - 1 - userInfos.size() - 1;
                    appInfo = systemInfos.get(newposition);
                }
              ViewHolder holder = (ViewHolder) view.getTag();
            //如果数据库中存在，则移除，反之添加
                if(dao.findApp(appInfo.getPackagename())){
                    dao.deleteApp(appInfo.getPackagename());
                    //记得更新界面
                    holder.iv_status.setImageResource(R.drawable.unlock);
                    adapter.notifyDataSetChanged();
                }else{
                    dao.addApp(appInfo.getPackagename());
                    //记得更新界面
                    holder.iv_status.setImageResource(R.drawable.lock);
                    adapter.notifyDataSetChanged();
                }

                return true;
            }
        });

    }

    /**
     * 子线程中加载数据
     */
    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        //子线程中加载数据
        new Thread() {
            @Override
            public void run() {
                super.run();
                appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
                userInfos = new ArrayList<AppInfo>();
                systemInfos = new ArrayList<AppInfo>();
                for (AppInfo info : appInfos) {
                    if (info.isUserApp()) {
                        userInfos.add(info);
                    } else {
                        systemInfos.add(info);
                    }
                }
                //主线程中更新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter == null) {
                            adapter = new AppManagerAdapter();
                            lv_app_manager.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                        ll_loading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();
    }

    private void closePopWindow() {
        //关掉旧的悬浮窗体
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_uninstall:
                closePopWindow();
                if (appInfo.isUserApp()) {
                    uninstallApplication(appInfo.getPackagename());
                } else {
                    Toast.makeText(getApplicationContext(), "you dont root ur phone!", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.ll_start:
                closePopWindow();
                startApplication(appInfo.getPackagename());
                break;
            case R.id.ll_share:
                closePopWindow();
                // Toast.makeText(this,appInfo.getName(),Toast.LENGTH_SHORT).show();
                shareApplication(appInfo.getPackagename());
                break;
        }

    }

    /**
     * 应用的分享
     *
     * @param packagename
     */
    private void shareApplication(String packagename) {

        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "a good app introduce to u,no thanks" + appInfo.getName());
        startActivity(intent);
    }

    /**
     * 卸载 程序，并刷新 listView
     *
     * @param packagename
     */
    private void uninstallApplication(String packagename) {

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + packagename));

        //启动卸载程序，刷新数据
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //刷新数据
        fillData();
    }

    /**
     * 通过packagename获取程序
     *
     * @param packagename
     */
    private void startApplication(String packagename) {
        //启动应用程序需要找到清单文件，PackageManager
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packagename);
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "对不起，没法启动应用", Toast.LENGTH_SHORT).show();
        }

       /* *//**
         *  以下是在写桌面应用的时候 变量程序找到对应的入口
         *//*
        //定义一个Intent 表示应用程序的入口activity
        Intent intent1 = new Intent();
        intent1.setAction("android.intent.action.MAIN");
        intent1.addCategory("android.intent.category.LAUNCHER");
        //找到所有对应的入口程序
        List<ResolveInfo> infos = pm.queryIntentActivities(intent1, PackageManager.GET_INTENT_FILTERS);
        List<String> packageNames = new ArrayList<String>();
        for(ResolveInfo info : infos){
            //获取到应用程序的包名
            String packageName = info.activityInfo.packageName;
            packageNames.add(packageName);
            //过后就是根据packageName 找到对应的intent 然后启动程序
        }*/
    }

    private class AppManagerAdapter extends BaseAdapter {
        /**
         * 该方法是返回listview的总个数的
         * 在listview中增加item，该位置也要变
         *
         * @return
         */
        @Override
        public int getCount() {
            return appInfos.size() + 2; //2表示多了两个item
        }

        @Override
        public Object getItem(int position) {
            return appInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            AppInfo appInfo;

            if (position == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("User app:" + userInfos.size());
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == (userInfos.size() + 1)) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("System app:" + systemInfos.size());
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position <= userInfos.size()) { //用户程序
                int newposition = position - 1;
                appInfo = userInfos.get(newposition);
            } else { //系统程序
                int newposition = position - 1 - userInfos.size() - 1;
                appInfo = systemInfos.get(newposition);
            }
            /**
             * 此处的 convertView instanceof RelativeLayout 非常重要。他是判断当前的convertView是否是
             * listView中的对象，不做判断会爆空指针错误
             */
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(AppManagerActivity.this, R.layout.list_item_appinfo, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.iv_app_name);
                holder.tv_location = (TextView) view.findViewById(R.id.iv_app_location);
                holder.iv_status = (ImageView) view.findViewById(R.id.iv_status);
                view.setTag(holder);
            }

            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getName());

    /*  android:installLocation="preferExternal" 安装的时候优先安装到外部存储*/
            if (appInfo.isInRom()) {
                holder.tv_location.setText("Phone Rom; "+"uid:"+appInfo.getUserId());
            } else {
                holder.tv_location.setText("External SD; "+"uid:"+appInfo.getUserId());
            }

            //如果数据库中保存了该程序，这锁定
            if(dao.findApp(appInfo.getPackagename())){
                holder.iv_status.setImageResource(R.drawable.lock);
            }else {
                holder.iv_status.setImageResource(R.drawable.unlock);
            }

            return view;
        }
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_location;
        ImageView iv_icon;
        ImageView iv_status;
    }

    /**
     * 得到指定路径的文件夹大小
     *
     * @param path
     * @return
     */

    public long getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        long size = statFs.getAvailableBlocks(); //得到可用的磁盘个数
        statFs.getBlockCount();//总共的磁盘个数
        long count = statFs.getBlockSize();//每个磁盘的大小
        return size * count;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closePopWindow();
    }
}
