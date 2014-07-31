package com.itheima.mobilesafe.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Bruce
 * Data 2014/7/29
 * Time 17:20.
 * 系统内存信息的工具类
 */
public class SystemInfoUtils {

    /**
     * 获取手机正在运行的进程数量
     *
     * @return
     */
    public static int getRunningProcessCount(Context context) {
        //PackageManager 相当于程序管理器，管理静态的内容
        //ActivityManager 相当于任务管理器，管理手机活动的信息
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        return infos.size();
    }

    /**
     * 获取手机可用内存
     *
     * @return
     */
    public static long getAvailMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    /**
     * 获取手机总共内存
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getTotalMem(Context context) {
//        //该方法有局限，在低版本运行时会报错！
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(outInfo);
//        return outInfo.totalMem;

        //读取文件的方法去获取内存
        File file = new File("/proc/meminfo");
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();//读取第一行数据    类似   Memtotal:                *****BK
            //解析字符串，保存数字
            StringBuffer sb = new StringBuffer();//存放数字
            for (char c : line.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            return Long.parseLong(sb.toString())*1024;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }
}
