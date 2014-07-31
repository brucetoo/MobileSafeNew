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

    private List<TaskInfo> allTaskInfo; //���еĽ���
    private List<TaskInfo> userTaskInfo; //�û�����
    private List<TaskInfo> systemTaskInfo; //ϵͳ����

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
         * �������
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
                        tv_status.setText("�û����̣� ��" + userTaskInfo.size() + ") ��");
                    } else {
                        tv_status.setText("ϵͳ���̣� ��" + systemTaskInfo.size() + "����");
                    }
                }
            }
        });

        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskInfo taskInfo;
                if (position == 0) {//�û������ǩ
                    return;
                } else if (position == userTaskInfo.size() + 1) {
                    return;
                } else if (position <= userTaskInfo.size()) { //�û�����
                    int newposition = position - 1;
                    taskInfo = userTaskInfo.get(newposition);
                } else {                                      //ϵͳ����
                    int newposition = position - 1 - userTaskInfo.size() - 1;
                    taskInfo = systemTaskInfo.get(newposition);
                }

                //���������Ӧ��ʱ�����ε�����¼�
                if(getPackageName().equals(taskInfo.packageName)){
                    return;
                }


                //�����ListView�е�ÿ��Itemʱ������ͨ��View.getTag()����ȡ�����󡣱�����ȥ����
                ViewHolder holder = (ViewHolder) view.getTag();
                if (taskInfo.checked) { //���ѡ���� Item,����CheckBOx ûѡ��
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
        tv_process_count.setText("�����еĽ���:" + SystemInfoUtils.getRunningProcessCount(this) + "��");
        tv_mem_info.setText("ʣ��/���ڴ�:" + Formatter.formatFileSize(this, SystemInfoUtils.getAvailMem(this)) + "/" +
                                    Formatter.formatFileSize(this, SystemInfoUtils.getTotalMem(this)));
    }

    /**
     * ���̻߳�ȡ����
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
                    //���������ֳ� �û� �� ϵͳ
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
     * ���̹����������
     */
    private class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allTaskInfo.size() + 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskInfo taskInfo;
            if (position == 0) {//�û������ǩ
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setText("�û����̣���" + userTaskInfo.size() + "����");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == userTaskInfo.size() + 1) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setText("ϵͳ���̣� ��" + systemTaskInfo.size() + "����");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position <= userTaskInfo.size()) { //�û�����
                int newposition = position - 1;
                taskInfo = userTaskInfo.get(newposition);
            } else {                                      //ϵͳ����
                int newposition = position - 1 - userTaskInfo.size() - 1;
                taskInfo = systemTaskInfo.get(newposition);
            }

            View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView; //���ö���
                holder = (ViewHolder) view.getTag();
            } else {
                holder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.list_item_taskinfo, null);
                //��ʼ��view�еĿؼ���������holder��
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_task_name);
                holder.tv_memsize = (TextView) view.findViewById(R.id.tv_task_memsize);
                holder.ck_task_check = (CheckBox) view.findViewById(R.id.ck_task_check);
                view.setTag(holder);
            }
            //���ؼ���ֵ
            holder.iv_icon.setImageDrawable(taskInfo.icon);
            holder.tv_name.setText(taskInfo.name);
            holder.tv_memsize.setText(Formatter.formatFileSize(getApplicationContext(), taskInfo.memsize));
            holder.ck_task_check.setChecked(taskInfo.checked);

            //��ǰ��ĿΪ����ʱ���� checkbox ���ɼ�
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
     * ����View���󣬱����ظ�ȥfindViewById...
     */
    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memsize;
        CheckBox ck_task_check;
    }

    /**
     * ȫѡ
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
     * ��ѡ
     *
     * @param view
     */
    public void selectOpposite(View view) {
        for (TaskInfo info : userTaskInfo) {
            info.checked = !info.checked;
        }
        adapter.notifyDataSetChanged(); //ֻ��ҳ��仯�� ����û��
    }


    /**
     * ����
     *
     * @param view
     */
    public void killAll(View view) {

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0; //������
        long saveMem = 0; //�����ڴ���
        //��¼���Ƴ��ļ���
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
        Toast.makeText(this, "��һ��ɱ����" + count + "�����̣�һ�������ڴ�" + Formatter.formatFileSize(this, saveMem), Toast.LENGTH_SHORT).show();

        tv_process_count.setText("�����еĽ���:" + allTaskInfo.size() + "��");
        tv_mem_info.setText("ʣ��/���ڴ�:" + Formatter.formatFileSize(this, saveMem+SystemInfoUtils.getAvailMem(this)) + "/" +
                                    Formatter.formatFileSize(this, SystemInfoUtils.getTotalMem(this)));
    }

    /**
     * ��������
     *
     * @param view
     */
    public void enterSetting(View view) {

    }

}
