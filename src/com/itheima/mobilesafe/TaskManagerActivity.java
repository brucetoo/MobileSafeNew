package com.itheima.mobilesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.itheima.mobilesafe.domain.TaskInfo;
import com.itheima.mobilesafe.engine.TaskInfoProvider;
import com.itheima.mobilesafe.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Bruce
 * Data 2014/7/29
 * Time 17:13.
 */
public class TaskManagerActivity extends Activity {
    private TextView tv_process_count;
    private TextView tv_mem_info;

    private LinearLayout ll_loading;
    private ListView lv_task_manager;

    private List<TaskInfo> allTaskInfo; //所有的进程
    private List<TaskInfo> userTaskInfo; //用户进程
    private List<TaskInfo> systemTaskInfo; //系统进程

    private TaskManagerAdapter adapter;
    private TextView tv_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);

        manageTitle();

        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
        tv_status = (TextView) findViewById(R.id.tv_status);
        /**
         * 填充数据
         */
        fillData();

        lv_task_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userTaskInfo != null && systemTaskInfo != null) {
                    if (firstVisibleItem <= userTaskInfo.size()) {
                        tv_status.setText("用户进程： （" + userTaskInfo.size() + ") 个");
                    } else {
                        tv_status.setText("系统进程： （" + systemTaskInfo.size() + "）个");
                    }
                }
            }
        });

        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskInfo taskInfo;
                if (position == 0) {//用户程序标签
                    return;
                } else if (position == userTaskInfo.size() + 1) {
                    return;
                } else if (position <= userTaskInfo.size()) { //用户进程
                    int newposition = position - 1;
                    taskInfo = userTaskInfo.get(newposition);
                } else {                                      //系统进程
                    int newposition = position - 1 - userTaskInfo.size() - 1;
                    taskInfo = systemTaskInfo.get(newposition);
                }

                //当点击自身应用时，屏蔽掉点击事件
                if(getPackageName().equals(taskInfo.packageName)){
                    return;
                }


                //当点击ListView中的每个Item时，可以通过View.getTag()，获取到对象。避免再去创建
                ViewHolder holder = (ViewHolder) view.getTag();
                if (taskInfo.checked) { //如果选中了 Item,设置CheckBOx 没选中
                    taskInfo.checked = false;
                    holder.ck_task_check.setChecked(false);
                } else {
                    taskInfo.checked = true;
                    holder.ck_task_check.setChecked(true);
                }
            }
        });
    }

    private void manageTitle() {
        tv_process_count.setText("运行中的进程:" + SystemInfoUtils.getRunningProcessCount(this) + "个");
        tv_mem_info.setText("剩余/总内存:" + Formatter.formatFileSize(this, SystemInfoUtils.getAvailMem(this)) + "/" +
                                    Formatter.formatFileSize(this, SystemInfoUtils.getTotalMem(this)));
    }

    /**
     * 子线程获取数据
     */
    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                super.run();
                allTaskInfo = TaskInfoProvider.getTaskInfo(getApplicationContext());
                userTaskInfo = new ArrayList<TaskInfo>();
                systemTaskInfo = new ArrayList<TaskInfo>();
                Log.i("TaskInfos -------", String.valueOf(allTaskInfo.size()));
                for (TaskInfo info : allTaskInfo) {
                    //将进程区分成 用户 和 系统
                    if (info.userTask) {
                        userTaskInfo.add(info);
                    } else {
                        systemTaskInfo.add(info);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        if (adapter == null) {
                            adapter = new TaskManagerAdapter();
                            lv_task_manager.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.start();
    }

    /**
     * 进程管理的适配器
     */
    private class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allTaskInfo.size() + 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskInfo taskInfo;
            if (position == 0) {//用户程序标签
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setText("用户进程：（" + userTaskInfo.size() + "）个");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == userTaskInfo.size() + 1) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setText("系统进程： （" + systemTaskInfo.size() + "）个");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position <= userTaskInfo.size()) { //用户进程
                int newposition = position - 1;
                taskInfo = userTaskInfo.get(newposition);
            } else {                                      //系统进程
                int newposition = position - 1 - userTaskInfo.size() - 1;
                taskInfo = systemTaskInfo.get(newposition);
            }

            View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView; //复用对象
                holder = (ViewHolder) view.getTag();
            } else {
                holder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.list_item_taskinfo, null);
                //初始化view中的控件，保存在holder中
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_task_name);
                holder.tv_memsize = (TextView) view.findViewById(R.id.tv_task_memsize);
                holder.ck_task_check = (CheckBox) view.findViewById(R.id.ck_task_check);
                view.setTag(holder);
            }
            //给控件设值
            holder.iv_icon.setImageDrawable(taskInfo.icon);
            holder.tv_name.setText(taskInfo.name);
            holder.tv_memsize.setText(Formatter.formatFileSize(getApplicationContext(), taskInfo.memsize));
            holder.ck_task_check.setChecked(taskInfo.checked);

            //当前条目为自身时，让 checkbox 不可见
            if(getPackageName().equals(taskInfo.packageName)){
                holder.ck_task_check.setVisibility(View.INVISIBLE);
            }else{
                holder.ck_task_check.setVisibility(View.VISIBLE);
            }
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }

    /**
     * 保存View对象，避免重复去findViewById...
     */
    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memsize;
        CheckBox ck_task_check;
    }

    /**
     * 全选
     *
     * @param view
     */
    public void selectAll(View view) {
        for (TaskInfo info : userTaskInfo) {
            if(getPackageName().equals(info.packageName)){
                continue;
            }
            info.checked = true;
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     *
     * @param view
     */
    public void selectOpposite(View view) {
        for (TaskInfo info : userTaskInfo) {
            info.checked = !info.checked;
        }
        adapter.notifyDataSetChanged(); //只是页面变化了 数据没变
    }


    /**
     * 清理
     *
     * @param view
     */
    public void killAll(View view) {

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0; //进程数
        long saveMem = 0; //清理内存数
        //记录被移除的集合
        List<TaskInfo> killedTaskInfo = new ArrayList<TaskInfo>();
        for (TaskInfo info : allTaskInfo) {
            if (info.checked) {
                am.killBackgroundProcesses(info.packageName);
                if (info.userTask) {
                    userTaskInfo.remove(info);
                } else {
                    systemTaskInfo.remove(info);
                }
                count++;
                saveMem += info.memsize;
                killedTaskInfo.add(info);
            }
        }
        adapter.notifyDataSetChanged();
        allTaskInfo.removeAll(killedTaskInfo);
        Toast.makeText(this, "您一共杀死了" + count + "个进程，一共清理内存" + Formatter.formatFileSize(this, saveMem), Toast.LENGTH_SHORT).show();

        tv_process_count.setText("运行中的进程:" + allTaskInfo.size() + "个");
        tv_mem_info.setText("剩余/总内存:" + Formatter.formatFileSize(this, saveMem+SystemInfoUtils.getAvailMem(this)) + "/" +
                                    Formatter.formatFileSize(this, SystemInfoUtils.getTotalMem(this)));
    }

    /**
     * 进入设置
     *
     * @param view
     */
    public void enterSetting(View view) {

    }

}
