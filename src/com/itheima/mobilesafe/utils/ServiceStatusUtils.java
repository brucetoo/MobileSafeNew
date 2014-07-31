package com.itheima.mobilesafe.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.util.List;

public class ServiceStatusUtils {
	//���һ�������Ƿ�������״̬
	public static boolean isServiceRunning(Context context,String serviceName){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(100);
		for (RunningServiceInfo info : infos) {
			String className = info.service.getClassName();
			if(serviceName.equals(className)){
				return true;
			}
		}
		return false;
	}
}
