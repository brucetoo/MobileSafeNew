package com.itheima.mobilesafe.engine;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;
import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruce
 * Data 2014/7/30
 * Time 13:38.
 * 获取进程信息的提供类
 */
public class TaskInfoProvider {

    /**
     * 获取进程信息
     *
     * @param context
     * @return
     */
    public static List<TaskInfo> getTaskInfo(Context context) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        Log.i("getTaskInfo----",String.valueOf(processInfos.size()));
        for (RunningAppProcessInfo info : processInfos) {

            TaskInfo taskInfo = new TaskInfo();
            //获取应用程序的包名
            String packageName = info.processName;
            taskInfo.packageName = packageName;
            //获取进程的大小,通过进程Id
            Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{info.pid});
            long mensize = memoryInfos[0].getTotalPrivateDirty() * 1024;//因为返回值是KB 所以要乘1024便于运算
            taskInfo.memsize = mensize;
            //获取应用程序的icon 和 名字
            try {
                //获取ApplicationInfo
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                Drawable icon = appInfo.loadIcon(pm);
                String name = appInfo.loadLabel(pm).toString();
                //保存对象
                taskInfo.icon = icon;
                taskInfo.name = name;
                //判断是系统进程还是用户进程
                if ((ApplicationInfo.FLAG_SYSTEM & appInfo.flags) == 0) {
                    //用户进程
                    taskInfo.userTask = true;
                } else {
                    //系统进程
                    taskInfo.userTask = false;
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                //在抛出没有名字的异常时需要设置默认的
                taskInfo.icon = context.getResources().getDrawable(R.drawable.ic_launcher);
                taskInfo.name = packageName;
            }
            taskInfos.add(taskInfo);
        }

        return taskInfos;
    }

}
