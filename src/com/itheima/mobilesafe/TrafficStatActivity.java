package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Bruce
 * Data 2014/8/4
 * Time 15:24.
 * 流量统计activity
 */
public class TrafficStatActivity extends Activity {
    private ListView lv_traffic;
    private PackageManager pm;
    private List<ApplicationInfo> applicationInfos;
    private TrafficStatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_status);

        lv_traffic = (ListView) findViewById(R.id.lv_traffic);

        pm = getPackageManager();
        //获取所有安装程序的信息
        applicationInfos = pm.getInstalledApplications(0);
        pm = getPackageManager();
        //获取所有安装程序的信息
        applicationInfos = pm.getInstalledApplications(0);

        adapter = new TrafficStatAdapter();
        lv_traffic.setAdapter(adapter);

       /* for (ApplicationInfo info : applicationInfos) {
            int uid = info.uid;
            //存放流量的文件放在   /proc/uid_stat/tcp_rvd(下载) tcp_snd(上传)
            long tx = TrafficStats.getUidTxBytes(uid);// 上传的流量

            long rx = TrafficStats.getUidRxBytes(uid);// 下载的流量
            //如果返回值=-1的话，表示程序没有产生流量，或者是系统不支持流量统计
        }

        TrafficStats.getMobileTxBytes();//获取手机3G或者2G手机上传的流量
        TrafficStats.getMobileRxBytes();//获取手机3G或者2G手机下载的流量

        TrafficStats.getTotalTxBytes();//所以上传的流量，包括wifi 3g....
        TrafficStats.getTotalRxBytes();//所以下载的流量，包括wifi 3g....*/
    }

    private class TrafficStatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return applicationInfos.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(TrafficStatActivity.this, R.layout.list_item_trafficstat, null);
            }

            ViewHolder holder = new ViewHolder();
            holder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
            holder.iv_app_name = (TextView) view.findViewById(R.id.iv_app_name);
            holder.tv_receive_traffic = (TextView) view.findViewById(R.id.tv_receive_traffic);
            holder.tv_send_traffic = (TextView) view.findViewById(R.id.tv_send_traffic);
            holder.tv_total_traffic = (TextView) view.findViewById(R.id.tv_total_traffic);
            view.setTag(holder);

            ApplicationInfo info = applicationInfos.get(position);
            holder.iv_app_icon.setImageDrawable(info.loadIcon(pm));
            holder.iv_app_name.setText(info.loadLabel(pm));
            int uid = info.uid;
            //存放流量的文件放在   /proc/uid_stat/tcp_rvd(下载) tcp_snd(上传)
            long tx = TrafficStats.getUidTxBytes(uid);// 上传的流量

            long rx = TrafficStats.getUidRxBytes(uid);// 下载的流量
            //如果返回值=-1的话，表示程序没有产生流量，或者是系统不支持流量统计
            if (tx != -1) {
                holder.tv_send_traffic.setText("transfer:" + Formatter.formatFileSize(TrafficStatActivity.this, tx));
            } else {
                holder.tv_send_traffic.setText("empty");
            }
            if (rx != -1) {
                holder.tv_receive_traffic.setText("receive:" + Formatter.formatFileSize(TrafficStatActivity.this, rx));
            } else {
                holder.tv_receive_traffic.setText("empty");
            }
            if ((rx + tx) != -2) {
                holder.tv_total_traffic.setText("total:" + Formatter.formatFileSize(TrafficStatActivity.this, rx + tx));
            } else {
                holder.tv_total_traffic.setText("empty");
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

    class ViewHolder {
        ImageView iv_app_icon; //应用图标
        TextView iv_app_name;  //应用名
        TextView tv_send_traffic; //上传流量
        TextView tv_receive_traffic; //下载流量
        TextView tv_total_traffic;//总流量

    }
}
