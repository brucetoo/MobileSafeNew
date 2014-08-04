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
     * �ֻ��е�Ӧ�ó���
     */
    private List<AppInfo> userInfos;
    /**
     * ϵͳ�е�Ӧ�ó���
     */
    private List<AppInfo> systemInfos;
    /**
     * ��ʾ��Ӧ��״̬��ʾ
     */
    private TextView tv_status;
    /**
     * ��������
     */
    private PopupWindow popupWindow;
    /**
     * ж��
     */
    private LinearLayout ll_uninstall;
    /**
     * ����
     */
    private LinearLayout ll_start;
    /**
     * ����
     */
    private LinearLayout ll_share;
    /**
     * ���������Ŀ
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


        fillData();  //�������

        //������ListViewʱ
        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            //����ʱ����
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
         * ��Ŀ����¼�
         */
        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //�жϵ����λ��
                if (position == 0 || position == userInfos.size() + 1) {
                    return;
                } else if (position <= userInfos.size()) {//�û�����
                    int newposition = position - 1;
                    appInfo = userInfos.get(newposition);
                } else {//ϵͳ����
                    int newposition = position - 1 - userInfos.size() - 1;
                    appInfo = systemInfos.get(newposition);
                }
                // Toast.makeText(AppManagerActivity.this,appInfo.getPackagename(),Toast.LENGTH_SHORT).show();
                closePopWindow();

//                TextView contentView = new TextView(getApplicationContext());
//                contentView.setText(appInfo.getPackagename());
//                contentView.setTextColor(Color.BLACK);
                View contentView = View.inflate(getApplicationContext(), R.layout.popup_app_item, null);

                //��ʼ��LinearLayout
                ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
                ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);

                ll_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_start.setOnClickListener(AppManagerActivity.this);
                ll_share.setOnClickListener(AppManagerActivity.this);
                //��ʼ��popWindow��contentView��ʾҪ��ʾ�Ĵ������ݣ�-2��ʾ�������� wrap_content
                popupWindow = new PopupWindow(contentView, -2, -2);
                //��popWindow��Ҫ��ʾ����Ч�����������ñ���ɫ��TRANSPARENT ͸��ɫ
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[] location = new int[2];
                //��ȡ���view�ڴ����е�λ�ã�������Location��
                view.getLocationInWindow(location);
                //��ʾ����
                //�����ﶨ��Ŀ��ֵ����px ,Ҫת�� dip
                int px = DensityUtil.dip2px(getApplicationContext(), 60);
                popupWindow.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, px, location[1]);
                //���Ŷ���Ч��
                ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
                                                       Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(500);
                //���뵭������Ч��
                AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                aa.setDuration(500);
                //������
                AnimationSet as = new AnimationSet(false);//false��ʾ ���� ��ʾ����
                as.addAnimation(sa);
                as.addAnimation(aa);
                contentView.setAnimation(as);

            }
        });


        //��ListView �������Ƿ���������
        lv_app_manager.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == userInfos.size() + 1) {
                    return true;//true ��ʾ�ضϵ���¼�
                } else if (position <= userInfos.size()) {//�û�����
                    int newposition = position - 1;
                    appInfo = userInfos.get(newposition);
                } else {                                  //ϵͳ����
                    int newposition = position - 1 - userInfos.size() - 1;
                    appInfo = systemInfos.get(newposition);
                }
              ViewHolder holder = (ViewHolder) view.getTag();
            //������ݿ��д��ڣ����Ƴ�����֮���
                if(dao.findApp(appInfo.getPackagename())){
                    dao.deleteApp(appInfo.getPackagename());
                    //�ǵø��½���
                    holder.iv_status.setImageResource(R.drawable.unlock);
                    adapter.notifyDataSetChanged();
                }else{
                    dao.addApp(appInfo.getPackagename());
                    //�ǵø��½���
                    holder.iv_status.setImageResource(R.drawable.lock);
                    adapter.notifyDataSetChanged();
                }

                return true;
            }
        });

    }

    /**
     * ���߳��м�������
     */
    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        //���߳��м�������
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
                //���߳��и��½���
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
        //�ص��ɵ���������
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
     * Ӧ�õķ���
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
     * ж�� ���򣬲�ˢ�� listView
     *
     * @param packagename
     */
    private void uninstallApplication(String packagename) {

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + packagename));

        //����ж�س���ˢ������
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //ˢ������
        fillData();
    }

    /**
     * ͨ��packagename��ȡ����
     *
     * @param packagename
     */
    private void startApplication(String packagename) {
        //����Ӧ�ó�����Ҫ�ҵ��嵥�ļ���PackageManager
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packagename);
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "�Բ���û������Ӧ��", Toast.LENGTH_SHORT).show();
        }

       /* *//**
         *  ��������д����Ӧ�õ�ʱ�� ���������ҵ���Ӧ�����
         *//*
        //����һ��Intent ��ʾӦ�ó�������activity
        Intent intent1 = new Intent();
        intent1.setAction("android.intent.action.MAIN");
        intent1.addCategory("android.intent.category.LAUNCHER");
        //�ҵ����ж�Ӧ����ڳ���
        List<ResolveInfo> infos = pm.queryIntentActivities(intent1, PackageManager.GET_INTENT_FILTERS);
        List<String> packageNames = new ArrayList<String>();
        for(ResolveInfo info : infos){
            //��ȡ��Ӧ�ó���İ���
            String packageName = info.activityInfo.packageName;
            packageNames.add(packageName);
            //������Ǹ���packageName �ҵ���Ӧ��intent Ȼ����������
        }*/
    }

    private class AppManagerAdapter extends BaseAdapter {
        /**
         * �÷����Ƿ���listview���ܸ�����
         * ��listview������item����λ��ҲҪ��
         *
         * @return
         */
        @Override
        public int getCount() {
            return appInfos.size() + 2; //2��ʾ��������item
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
            } else if (position <= userInfos.size()) { //�û�����
                int newposition = position - 1;
                appInfo = userInfos.get(newposition);
            } else { //ϵͳ����
                int newposition = position - 1 - userInfos.size() - 1;
                appInfo = systemInfos.get(newposition);
            }
            /**
             * �˴��� convertView instanceof RelativeLayout �ǳ���Ҫ�������жϵ�ǰ��convertView�Ƿ���
             * listView�еĶ��󣬲����жϻᱬ��ָ�����
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

    /*  android:installLocation="preferExternal" ��װ��ʱ�����Ȱ�װ���ⲿ�洢*/
            if (appInfo.isInRom()) {
                holder.tv_location.setText("Phone Rom; "+"uid:"+appInfo.getUserId());
            } else {
                holder.tv_location.setText("External SD; "+"uid:"+appInfo.getUserId());
            }

            //������ݿ��б����˸ó���������
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
     * �õ�ָ��·�����ļ��д�С
     *
     * @param path
     * @return
     */

    public long getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        long size = statFs.getAvailableBlocks(); //�õ����õĴ��̸���
        statFs.getBlockCount();//�ܹ��Ĵ��̸���
        long count = statFs.getBlockSize();//ÿ�����̵Ĵ�С
        return size * count;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closePopWindow();
    }
}
